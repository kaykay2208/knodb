package com.grpc.knodb;
import com.data.DataEngine;
import com.grpc.knodb.KnodbOpsServiceOuterClass.GetRequest;
import com.grpc.knodb.KnodbOpsServiceOuterClass.GetResponse;
import com.grpc.knodb.KnodbOpsServiceOuterClass.PutRequest;
import com.grpc.knodb.KnodbOpsServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.Map;

public class KnodbOpsServiceImpl extends KnodbOpsServiceGrpc.KnodbOpsServiceImplBase
{
	@Override
	public void get(GetRequest request, StreamObserver<GetResponse> responseObserver) {

		String data =DataEngine.getData(request.getKey());
		System.out.println("data :"+ data.toString());
		GetResponse response = GetResponse.newBuilder()
			.putData("sample",data)
			.build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void put(PutRequest request, StreamObserver<GetResponse> responseObserver) {

		System.out.println("Received PUT request:");
		System.out.println("Key: " + request.getKey());
		DataEngine.addData( request.getKey(),request.getDataMap());
		System.out.println("Data: " + request.getDataMap());
		GetResponse response = GetResponse.newBuilder()
			.putData("example_key", "example_value")
			.build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
