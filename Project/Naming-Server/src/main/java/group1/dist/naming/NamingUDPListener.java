package group1.dist.naming;

import group1.dist.discovery.UDPListener;
import group1.dist.model.Node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

import static group1.dist.discovery.DiscoveryService.*;

public class NamingUDPListener extends UDPListener {
    private MapManager mapManager;

    public NamingUDPListener(MapManager mapManager) {
        this.mapManager = mapManager;
    }

    public void run() {
        System.out.println("Started Listener");
        while (true) {
            try (MulticastSocket listenSocket = new MulticastSocket(MULTICAST_PORT)) {
                InetAddress group = InetAddress.getByName(MULTICAST_GROUP_ADDRESS);
                listenSocket.joinGroup(group);

                byte[] msg = new byte[MAX_MSG_LEN];
                DatagramPacket packet = new DatagramPacket(msg, msg.length);
                listenSocket.receive(packet);
                String data = new String(packet.getData());
                System.out.println("\nReceived packet from: " + packet.getAddress());
                System.out.println("\nMessage: \"" + data + "\"\n");
                if (data.contains("Joining")){
                    handleJoin(data.substring(data.indexOf(':')+2, data.indexOf(',')), packet.getAddress());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void handleJoin(String nodeName, InetAddress ipAddress) {
        int existingNodes = mapManager.getMap().size();
        String response = "Response from: Naming Server\nExisting nodes: " + existingNodes;
        if (mapManager.addNode(new Node(nodeName, ipAddress.getHostAddress()))) {
            System.out.println("Added node: " + nodeName); //TODO: node.toString()
        }
        else {
            if (!mapManager.getMap().get(Integer.toString(Node.calculateHash(nodeName))).getIp().equals(ipAddress.getHostAddress())) {
                int i = 0;
                do {
                    i++;
                    nodeName = nodeName + i;
                } while (!mapManager.addNode(new Node(nodeName, ipAddress.getHostAddress())));
                response += "\nNew name: " + nodeName;
                System.out.println("Added node: " + nodeName); //TODO: node.toString()
            }
        }
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
