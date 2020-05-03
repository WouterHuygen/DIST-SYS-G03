package group1.dist.node.Replication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPMessage {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String sendMessage(String msg) {
        try{
            out.println(msg);
            String resp = in.readLine();
            return resp;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void stopConnection() {
        try{
            in.close();
            out.close();
            clientSocket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
