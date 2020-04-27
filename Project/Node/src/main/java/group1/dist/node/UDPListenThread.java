package group1.dist.node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import static group1.dist.node.DiscoveryService.*;

public class UDPListenThread extends Thread{
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

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
