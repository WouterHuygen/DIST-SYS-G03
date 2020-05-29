package group1.dist.node.Replication;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;


public class FileTransferClient {

    public static void clientRun(String ip, String fileName) {
        System.out.println("Client startup!");
        String filePath = "";

        if(available(5000))
            System.out.println("port free");
        else
            System.out.println("fucked");

        try {
            //Initialize socket
            Socket socket = null;
            socket = new Socket(InetAddress.getByName(ip), 5000);

            System.out.println("Client connected");

            byte[] contents = new byte[10000];

            filePath = "/home/pi/node/replicatedFiles/" + fileName;
            //Initialize the FileOutputStream to the output file's full path.
            FileOutputStream fos = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            InputStream is = socket.getInputStream();

            //No of bytes read in one read() call
            int bytesRead = 0;

            while((bytesRead=is.read(contents))!=-1)
                bos.write(contents, 0, bytesRead);

            bos.flush();
            bos.close();
            fos.close();
            socket.close();

            System.out.println("Client closed: " + socket.isClosed());
            System.out.println("Client connected: " + socket.isConnected());

        } catch (UnknownHostException u){
            System.out.println(u);
        } catch (IOException i){
            System.out.println(i);
        }

        //Update log file.
        if(fileName.split("\\.")[1].equals("json")){
            FileLogHandler logHandler = new FileLogHandler();
            try{
                logHandler.updateReplicatedLog(filePath);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

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
