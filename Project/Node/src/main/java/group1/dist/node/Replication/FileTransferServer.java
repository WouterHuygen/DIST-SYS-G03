package group1.dist.node.Replication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class FileTransferServer {

    public void ServerRun(String filename, String IP) throws Exception {

        NetworkInterface networkInterface = NetworkInterface.getByName("ethwe0");

        System.out.println("Server startup!");

        //Initialize Sockets
        TCPMessage msg = new TCPMessage();
        System.out.println("ip used for TCP: " + IP);
        msg.startConnection(IP, 5556);
        msg.sendReplicationMessage(networkInterface.getInetAddresses().nextElement().getHostAddress(), filename);
        msg.stopConnection();

        ServerSocket ssock = new ServerSocket(5000);
        Socket socket = ssock.accept();

        if(IP.equals(socket.getInetAddress().getHostAddress())){
            //Specify the file
            File file = new File("/home/pi/node/ownFiles/" + filename);
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            //Get socket's output stream
            OutputStream os = socket.getOutputStream();

            //Read File Contents into contents array
            byte[] contents;
            long fileLength = file.length();
            long current = 0;

            long start = System.nanoTime();
            while(current!=fileLength){
                int size = 10000;
                if(fileLength - current >= size)
                    current += size;
                else{
                    size = (int)(fileLength - current);
                    current = fileLength;
                }
                contents = new byte[size];
                bis.read(contents, 0, size);
                os.write(contents);
                System.out.print("Sending file ... "+(current*100)/fileLength+"% complete!");
            }

            os.flush();
            //File transfer done. Close the socket connection!
            socket.close();
            ssock.close();
            System.out.println("File sent succesfully!");
        }


    }
}