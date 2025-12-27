package com.erval.argos.mongo;

import java.util.List;
import java.util.Optional;

import com.erval.argos.core.application.PageRequest;
import com.erval.argos.core.application.PageResult;
import com.erval.argos.core.application.SortDirection;
import com.erval.argos.core.application.port.in.queries.MeasurementQueryUseCase.MeasurementFilter;
import com.erval.argos.core.application.port.out.MeasurementRepositoryPort;
import com.erval.argos.core.domain.measurement.Measurement;
import com.erval.argos.mongo.model.MeasurementDocument;
import com.erval.argos.mongo.repositories.MeasurementMongoRepository;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * MongoDB-backed adapter for measurement persistence.
 * <p>
 * Provides:
 * <ul>
 * <li>filtering by device, type, and timestamp ranges</li>
 * <li>paged retrieval with caller-driven sorting</li>
 * <li>conversion between domain aggregates and Mongo documents</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class MongoMeasurementRepositoryAdapter implements MeasurementRepositoryPort {
    private final MeasurementMongoRepository repo;
    private final MongoTemplate mongoTemplate;

    /**
     * Removes all measurements belonging to a device.
     *
     * @param deviceId device identifier to delete measurements for
     */
    @Override
    public void deleteByDeviceId(String deviceId) {
        Query query = new Query(Criteria.where("deviceId").is(deviceId));
        mongoTemplate.remove(query, MeasurementDocument.class);
    }

    /**
     * Executes a paginated search applying filter criteria and sorting
     * instructions.
     * <p>
     * Defaults to sorting by {@code timestamp} when the caller provides no sort
     * field.
     *
     * @param filter      constraints like device id, type, or time interval
     * @param pageRequest paging and sorting instructions
     * @return a page of domain measurements
     */
    @Override
    public PageResult<Measurement> findByFilter(MeasurementFilter filter, PageRequest pageRequest) {
        Query query = new Query();

        if (filter != null) {
            if (filter.deviceId() != null && !filter.deviceId().isBlank()) {
                query.addCriteria(Criteria.where("deviceId").is(filter.deviceId()));
            }
            if (filter.type() != null) {
                query.addCriteria(Criteria.where("type").is(filter.type()));
            }
            Criteria ts = null;
            if (filter.from() != null) {
                ts = Criteria.where("timestamp").gte(filter.from());
            }
            if (filter.to() != null) {
                ts = (ts == null ? Criteria.where("timestamp") : ts).lte(filter.to());
            }
            if (ts != null) {
                query.addCriteria(ts);
            }
        }

        Sort sort = Sort.by(
                pageRequest.direction() == SortDirection.ASC
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC,
                pageRequest.sortBy() != null && !pageRequest.sortBy().isBlank()
                        ? pageRequest.sortBy()
                        : "timestamp");

        org.springframework.data.domain.PageRequest pageable = org.springframework.data.domain.PageRequest
                .of(pageRequest.page(), pageRequest.size(), sort);

        long total = mongoTemplate.count(query, MeasurementDocument.class);
        List<MeasurementDocument> docs = mongoTemplate.find(query.with(pageable), MeasurementDocument.class);

        List<Measurement> content = docs.stream()
                .map(MeasurementDocument::toDomain)
                .toList();

        return new PageResult<>(
                content,
                total,
                pageRequest.page(),
                pageRequest.size());
    }

    /**
     * Saves a measurement aggregate by persisting a Mongo document.
     *
     * @param measurement aggregate to persist
     * @return saved domain measurement
     */
    @Override
    public Measurement save(Measurement measurement) {
        MeasurementDocument saved = repo.save(MeasurementDocument.fromDomain(measurement));
        return saved.toDomain();
    }

    @Override
    /**
     * Deletes a measurement by its id.
     *
     * @param id measurement identifier
     */
    public void deleteById(String id) {
        repo.deleteById(id);
    }

    /**
     * Retrieves all measurements with paging and sorting.
     *
     * @param pageRequest paging and sorting instructions
     * @return paged measurements
     */
    @Override
    public PageResult<Measurement> findAll(PageRequest pageRequest) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                pageRequest.page(),
                pageRequest.size(),
                pageRequest.direction() == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC,
                pageRequest.sortBy() != null && !pageRequest.sortBy().isBlank() ? pageRequest.sortBy() : "timestamp");

        Page<MeasurementDocument> page = repo.findAll(pageable);
        List<Measurement> content = page.getContent().stream()
                .map(MeasurementDocument::toDomain)
                .toList();

        return new PageResult<>(
                content,
                page.getTotalElements(),
                page.getNumber(),
                page.getSize());
    }

    @Override
    /**
     * Retrieves a measurement by id.
     *
     * @param id measurement identifier
     * @return optional measurement if found
     */
    public Optional<Measurement> findById(String id) {
        return repo.findById(id).map(MeasurementDocument::toDomain);
    }

    /**
     * Deletes all measurements in the repository.
     */
    @Override
    public void deleteAll() {
        repo.deleteAll();
    }

}
