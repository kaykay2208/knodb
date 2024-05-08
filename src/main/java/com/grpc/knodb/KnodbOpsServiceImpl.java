package com.grpc.knodb;
import com.grpc.knodb.KnodbOpsServiceOuterClass.GetRequest;
import com.grpc.knodb.KnodbOpsServiceOuterClass.GetResponse;
import com.grpc.knodb.KnodbOpsServiceOuterClass.PutRequest;
import com.grpc.knodb.KnodbOpsServiceGrpc;
import io.grpc.stub.StreamObserver;
public class KnodbOpsServiceImpl extends KnodbOpsServiceGrpc.KnodbOpsServiceImplBase
{
	@Override
	public void get(GetRequest request, StreamObserver<GetResponse> responseObserver) {


		GetResponse response = GetResponse.newBuilder()
			.putData("example_key", "example_value")
			.build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void put(PutRequest request, StreamObserver<GetResponse> responseObserver) {

		System.out.println("Received PUT request:");
		System.out.println("Key: " + request.getKey());
		System.out.println("Data: " + request.getDataMap());
		GetResponse response = GetResponse.newBuilder()
			.putData("example_key", "example_value")
			.build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
