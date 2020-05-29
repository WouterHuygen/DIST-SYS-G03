package group1.dist.node;

import group1.dist.discovery.DiscoveryMessage;
import group1.dist.discovery.MessageType;
import group1.dist.discovery.UDPListener;
import group1.dist.model.Node;
import group1.dist.model.NodeInfo;
import group1.dist.node.Replication.APICall;
import group1.dist.node.Replication.FileReplicationHandler;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static group1.dist.discovery.DiscoveryService.ACK_PORT;

public class NodeUDPListener extends UDPListener {

    private NodeInfo nodeInfo;
    private FileReplicationHandler fileReplicationHandler;
    private APICall apiCall;
    public NodeUDPListener(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
        fileReplicationHandler = new FileReplicationHandler(this.nodeInfo);
        apiCall = new APICall(this.nodeInfo);
    }


    protected void handleJoin(String nodeName, InetAddress ipAddress) {
        System.out.println(ipAddress.getHostAddress());
        int hash = Node.calculateHash(nodeName);
        System.out.println("calculating hashes");
        System.out.println("current Id: " + nodeInfo.getSelf().getId());
        System.out.println("hash Id: " + hash);
        Node node = new Node(nodeName, ipAddress.getHostAddress());
        int currentId = nodeInfo.getSelf().getId();
        int nextId = 0, previousId = 0;
        if (nodeInfo.getNextNode() != null)
            nextId = nodeInfo.getNextNode().getId();
        if (nodeInfo.getPreviousNode() != null)
            previousId = nodeInfo.getPreviousNode().getId();
        boolean isPrevious =  nextId == 0 || nextId == currentId
                || (currentId < hash         // for new node to become next, hash should be bigger than current hash
                && (hash < nextId            // either hash is between this node and next node
                    || nextId < currentId))  // this node is end of circle and new node will become new end of circle
                || hash < nextId && nextId < currentId; // this node is end of circle and new node becomes new begin of circle
        boolean isNext = previousId == 0 || previousId == currentId
                || (currentId > hash            // for new node to become previous, hash should be smaller than current hash
                && (hash > previousId           // either hash is between this node and previous node
                    || previousId > currentId)) // this node is start of circle and new node will become new start of circle
                || hash > previousId && previousId > currentId; // this node is the start of the circle and new node is new end of circle
        if (nextId == previousId && nextId != currentId) { // there were 2 nodes and a third node needs to be added previous settings of isNext/isPrevious are not valid
            if (hash > currentId) {
                if (hash < nextId){                 // the new node is between the two existing nodes
                    isPrevious = true;
                    isNext = false;
                }
                else if (currentId < nextId){       // the new node is after the two existing nodes and this node was the first node
                    isNext = true;
                    isPrevious = false;
                }
                else {                              // the new node is after the two existing nodes and this node was the last node
                    isPrevious = true;
                    isNext = false;
                }
            }
            else {
                if (hash > previousId) {            // the new node is between the two existing nodes
                    isNext = true;
                    isPrevious = false;
                }
                else if (currentId > previousId){   // the new node is before the two existing nodes and this node was the last node
                    isPrevious = true;
                    isNext = false;
                }
                else {                              // the new node is before the two existing nodes and this node was the first node
                    isNext = true;
                    isPrevious = false;
                }
            }
        }
        if (isNext) {
            nodeInfo.setPreviousNode(node);
            System.out.println(nodeInfo.getPreviousNode());
            sendAck(nodeInfo.getSelf().getName(), ipAddress, MessageType.NEXT_NODE);
        }
        if (isPrevious) {
            nodeInfo.setNextNode(node);
            System.out.println(nodeInfo.getNextNode());
            sendAck(nodeInfo.getSelf().getName(), ipAddress, MessageType.PREVIOUS_NODE);


            //Replicate own files if you were the only node in the system
            File ownFolder = new File("/home/pi/node/ownFiles");
            if(ownFolder.listFiles()  != null){
                String next = nodeInfo.getNextNode().getIp();
                String previous = nodeInfo.getPreviousNode().getIp();
                System.out.println("Own next " + next);
                System.out.println("Own previous " + previous);
                //Next and previous are the same node but are not your own IP
                if(next.equals(previous) && !next.equals(nodeInfo.getSelf().getIp())){
                    System.out.println("Replicate own files");
                    for (File fileEntry : Objects.requireNonNull(ownFolder.listFiles())){
                        fileReplicationHandler.replicateFile(fileEntry);
                    }
                }
            }

            // Rereplicate files if needed
            File folder = new File("/home/pi/node/replicatedFiles");
            if(folder.listFiles()  != null){
                System.out.println("Replicated files not empty");
                for (File fileEntry : Objects.requireNonNull(folder.listFiles())){

                    String goToIp = apiCall.call(fileEntry.getName());
                    System.out.println("Apicall IP " + goToIp);
                    System.out.println("self: " + nodeInfo.getSelf().getIp());

                    if(!goToIp.equals(nodeInfo.getSelf().getIp())){
                        fileReplicationHandler.replicateFile(fileEntry);
                        if(fileEntry.delete()){
                            System.out.println("Deleted after replication");
                        }
                        else{
                            System.out.println("File not deleted after replication");
                        }
                    }
                }
            }
        }

        System.out.println("self: " + nodeInfo.getSelf());
        System.out.println("next: " + nodeInfo.getNextNode());
        System.out.println("previous: " + nodeInfo.getPreviousNode());
    }

    private void sendAck(String srcHostname, InetAddress destIp, MessageType type) {
        DiscoveryMessage response = new DiscoveryMessage(type);
        response.setName(srcHostname);
        response.setIp(nodeInfo.getSelf().getIp());
        System.out.println("sending response: \"" + response + "\"");
        try (DatagramSocket unicastSocket = new DatagramSocket(ACK_PORT)){
            byte[] data = response.toString().getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(data, data.length, destIp, ACK_PORT);
            unicastSocket.send(packet);
            System.out.println("response sent");
        } catch (IOException e){
            System.out.println("Unicast socket failed\nFailed to send ack");
            e.printStackTrace();
        }
    }
}