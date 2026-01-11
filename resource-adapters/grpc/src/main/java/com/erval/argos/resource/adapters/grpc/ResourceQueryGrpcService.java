package com.erval.argos.resource.adapters.grpc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.erval.argos.contracts.resource.v1.Device;
import com.erval.argos.contracts.resource.v1.GetDeviceRequest;
import com.erval.argos.contracts.resource.v1.GetDeviceResponse;
import com.erval.argos.contracts.resource.v1.GetLastMeasurementsRequest;
import com.erval.argos.contracts.resource.v1.GetLastMeasurementsResponse;
import com.erval.argos.contracts.resource.v1.Measurement;
import com.erval.argos.contracts.resource.v1.ResourceQueryServiceGrpc.ResourceQueryServiceImplBase;
import com.erval.argos.core.application.PageRequest;
import com.erval.argos.core.application.SortDirection;
import com.erval.argos.core.application.port.in.queries.DeviceQueryUseCase;
import com.erval.argos.core.application.port.in.queries.MeasurementQueryUseCase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

/**
 * gRPC query adapter exposing resource read APIs backed by application use
 * cases.
 * <p>
 * Handles request validation and maps domain models to protobuf responses.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceQueryGrpcService extends ResourceQueryServiceImplBase {

    private final DeviceQueryUseCase deviceQuery;
    private final MeasurementQueryUseCase measurementQuery;

    /**
     * Fetches a device by id and returns a response with a {@code found} flag.
     *
     * @param request          contains the device id
     * @param responseObserver streams the response or an error
     */
    @Override
    public void getDevice(GetDeviceRequest request, StreamObserver<GetDeviceResponse> responseObserver) {
        try {
            log.info("Received GetDeviceRequest for id={}", request.getDeviceId());
            String id = request.getDeviceId();

            Optional<com.erval.argos.core.domain.device.Device> opt = deviceQuery.findById(id);

            if (opt.isEmpty()) {
                responseObserver.onNext(GetDeviceResponse.newBuilder().setFound(false).build());
                responseObserver.onCompleted();
                return;
            }

            var d = opt.get();

            Device device = Device.newBuilder()
                    .setId(d.id())
                    .setName(d.name())
                    .setType(d.type().name())
                    .setBuilding(d.building())
                    .setRoom(d.room())
                    .setActive(d.active())
                    .setDeleted(d.deleted())
                    .build();

            responseObserver.onNext(GetDeviceResponse.newBuilder()
                    .setFound(true)
                    .setDevice(device)
                    .build());

            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    /**
     * Fetches the latest measurements for a device, ordered by timestamp
     * descending.
     * <p>
     * Applies a limit (max 500) and defaults the {@code to} timestamp to now
     * when omitted.
     *
     * @param request          contains device id, optional limit and to timestamp
     * @param responseObserver streams the response or an error
     */
    @Override
    public void getLastMeasurements(GetLastMeasurementsRequest request,
            StreamObserver<GetLastMeasurementsResponse> responseObserver) {
        try {
            var deviceId = request.getDeviceId();
            if (deviceId.isBlank()) {
                responseObserver.onNext(GetLastMeasurementsResponse.newBuilder().build());
                responseObserver.onCompleted();
                return;
            }
            int limit = request.getLimit() > 0 ? request.getLimit() : 100;
            limit = Math.min(limit, 500);

            Instant to = request.getTo().isBlank()
                    ? Instant.now()
                    : Instant.parse(request.getTo());

            var filter = new MeasurementQueryUseCase.MeasurementFilter(
                    deviceId,
                    null,
                    null,
                    to);

            var pageReq = new PageRequest(0,
                    limit,
                    "timestamp",
                    SortDirection.DESC);

            var page = measurementQuery.findMeasurements(filter, pageReq);

            var res = GetLastMeasurementsResponse.newBuilder();
            for (var m : page.content()) {
                res.addMeasurements(Measurement.newBuilder()
                        .setId(m.id() == null ? "" : m.id())
                        .setDeviceId(m.deviceId())
                        .setType(m.type().name())
                        .setValue(m.value())
                        .setSequenceNumber(m.sequenceNumber())
                        .setTimestamp(m.timestamp().toString())
                        .addAllTags(m.tags() == null ? List.of() : m.tags())
                        .build());
            }

            responseObserver.onNext(res.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

}
