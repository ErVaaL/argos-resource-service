package com.erval.argos.mongo;

import com.erval.argos.core.application.PageRequest;
import com.erval.argos.core.application.PageResult;
import com.erval.argos.core.application.SortDirection;
import com.erval.argos.core.application.port.in.queries.DeviceQueryUseCase;
import com.erval.argos.core.application.port.out.DeviceRepositoryPort;
import com.erval.argos.core.domain.device.Device;
import com.erval.argos.mongo.model.DeviceDocument;
import com.erval.argos.mongo.repositories.DeviceMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB-backed adapter implementing {@link DeviceRepositoryPort}.
 * <p>
 * Responsibilities include:
 * <ul>
 * <li>translating domain filters and pagination into Mongo queries</li>
 * <li>converting between documents and domain aggregates</li>
 * <li>delegating persistence to Spring Data repositories</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class MongoDeviceRepositoryAdapter implements DeviceRepositoryPort {

    private final DeviceMongoRepository repo;
    private final MongoTemplate mongoTemplate;

    /**
     * Deletes a device by id, ignoring missing rows.
     *
     * @param id device identifier
     */
    @Override
    public void deleteById(String id) {
        repo.deleteById(id);
    }

    /**
     * Deletes all devices.
     */
    @Override
    public void deleteAll() {
        repo.deleteAll();
    }

    /**
     * Retrieves all devices using paging and sorting.
     *
     * @param pageRequest paging and sorting instructions
     * @return paged devices
     */
    @Override
    public PageResult<Device> findAll(PageRequest pageRequest) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                pageRequest.page(),
                pageRequest.size(),
                pageRequest.direction() == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC,
                pageRequest.sortBy() != null && !pageRequest.sortBy().isBlank() ? pageRequest.sortBy() : "name");

        Page<DeviceDocument> page = repo.findAll(pageable);
        List<Device> content = page.getContent().stream()
                .map(DeviceDocument::toDomain)
                .toList();

        return new PageResult<>(content, page.getTotalElements(), page.getNumber(), page.getSize());
    }

    /**
     * Applies filtering, sorting, and pagination in MongoDB, returning a typed
     * page.
     * <p>
     * When the filter is {@code null}, all devices are considered.
     * <p>
     * Sort defaults to {@code name} in descending order when a field is not
     * provided.
     *
     * @param filter      constraints such as building or activation status
     * @param pageRequest paging and sorting information from the caller
     * @return a page result with content and total count
     */
    @Override
    public PageResult<Device> findByFilter(DeviceQueryUseCase.DeviceFilter filter, PageRequest pageRequest) {
        Query query = new Query();

        if (filter != null) {
            if (filter.building() != null && !filter.building().isBlank()) {
                query.addCriteria(Criteria.where("building").is(filter.building()));
            }
            if (filter.room() != null && !filter.room().isBlank()) {
                query.addCriteria(Criteria.where("room").is(filter.room()));
            }
            if (filter.type() != null) {
                query.addCriteria(Criteria.where("type").is(filter.type()));
            }
            if (filter.active() != null) {
                query.addCriteria(Criteria.where("active").is(filter.active()));
            }
        }

        Sort sort = Sort.by(
                pageRequest.direction() == SortDirection.ASC
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC,
                pageRequest.sortBy() != null && pageRequest.sortBy().isBlank()
                        ? pageRequest.sortBy()
                        : "name");

        org.springframework.data.domain.PageRequest pageable = org.springframework.data.domain.PageRequest
                .of(pageRequest.page(), pageRequest.size(), sort);

        long total = mongoTemplate.count(query, DeviceDocument.class);
        List<DeviceDocument> docs = mongoTemplate.find(query.with(pageable), DeviceDocument.class);

        List<Device> content = docs.stream()
                .map(DeviceDocument::toDomain)
                .toList();

        return new PageResult<>(content, total, pageRequest.page(), pageRequest.size());
    }

    /**
     * Retrieves a device by id.
     *
     * @param id device identifier
     * @return optional device mapped from the stored document
     */
    @Override
    public Optional<Device> findById(String id) {
        return repo.findById(id).map(DeviceDocument::toDomain);
    }

    /**
     * Persists a device aggregate by mapping it to a Mongo document.
     *
     * @param device domain aggregate to save
     * @return saved device with any generated fields applied
     */
    @Override
    public Device save(Device device) {
        repo.findByName(device.name()).ifPresent(existing -> {
            boolean isSame = device.id() != null && device.id().equals(existing.getId());
            if (!isSame) {
                throw new DataIntegrityViolationException("Device name already exists: " + device.name());
            }
        });
        DeviceDocument saved = repo.save(DeviceDocument.fromDomain(device));
        return saved.toDomain();
    }

}
