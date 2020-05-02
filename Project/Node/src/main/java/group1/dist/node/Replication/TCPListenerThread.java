package group1.dist.node.Replication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPListenerThread implements Runnable {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void stop() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Started TCP Listener");
        while(true) {
            try {
                // TODO: socket port moven
                serverSocket = new ServerSocket(5555);
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String greeting = in.readLine();
                if ("hello server".equals(greeting)) {
                    out.println("hello client");
                } else {
                    out.println("unrecognised greeting");
                }
                stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
