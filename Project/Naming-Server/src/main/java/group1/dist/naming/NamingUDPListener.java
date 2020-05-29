package group1.dist.naming;

import group1.dist.discovery.DiscoveryMessage;
import group1.dist.discovery.MessageType;
import group1.dist.discovery.UDPListener;
import group1.dist.model.Node;
import group1.dist.model.NodeInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import static group1.dist.discovery.DiscoveryService.*;

public class NamingUDPListener extends UDPListener {
    private MapManager mapManager;
    private NodeInfo nodeInfo;

    public NamingUDPListener(MapManager mapManager, NodeInfo nodeInfo) {
        this.mapManager = mapManager;
        this.nodeInfo = nodeInfo;
    }

    protected void handleJoin(String nodeName, InetAddress ipAddress) {
        int existingNodes = mapManager.getMap().size();
        DiscoveryMessage response = new DiscoveryMessage(MessageType.NAMING_RESPONSE);
        response.setExistingNodes(existingNodes);
        response.setIp(nodeInfo.getSelf().getIp());
        Node newNode = new Node(nodeName, ipAddress.getHostAddress());
        if (mapManager.addNode(newNode)) {
            System.out.println("Added node: " + newNode);
        }
        else {
            if (!mapManager.getMap().get(Integer.toString(Node.calculateHash(nodeName))).getIp().equals(ipAddress.getHostAddress())) {
                int i = 0;
                do {
                    i++;
                    nodeName = nodeName + i;
                    newNode = new Node(nodeName, ipAddress.getHostAddress());
                } while (!mapManager.addNode(newNode));
                response.setNewHostname(nodeName);
                System.out.println("Added node: " + nodeName); //TODO: node.toString()
            }
        }
        System.out.println("sending response: \"" + response + "\"");
        try (DatagramSocket unicastSocket = new DatagramSocket()){
            byte[] data = response.toString().getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, ACK_PORT);
            unicastSocket.send(packet);
            System.out.println("response sent");
        } catch (IOException e){
            System.out.println("failed to send ack");
            e.printStackTrace();
        }
    }
}
