package group1.dist.node;

import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

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
                System.out.println("hasing with: " + hostname);

                int hashName = calculateHash(hostname);
                listenSocket.close();
                //sendAck(packet.getAddress());
                checkIds(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static int calculateHash(String name) {
        int hash = (name.hashCode() & (32768 - 1));
        return ((hash * hash) & (32768 - 1));
    }

    public void checkIds(DatagramPacket packet) throws IOException {
        System.out.println(packet.getAddress().getHostAddress());
        int hash = calculateHash(packet.getAddress().getHostAddress());
        currentId = calculateHash(InetAddress.getLocalHost().getHostName());
        System.out.println("calculating hashes");
        System.out.println("current Id: " + currentId);
        System.out.println("hash Id: " + hash);
        if ((currentId < hash && hash < nextNodeID) | nextNodeID == 0) {
            nextNodeID = hash;
            sendAck(packet.getAddress(), "previous");
        } else if ((currentId > hash && hash > previousNodeID) | previousNodeID == 0){
            previousNodeID = hash;
            sendAck(packet.getAddress(), "next");
        }
    }
}