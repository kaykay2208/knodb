package knodb;

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
	public void put(PutRequest request, StreamObserver<google.protobuf.Empty> responseObserver) {

		System.out.println("Received PUT request:");
		System.out.println("Key: " + request.getKey());
		System.out.println("Data: " + request.getDataMap());


		responseObserver.onNext(google.protobuf.Empty.getDefaultInstance());
		responseObserver.onCompleted();
	}
}
