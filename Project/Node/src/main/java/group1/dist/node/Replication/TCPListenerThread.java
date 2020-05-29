package group1.dist.node.Replication;

import com.fasterxml.jackson.databind.ObjectMapper;
import group1.dist.discovery.DiscoveryMessage;
import group1.dist.model.Node;
import group1.dist.model.NodeInfo;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPListenerThread implements Runnable {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private ObjectMapper objectMapper;

    private NodeInfo nodeInfo;
    public TCPListenerThread(NodeInfo nodeInfo) {
        objectMapper = new ObjectMapper();
        this.nodeInfo = nodeInfo;
    }

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

                String data = in.readLine();
                DiscoveryMessage message = objectMapper.readValue(data, DiscoveryMessage.class);
                stop();
                //TODO: verplaatsen naar een aparte klasse ? Messagehandler ?
                switch (message.getType()) {
                    case REPLICATION_UPDATE:
                        FileTransferClient.clientRun(message.getIp(), message.getFilename());
                        break;
                    case REPLICATION_DELETE:
                        File file = new File("/home/pi/node/replicatedFiles/" + message.getFilename());
                        if(file.delete())
                            System.out.println(file.getName() + " is deleted successfully.");
                        else
                            System.out.println("Failed to delete " + file.getName());
                        break;
                    case NEXT_NODE_UPDATE:
                        Node newNextNode = new Node(message.getName(), message.getIp());
                        nodeInfo.setNextNode(newNextNode);
                        System.out.println("new next node set to: Name:" + nodeInfo.getNextNode().getName() + " IP: " + nodeInfo.getNextNode().getIp());
                        break;
                    case PREVIOUS_NODE_UPDATE:
                        Node newPreviousNode = new Node(message.getName(), message.getIp());
                        nodeInfo.setPreviousNode(newPreviousNode);
                        System.out.println("new previous node set to: Name:" + nodeInfo.getPreviousNode().getName() + " IP: " + nodeInfo.getPreviousNode().getIp());
                        break;
                    default:
                        break;
                }
                /*
                if (split_message[0].equals("replication")) {
                    FileTransferClient.clientRun(split_message[1], split_message[2]);
                } else if (split_message[0].equals("delete")){
                    File file = new File("/home/pi/node/replicatedFiles/" + split_message[1]);
                    if(file.delete())
                        System.out.println(file.getName() + " is deleted successfully.");
                    else
                        System.out.println("Failed to delete " + file.getName());
                }
                 */
            } catch (Exception e){
                e.printStackTrace();
                stop();
                break;
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        stop();
    }
}
