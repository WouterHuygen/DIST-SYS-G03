package group1.dist.node.Replication;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;

public class FileTransferServer {

    public void serverRun(File file, String IP) throws Exception {

        NetworkInterface networkInterface = NetworkInterface.getByName("ethwe0");

        //Initialize Sockets
        TCPMessage msg = new TCPMessage();
        System.out.println("IP used for TCP: " + IP);
        msg.startConnection(IP, 5556);
        msg.sendReplicationMessage(networkInterface.getInetAddresses().nextElement().getHostAddress(), file.getName());


        System.out.println("Server startup!");

        if(available(5000))
            System.out.println("port free");
        else
            System.out.println("server fucked");


        ServerSocket ssock = null;
        try{
        ssock = new ServerSocket(5000);
        ssock.setSoTimeout(1000);
        Socket socket = ssock.accept();

        System.out.println("Server socket connection accepted!");
        System.out.println("IP given to serverRun: " + IP);
        System.out.println("IP from connection: " + socket.getInetAddress().getHostAddress());


        if(IP.equals(socket.getInetAddress().getHostAddress())) {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            //Get socket's output stream
            OutputStream os = socket.getOutputStream();

            //Read File Contents into contents array
            byte[] contents;
            long fileLength = file.length();
            long current = 0;

            long start = System.nanoTime();
            while (current != fileLength) {
                int size = 10000;
                if (fileLength - current >= size)
                    current += size;
                else {
                    size = (int) (fileLength - current);
                    current = fileLength;
                }
                contents = new byte[size];
                bis.read(contents, 0, size);
                os.write(contents);
                System.out.print("Sending file ... " + (current * 100) / fileLength + "% complete!");
            }

            os.flush();
            //File transfer done. Close the socket connection!
        }
            socket.close();
            ssock.close();
            msg.stopConnection();
            System.out.println("File sent succesfully!");
        } else {
            System.out.println("Server IP komt niet overeen");
        }
    }

    public static boolean available(int port) {

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }
}