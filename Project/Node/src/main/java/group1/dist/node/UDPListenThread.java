package group1.dist.node;

import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

import static group1.dist.node.DiscoveryService.*;

public class UDPListenThread extends Thread{

    private ApplicationContext context;

    public UDPListenThread(ApplicationContext context) {
        this.context = context;
    }

    public void run() {
        System.out.println("Started Listener");
        while (true) {
            try (MulticastSocket listenSocket = new MulticastSocket(MULTICAST_PORT)){
                InetAddress group = InetAddress.getByName(MULTICAST_GROUP_ADDRESS);
                listenSocket.joinGroup(group);
                System.out.println("Joined multicast group");

                byte[] msg = new byte[MAX_MSG_LEN];
                DatagramPacket packet = new DatagramPacket(msg, msg.length);
                listenSocket.receive(packet);
                System.out.println("\nReceived packet from: " + packet.getAddress());
                System.out.println("\nMessage: \"" + new String(packet.getData()) + "\"\n");
                String hostname = new String(packet.getData());
                hostname = hostname.substring(hostname.indexOf(",")+2);
                System.out.println("hashing with: " + hostname);
                listenSocket.close();
                checkIds(packet);
            } catch (IOException e) {
                System.out.println("MulticastSocket failed");
                e.printStackTrace();
            }
        }
    }

    public void checkIds(DatagramPacket packet) {
        NodeInfo nodeInfo = context.getBean(NodeInfo.class);
        System.out.println(packet.getAddress().getHostAddress());
        String nodeName = packet.getAddress().getHostAddress(); //TODO: extract name from packet
        int hash = Node.calculateHash(nodeName);
        System.out.println("calculating hashes");
        System.out.println("current Id: " + nodeInfo.getSelf().getId());
        System.out.println("hash Id: " + hash);
        int currentId = nodeInfo.getSelf().getId();
        Node node = new Node(nodeName, packet.getAddress().toString());

        boolean isNext = false;
        if (nodeInfo.getNextNode() == null) {
            isNext = true;
        }
        else if ((currentId < hash && hash < nodeInfo.getNextNode().getId())) {
            isNext = true;
        }
        if (isNext) {
            nodeInfo.setNextNode(node);
            sendAck(nodeInfo.getSelf().getName(), packet.getAddress(), "previous");
        }

        boolean isPrevious = false;
        if (nodeInfo.getPreviousNode() == null) {
            isPrevious = true;
        }
        else if ((currentId > hash && hash > nodeInfo.getPreviousNode().getId())) {
            isPrevious = true;
        }

        if (isPrevious){
            nodeInfo.setPreviousNode(node);
            sendAck(nodeInfo.getSelf().getName(), packet.getAddress(), "next");
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