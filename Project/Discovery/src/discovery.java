import java.io.IOException;
import java.net.*;

public class discovery {
    static int multicastPort = 3009;
    static int MAX_MSG_LEN = 100;
    public static void main(String[] args) {
        sendHello();
    }

    public static void sendHello(){
        //Hello message via multicast
        int multicastPort = 3009;
        InetAddress group = null;
        try {
            group = InetAddress.getByName("225.4.5.6");
            MulticastSocket sendSocket = new MulticastSocket();
            String msg = "Hello I am, " + InetAddress.getLocalHost().getHostName();
            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), group, multicastPort);
            sendSocket.send(packet);
            sendSocket.close();
            System.out.println("Hello msg send, starting listen thread.");
            //run();

            DatagramSocket UDPSocket = new DatagramSocket(3008);
            byte[] receiveData = new byte[100];
            DatagramPacket receiveAck = new DatagramPacket(receiveData, receiveData.length);
            while (true){
                try {
                    UDPSocket.setSoTimeout(1000);
                    UDPSocket.receive(receiveAck);
                    System.out.println("received ack containing: \n" + new String(receiveAck.getData()));
                }catch (SocketTimeoutException sto){
                    System.out.println("No more packets");
                    break;
                }
            }
            run();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void run(){
        try {
            while (true){
                MulticastSocket listenSocket = new MulticastSocket(multicastPort);
                InetAddress group = InetAddress.getByName("225.4.5.6");
                listenSocket.joinGroup(group);
                System.out.println("Ready for packets.");

                byte[] message = new byte[MAX_MSG_LEN];
                DatagramPacket packet = new DatagramPacket(message, message.length);
                listenSocket.receive(packet);

                System.out.println("\nReceived packet on listenSocket from: " + packet.getAddress());
                System.out.println("Message: \n" + new String(packet.getData()) + "\n");
                listenSocket.close();
                sendAck(packet.getAddress());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendAck(InetAddress IPaddress) throws IOException {
        System.out.println("Sending ack. \n");
        DatagramSocket unicastSocket = new DatagramSocket(3008);
        String ackString = "ack from: " + InetAddress.getLocalHost().getHostName();
        byte[] ackData = ackString.getBytes("UTF-8");
        DatagramPacket ackPacket = new DatagramPacket(ackData, ackData.length, IPaddress, 3008);
        unicastSocket.send(ackPacket);
        unicastSocket.close();
    }

    public static void init() throws UnknownHostException {
        System.out.println("Turning on discovery: ");
    }
}

