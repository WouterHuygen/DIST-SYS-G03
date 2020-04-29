package group1.dist.naming;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import static group1.dist.naming.DiscoveryService.ACK_PORT;

public class HandleJoinThread extends Thread {

    private ApplicationContext context;

    private MapManager mapManager;

    private String nodeName;
    private InetAddress ipAddress;

    public HandleJoinThread(String nodeName, InetAddress ipAddress, ApplicationContext context) {
        this.nodeName = nodeName;
        this.ipAddress = ipAddress;
        this.context = context;
        mapManager = context.getBean(MapManager.class);
    }

    public void run() {
        int existingNodes = mapManager.getMap().size(); //TODO: error, nullpointer exception
        if (mapManager.addNode(new Node(nodeName, ipAddress.toString()))) {
            System.out.println("Added node: " + nodeName); //TODO: node.toString()
        }
        String response = "Response from: Naming Server\nExisting nodes: " + existingNodes;
        System.out.println("sending response: \"" + response + "\"");
        try (DatagramSocket unicastSocket = new DatagramSocket(ACK_PORT)){
            byte[] data = response.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, ACK_PORT);
            unicastSocket.send(packet);
            System.out.println("response sent");
        } catch (IOException e){
            System.out.println("failed to send ack");
            e.printStackTrace();
        }
    }
}
