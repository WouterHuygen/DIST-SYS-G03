package group1.dist.naming;

import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import static group1.dist.naming.DiscoveryService.*;

public class UDPListenThread extends Thread {
    private ApplicationContext context;

    public UDPListenThread(ApplicationContext context) {
        this.context = context;
    }

    public void run() {
        System.out.println("Started Listener");
        while (true) {
            try (MulticastSocket listenSocket = new MulticastSocket(MULTICAST_PORT)) {
                InetAddress group = InetAddress.getByName(MULTICAST_GROUP_ADDRESS);
                listenSocket.joinGroup(group);
                System.out.println("Joined multicast group");

                byte[] msg = new byte[MAX_MSG_LEN];
                System.out.println("made byte array");
                DatagramPacket packet = new DatagramPacket(msg, msg.length);
                System.out.println("made datagram packet");
                listenSocket.receive(packet);
                System.out.println("listened for packed");
                String data = new String(packet.getData());
                System.out.println("data: " + data);
                System.out.println("\nReceived packet from: " + packet.getAddress());
                System.out.println("\nMessage: \"" + data + "\"\n");
                if (data.contains("Joining")){
                    HandleJoinThread handleJoinThread = new HandleJoinThread(data.substring(data.indexOf(':')+1, data.indexOf(',')), packet.getAddress(), context); // TODO: json
                    handleJoinThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
