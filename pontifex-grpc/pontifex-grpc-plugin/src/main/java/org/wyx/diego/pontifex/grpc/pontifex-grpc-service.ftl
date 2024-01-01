package ${packageName};

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.modelmapper.ModelMapper;

import javax.annotation.Resource;
@GrpcService
public class ${className} extends ${superClassName} {

@Resource
private ${realPipelineServiceName} pipeline;

@Override
public void call(PontifexRequest request, StreamObserver<PontifexResponse> responseObserver) {

    org.wyx.diego.pontifex.PontifexRequest<${pontfexRequestGenericParamFullName}> pontifexRequest = new org.wyx.diego.pontifex.PontifexRequest<>();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.map(request, pontifexRequest);
        org.wyx.diego.pontifex.PontifexResponse<${pontifexResponseGenericParamFullName}> pontifexResponse = pipeline.call(pontifexRequest);
        PontifexResponse pr = PontifexResponse.newBuilder().build();
        modelMapper.map(pontifexResponse, pr);
        responseObserver.onNext(pr);
        responseObserver.onCompleted();
        //        super.call(request, responseObserver);
    }
}
