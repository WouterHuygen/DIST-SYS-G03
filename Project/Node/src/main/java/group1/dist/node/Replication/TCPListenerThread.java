package group1.dist.node.Replication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPListenerThread implements Runnable {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    //private PrintWriter out;
    private BufferedReader in;

    public void stop() {
        try {
            in.close();
            //out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Started TCP Listener");
                // TODO: socket port moven
                while(true) {
                    try{
                        serverSocket = new ServerSocket(5556);
                        clientSocket = serverSocket.accept();
                        //out = new PrintWriter(clientSocket.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String message = in.readLine();
                        String split_message[] = message.split(" ");
                        System.out.println(message);
                        stop();
                        if (split_message[0].equals("replication")) {
                            FileTransferClient.ClientRun(split_message[1], split_message[2]);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
    }
}
