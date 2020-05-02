package group1.dist.node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.*;

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
            String nodeName = context.getBean(NodeInfo.class).getSelf().getName();
            String msg = "Joining: " + nodeName + ", " + InetAddress.getLocalHost().getHostAddress();
            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), group, MULTICAST_PORT);
            multicastSocket.send(packet);
            System.out.println("Sent message: \"" + msg + "\"");
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
                    String data = new String(received.getData());
                    System.out.println("Received: " + data);
                    data = data.toLowerCase();
                    if (data.contains("response from")) { //TODO: depending on functionality
                        System.out.println("Received ACK");
                        success = true;
                        if (data.contains("naming")){
                            System.out.println("Response from naming");
                            //TODO: logic
                        }
                        else if (data.contains("previous")){
                            System.out.println("Response from previous");
                            //TODO: logic
                        }
                        else if (data.contains("next")){
                            System.out.println("Response from next");
                            //TODO: logic
                        }
                    } //TODO: clear receivedMsg byte array?
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
