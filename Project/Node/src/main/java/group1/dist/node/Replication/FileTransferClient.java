package group1.dist.node.Replication;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class FileTransferClient {

    public static void clientRun(String ip, String fileName) throws Exception {
        System.out.println("Client startup!");

        if(available(5000))
            System.out.println("port free");
        else
            System.out.println("fucked");

        System.out.println("IP in filetransferclient: " + InetAddress.getByName(ip));
        //Initialize socket
        Socket socket = new Socket(InetAddress.getByName(ip), 5000);
        byte[] contents = new byte[10000];

        String filePath = "/home/pi/node/replicatedFiles/" + fileName;
        //Initialize the FileOutputStream to the output file's full path.
        FileOutputStream fos = new FileOutputStream(filePath);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        InputStream is = socket.getInputStream();

        //No of bytes read in one read() call
        int bytesRead = 0;

        while((bytesRead=is.read(contents))!=-1)
            bos.write(contents, 0, bytesRead);

        bos.flush();
        socket.close();

        //Update log file.
        //FileLogHandler logHandler = new FileLogHandler();
        //logHandler.updateFileLog(filePath);

        System.out.println(fileName + " saved successfully!");
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
