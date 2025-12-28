package com.erval.argos.resource.adapters.grpc;

import com.erval.argos.contracts.resource.v1.*;
import com.erval.argos.contracts.resource.v1.ResourceQueryServiceGrpc.ResourceQueryServiceImplBase;
import com.erval.argos.core.application.PageRequest;
import com.erval.argos.core.application.SortDirection;
import com.erval.argos.core.application.port.in.queries.DeviceQueryUseCase;
import com.erval.argos.core.application.port.in.queries.MeasurementQueryUseCase;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ResourceQueryGrpcService extends ResourceQueryServiceImplBase {

    private final DeviceQueryUseCase deviceQuery;
    private final MeasurementQueryUseCase measurementQuery;

    @Override
    public void getDevice(GetDeviceRequest request, StreamObserver<GetDeviceResponse> responseObserver) {
        try {
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
