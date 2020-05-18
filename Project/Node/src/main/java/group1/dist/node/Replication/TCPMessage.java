package group1.dist.node.Replication;

import com.fasterxml.jackson.databind.ObjectMapper;
import group1.dist.discovery.DiscoveryMessage;
import group1.dist.discovery.MessageType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TCPMessage {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ObjectMapper objectMapper;

    public TCPMessage() {
        objectMapper = new ObjectMapper();
    }

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String sendMessage(DiscoveryMessage msg) {
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

    void sendReplicationMessage(String IP, String filename){
        DiscoveryMessage msg = new DiscoveryMessage(MessageType.REPLICATION_UPDATE);
        msg.setIp(IP);
        msg.setFilename(filename);
        sendMessage(msg);//"replication " + IP + " " + filename);

    }

    void sendDeleteMessage(String filename){
        DiscoveryMessage msg = new DiscoveryMessage(MessageType.REPLICATION_DELETE);
        msg.setFilename(filename);
        sendMessage(msg);//"delete " + filename);
    }

    public void sendShutdownMessage(String fromHost, String updateName,String updateIp){
        sendMessage("shutdown " + fromHost + " IP " + updateIp + " NAME " + updateName);
    }

}
