package group1.dist.node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class DiscoveryService {
    static final int MULTICAST_PORT = 3009;
    static final String MULTICAST_GROUP_ADDRESS = "225.4.5.6";
    static final int ACK_PORT = 3008;
    static final int MAX_MSG_LEN = 100;

    @Autowired
    private ApplicationContext context;

    public boolean sendJoin() {
        boolean success = false;
        try (MulticastSocket multicastSocket = new MulticastSocket()){
            InetAddress group = InetAddress.getByName(MULTICAST_GROUP_ADDRESS);
            String nodeName = context.getBean(NodeInfo.class).getNodeName();
            String msg = "Joining: " + nodeName + ", " + Arrays.toString(InetAddress.getLocalHost().getAddress());
            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), group, MULTICAST_PORT);
            multicastSocket.send(packet);
            System.out.println("Sent message: " + msg);
            success = receiveAck();
        } catch (UnknownHostException uhe) {
            System.out.println("Unknown host 225.4.5.6");
            uhe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    private boolean receiveAck(){
        boolean success = false;
        try(DatagramSocket UDPSocket = new DatagramSocket(ACK_PORT)){
            byte[] receivedMsg = new byte[MAX_MSG_LEN];
            DatagramPacket received = new DatagramPacket(receivedMsg, receivedMsg.length);
            while (true){
                try{
                    UDPSocket.setSoTimeout(1000);
                    UDPSocket.receive(received);
                    System.out.println("Received: " + Arrays.toString(received.getData()));
                    if (Arrays.toString(received.getData()).equals("ack")) { //TODO: depending on functionality
                        System.out.println("Received ACK");
                        success = true;
                        break;
                    }
                } catch (SocketTimeoutException sto){
                    System.out.println("Timeout for ack, no ack received");
                    success = false;
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

    public void listen() {
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
                System.out.println("\nMessage: " + Arrays.toString(packet.getData()) + "\n");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
