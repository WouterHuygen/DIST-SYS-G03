package group1.dist.node.Replication;

import com.fasterxml.jackson.databind.ObjectMapper;
import group1.dist.model.NodeInfo;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class FileReplicationHandler {
    private final NodeInfo nodeInfo;
    private final APICall apiCall;
    private final TCPMessage tcpMessage;
    public FileReplicationHandler(NodeInfo nodeInfo){
        this.nodeInfo = nodeInfo;
        apiCall = new APICall(nodeInfo);
        tcpMessage = new TCPMessage();
    }

    public void replicateFile(File file){
        if(!file.getPath().endsWith(".json")) {
            String ip = apiCall.call(file.getName());
            FileLogHandler logHandler = new FileLogHandler();
            if (ip != null) {
                if (ip.equals(nodeInfo.getSelf().getIp())) {
                    if (nodeInfo.getPreviousNode() != null && !nodeInfo.getPreviousNode().getIp().equals(nodeInfo.getSelf().getIp()))
                        ip = nodeInfo.getPreviousNode().getIp();
                    else {
                        ip = "0";
                    }
                } else {
                    //check whether file will replicate to original owner of file
                    FileLogObject logObject = logHandler.fileToLogObject(file.getPath());
                    System.out.println("ip " + ip);
                    System.out.println("Download " + logObject.getDownloadLocation());
                    if (ip.equals(logObject.getDownloadLocation())) {
                        System.out.println(nodeInfo.getPreviousNode().getIp());
                        System.out.println(nodeInfo.getSelf().getIp());
                        System.out.println(nodeInfo.getNextNode().getIp());
                        if (nodeInfo.getPreviousNode() != null && !nodeInfo.getPreviousNode().getIp().equals(nodeInfo.getSelf().getIp()) && !nodeInfo.getNextNode().getIp().equals(nodeInfo.getPreviousNode().getIp())){
                            //More than 2 nodes in network
                            if (!nodeInfo.getPreviousNode().getIp().equals(logObject.getDownloadLocation())) {
                                //Previous isn't the same as original location
                                ip = nodeInfo.getPreviousNode().getIp();
                            } else {
                                //Replicate to previous of previous
                                ip = apiCall.getPreviousNode();
                                System.out.println("ELSE IP: " + ip);
                                ip = !ip.equals(logObject.getDownloadLocation()) ? ip : "0";
                            }
                        }
                        else{
                            //Exactly 2 nodes in the network
                            ip = "0";
                        }
                    }
                }
                //send file first
                System.out.println("IP 0?? " + ip);
                sendFile(file, ip);
                try {
                    //Wait for first file to be sent and socket to be closed
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //send logfile after file is sent
                sendFile(logHandler.getLogFile(file.getPath()), ip);

                try {
                    //Wait for first file to be sent and socket to be closed
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void sendFile(File file, String ip){
        try{
            if(!ip.equals("0")){
                FileTransferServer fileTransferServer = new FileTransferServer();
                fileTransferServer.serverRun(file, ip);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void deleteFile(File file){
        String ip = apiCall.call(file.getName());

        if(ip != null){
            if(ip.equals(nodeInfo.getSelf().getIp())){
                System.out.println("OWN IP");

                if(nodeInfo.getPreviousNode() != null && nodeInfo.getPreviousNode() != nodeInfo.getSelf())
                    ip = nodeInfo.getPreviousNode().getIp();
                else{
                    System.out.println("No previous node, no need to delete replicated file");
                    ip = "0";
                }
            }
            try{
                if(!ip.equals("0")){
                    System.out.println("ip used for TCP: " + ip);
                    tcpMessage.startConnection(ip, 5556);
                    tcpMessage.sendDeleteMessage(file.getName());
                    tcpMessage.stopConnection();
                    System.out.println("Delete message sent!");
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void shutDown(){
        //Delete all own files from the network
        File folder = new File("/home/pi/node/ownFiles");
        if(folder.listFiles() != null) {
            for (File fileEntry : Objects.requireNonNull(folder.listFiles())){
                deleteFile(fileEntry);
                //Delete json log files
                String[] split = fileEntry.getName().split("\\.");
                if(split[1].equals("json")) {
                    if (fileEntry.delete())
                        System.out.println("Successfully deleted: " + fileEntry.getName());
                    else
                        System.out.println("Failed to delete: " + fileEntry.getName());
                }
            }
        }

        //Replicate files, that were replicated to this node, to other nodes
        folder = new File("/home/pi/node/replicatedFiles");
        if(folder.listFiles() != null) {
            for (File fileEntry : Objects.requireNonNull(folder.listFiles())){
                replicateFile(fileEntry);
            }
        }
    }
}
