package group1.dist.node;

import group1.dist.discovery.UDPListener;
import group1.dist.model.Node;
import group1.dist.model.NodeInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import static group1.dist.discovery.DiscoveryService.ACK_PORT;

public class NodeUDPListener extends UDPListener {

    private NodeInfo nodeInfo;

    public NodeUDPListener(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    protected void handleJoin(String nodeName, InetAddress ipAddress) {
        System.out.println(ipAddress.getHostAddress());
        int hash = Node.calculateHash(nodeName);
        System.out.println("calculating hashes");
        System.out.println("current Id: " + nodeInfo.getSelf().getId());
        System.out.println("hash Id: " + hash);
        int currentId = nodeInfo.getSelf().getId();
        Node node = new Node(nodeName, ipAddress.getHostAddress());
        if (nodeInfo.getNextNode() == nodeInfo.getSelf() && nodeInfo.getPreviousNode() == nodeInfo.getSelf()) {
            nodeInfo.setNextNode(node);
            System.out.println(nodeInfo.getNextNode());
            sendAck(nodeInfo.getSelf().getName(), ipAddress, "previous\nname: " + nodeInfo.getSelf().getName() + ";");
            nodeInfo.setPreviousNode(node);
            System.out.println(nodeInfo.getPreviousNode());
            sendAck(nodeInfo.getSelf().getName(), ipAddress, "next\nname: " + nodeInfo.getSelf().getName() + ";");
        }
        else if (nodeInfo.getNextNode() == null || nodeInfo.getNextNode() == nodeInfo.getSelf() || (currentId < hash && hash < nodeInfo.getNextNode().getId())) {
            nodeInfo.setNextNode(node);
            System.out.println(nodeInfo.getNextNode());
            sendAck(nodeInfo.getSelf().getName(), ipAddress, "previous\nname: " + nodeInfo.getSelf().getName() + ";");
        }
        else if (nodeInfo.getPreviousNode() == null || nodeInfo.getPreviousNode() == nodeInfo.getSelf() || (currentId > hash && hash > nodeInfo.getPreviousNode().getId())){
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