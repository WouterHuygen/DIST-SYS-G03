package group1.dist.node.Replication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPListenerThread implements Runnable {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;

    public void stop() {
        try {
            in.close();
            clientSocket.close();
            serverSocket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Started TCP Listener");
        while(true) {
            try{
                serverSocket = new ServerSocket(5556);
                clientSocket = serverSocket.accept();
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message = in.readLine();
                String[] split_message = message.split(" ");
                System.out.println("Incoming TCP message: " + message);
                stop();
                //TODO: verplaatsen naar een aparte klasse ? Messagehandler ?
                if (split_message[0].equals("replication")) {
                    FileTransferClient.ClientRun(split_message[1], split_message[2]);
                } else if (split_message[0].equals("delete")){
                    File file = new File("/home/pi/node/replicatedFiles/" + split_message[1]);
                    if(file.delete())
                        System.out.println(file.getName() + " is deleted successfully.");
                    else
                        System.out.println("Failed to delete " + file.getName());
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
