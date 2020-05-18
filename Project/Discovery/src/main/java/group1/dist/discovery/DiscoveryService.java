package group1.dist.discovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import group1.dist.model.Node;
import group1.dist.model.NodeInfo;

import java.io.IOException;
import java.net.*;

public class DiscoveryService {
    public static final int MULTICAST_PORT = 3009;
    public static final String MULTICAST_GROUP_ADDRESS = "225.4.5.6";
    public static final int ACK_PORT = 3008;
    public static final int MAX_MSG_LEN = 150;

    private NodeInfo nodeInfo;

    public DiscoveryService(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public boolean sendJoin() {
        boolean success = false;
        try (MulticastSocket multicastSocket = new MulticastSocket()){
            InetAddress group = InetAddress.getByName(MULTICAST_GROUP_ADDRESS);
            String nodeName = nodeInfo.getSelf().getName();
            DiscoveryMessage msg = new DiscoveryMessage(MessageType.JOINING_NODE);
            msg.setIp(nodeInfo.getSelf().getIp());
            msg.setName(nodeName);
            String strMsg = msg.toString();
            DatagramPacket packet = new DatagramPacket(strMsg.getBytes(), strMsg.length(), group, MULTICAST_PORT);
            multicastSocket.send(packet);
            System.out.println("Sent message: \"" + strMsg + "\"");
            success = receiveAck();
        } catch (UnknownHostException uhe) {
            System.out.println("Unknown host 225.4.5.6");
            uhe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    public void shutdown(NodeInfo nodeInfo) throws IOException {
        System.out.println("Removing " + nodeInfo.getSelf().getName() + "from nameserver mapping");
        URL url = new URL("http://" + nodeInfo.getNamingIp() + ":8080/nodes?name=" + nodeInfo.getSelf().getName());
        System.out.println(url);
        if (nodeInfo.getNamingIp() != null) {
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestMethod("DELETE");
            httpCon.connect();
            System.out.println(httpCon.getResponseCode());
        } else {
            System.out.println("failed to remove from naming server, IP was null");
        }
        try {
            Node previousNode = nodeInfo.getPreviousNode();
            Node nextNode = nodeInfo.getNextNode();
            System.out.println("My next node is: " + nextNode.getName() + " with IP: " + nextNode.getIp());
            System.out.println("My previous node is: " + previousNode.getName() + " with IP: " + previousNode.getIp());
        }catch (NullPointerException e){
            System.out.println("No previous or Next node found");
        }
    }

    private boolean receiveAck(){
        boolean success = false;
        try(DatagramSocket UDPSocket = new DatagramSocket(ACK_PORT)){
            byte[] receivedMsg = new byte[MAX_MSG_LEN];
            DatagramPacket received = new DatagramPacket(receivedMsg, receivedMsg.length);
            ObjectMapper objectMapper = new ObjectMapper();
            while (true){
                try{
                    UDPSocket.setSoTimeout(1000);
                    UDPSocket.receive(received);
                    String data = new String(received.getData());
                    System.out.println("Received: " + data);
                    DiscoveryMessage message = objectMapper.readValue(data, DiscoveryMessage.class);
                    switch (message.getType()) {
                        case NEXT_NODE:
                            System.out.println("Response from next");
                            nodeInfo.setNextNode(new Node(message.getName(), message.getIp()));
                            System.out.println(nodeInfo.getNextNode());
                            System.out.println("Received ACK");
                            success = true;
                            break;
                        case PREVIOUS_NODE:
                            System.out.println("Response from previous");
                            nodeInfo.setPreviousNode(new Node(message.getName(), message.getIp()));
                            System.out.println(nodeInfo.getPreviousNode());
                            System.out.println("Received ACK");
                            success = true;
                            break;
                        case NAMING_RESPONSE:
                            System.out.println("Response from naming");
                            if (message.getExistingNodes() == 0) {
                                nodeInfo.setNextNode(nodeInfo.getSelf());
                                System.out.println("Next: " + nodeInfo.getNextNode());
                                nodeInfo.setPreviousNode(nodeInfo.getSelf());
                                System.out.println("Previous: " + nodeInfo.getPreviousNode());
                            }
                            nodeInfo.setNamingIp(message.getIp());
                            System.out.println("Naming: " + nodeInfo.getNamingIp());
                            if (message.getNewHostname() != null) {
                                //TODO: shutdown
                                System.out.println("shutting down for restart with: " + message.getNewHostname());
                            }
                            System.out.println("Received ACK");
                            success = true;
                            break;
                    }
                    receivedMsg = new byte[MAX_MSG_LEN];
                    received.setData(receivedMsg);
               } catch (SocketTimeoutException sto){
                    System.out.println("Timeout for ack, " + (success? "ack received" : "no ack received"));
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (SocketException se) {
            System.out.println("failed to open ack socket");
            se.printStackTrace();
        }
        return success;
    }
}
