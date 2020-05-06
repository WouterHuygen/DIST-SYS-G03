package group1.dist.node;

import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

import static group1.dist.node.DiscoveryService.*;

public class UDPListener implements Runnable {

    private ApplicationContext context;

    public UDPListener(ApplicationContext context) {
        this.context = context;
    }

    public void run() {
        System.out.println("Started Listener");
        while (true) {
            try (MulticastSocket listenSocket = new MulticastSocket(MULTICAST_PORT)){
                InetAddress group = InetAddress.getByName(MULTICAST_GROUP_ADDRESS);
                listenSocket.joinGroup(group);

                byte[] msg = new byte[MAX_MSG_LEN];
                DatagramPacket packet = new DatagramPacket(msg, msg.length);
                listenSocket.receive(packet);
                String data = new String(packet.getData());
                System.out.println("\nReceived packet from: " + packet.getAddress().getHostAddress());
                System.out.println("\nMessage: \"" + data + "\"\n");
                if (data.contains("Joining")){
                    handleJoin(data.substring(data.indexOf(':')+2, data.indexOf(',')), packet.getAddress());
                }
            } catch (IOException e) {
                System.out.println("MulticastSocket failed");
                e.printStackTrace();
            }
        }
    }

    public void handleJoin(String nodeName, InetAddress ipAddress) {
        NodeInfo nodeInfo = context.getBean(NodeInfo.class);
        System.out.println(ipAddress.getHostAddress());
        int hash = Node.calculateHash(nodeName);
        System.out.println("calculating hashes");
        System.out.println("current Id: " + nodeInfo.getSelf().getId());
        System.out.println("hash Id: " + hash);
        int currentId = nodeInfo.getSelf().getId();
        Node node = new Node(nodeName, ipAddress.getHostAddress());
        if (nodeInfo.getNextNode() == null || nodeInfo.getNextNode() == nodeInfo.getSelf() || (currentId < hash && hash < nodeInfo.getNextNode().getId())) {
            nodeInfo.setNextNode(node);
            System.out.println(nodeInfo.getNextNode());
            sendAck(nodeInfo.getSelf().getName(), ipAddress, "previous\nname: " + nodeInfo.getSelf().getName() + ";");
        } else if (nodeInfo.getPreviousNode() == null || nodeInfo.getPreviousNode() == nodeInfo.getSelf() || (currentId > hash && hash > nodeInfo.getPreviousNode().getId())){
            nodeInfo.setPreviousNode(node);
            System.out.println(nodeInfo.getPreviousNode());
            sendAck(nodeInfo.getSelf().getName(), ipAddress, "next\nname: " + nodeInfo.getSelf().getName() + ";");
        }
    }

    private void sendAck(String srcHostname, InetAddress destIp, String message) {
        String response = "Response from: " + srcHostname + "\nMessage: " + message;
        System.out.println("sending response: \"" + response + "\"");
        try (DatagramSocket unicastSocket = new DatagramSocket(ACK_PORT)){
            byte[] data = response.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(data, data.length, destIp, ACK_PORT);
            unicastSocket.send(packet);
            System.out.println("response sent");
        } catch (IOException e){
            System.out.println("Unicast socket failed\nFailed to send ack");
            e.printStackTrace();
        }
    }
}