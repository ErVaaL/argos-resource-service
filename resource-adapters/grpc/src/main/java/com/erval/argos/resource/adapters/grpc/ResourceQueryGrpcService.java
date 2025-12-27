package com.erval.argos.resource.adapters.grpc;

import java.util.Optional;

import com.erval.argos.contracts.resource.v1.Device;
import com.erval.argos.contracts.resource.v1.GetDeviceRequest;
import com.erval.argos.contracts.resource.v1.GetDeviceResponse;
import com.erval.argos.contracts.resource.v1.ResourceQueryServiceGrpc.ResourceQueryServiceImplBase;
import com.erval.argos.core.application.port.in.queries.DeviceQueryUseCase;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ResourceQueryGrpcService extends ResourceQueryServiceImplBase {

    private final DeviceQueryUseCase deviceQuery;

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

}
