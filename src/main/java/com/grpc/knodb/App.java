/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.grpc.knodb;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

import com.data.DataEngine;
import com.data.DataWorker;

public class App
{
	public String getGreeting ()
	{
		return "Hello world.";
	}

	public static void main (String[] args) throws IOException
	{
		Server server = ServerBuilder.forPort(9090)
			.addService((BindableService) new KnodbOpsServiceImpl())
			.build();
		server.start();
		DataWorker.initializeWorker();
		System.out.println("Server started, listening on " + server.getPort());
		try
		{
			server.awaitTermination();
		}
		catch (Exception e)
		{

		}
	}
}
