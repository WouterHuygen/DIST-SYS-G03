package com.uantwerpen.hwserver;
//
//  Hello World server in Java
//  Binds REP socket to tcp://*:5555
//  Expects "Hello" from client, replies with "World"
//

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HwserverApplication {

	public static void main(String[] args) throws Exception
	{
		SpringApplication.run(HwserverApplication.class, args);
		try (ZContext context = new ZContext()) {
			// Socket to talk to clients
			ZMQ.Socket socket = context.createSocket(SocketType.REP);
			socket.bind("tcp://10.0.3.14:5555");

			while (!Thread.currentThread().isInterrupted()) {
				byte[] reply = socket.recv(0);
				System.out.println(
						"Received " + ": [" + new String(reply, ZMQ.CHARSET) + "]"
				);

				String response = "world";
				socket.send(response.getBytes(ZMQ.CHARSET), 0);

				Thread.sleep(1000); //  Do some 'work'
			}
		}
	}
}