package group1.dist.discovery;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import static group1.dist.discovery.DiscoveryService.*;

public abstract class UDPListener implements Runnable {

    public void run() {
        System.out.println("Started Listener");
        ObjectMapper objectMapper = new ObjectMapper();
        while (true) {
            try (MulticastSocket listenSocket = new MulticastSocket(MULTICAST_PORT)){
                InetAddress group = InetAddress.getByName(MULTICAST_GROUP_ADDRESS);
                listenSocket.joinGroup(group);

                byte[] msg = new byte[MAX_MSG_LEN];
                DatagramPacket packet = new DatagramPacket(msg, msg.length);
                listenSocket.receive(packet);
                String data = new String(packet.getData());
                DiscoveryMessage message = objectMapper.readValue(data, DiscoveryMessage.class);
                System.out.println("\nReceived packet from: " + packet.getAddress().getHostAddress());
                System.out.println("\nMessage: \"" + data + "\"\n");
                if (message.getType() == MessageType.JOINING_NODE){
                    handleJoin(message.getName(), InetAddress.getByName(message.getIp()));
                }
            } catch (IOException e) {
                System.out.println("MulticastSocket failed");
                e.printStackTrace();
            }
        }
    }

    protected abstract void handleJoin(String nodeName, InetAddress ipAddress);
}
