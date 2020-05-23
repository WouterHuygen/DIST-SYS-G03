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
        String ip = apiCall.call(file.getName());



        if(ip != null){
            if(ip.equals(nodeInfo.getSelf().getIp())){
                System.out.println("OWN IP");
                if(nodeInfo.getPreviousNode() != null && nodeInfo.getPreviousNode() != nodeInfo.getSelf())
                    ip = nodeInfo.getPreviousNode().getIp();
                else{
                    System.out.println("No previous node, no replication needed");
                    ip = "0";
                }
            }
            else{
                //check whether file will replicate to original owner of file
                String logPath;
                FileLogObject log;
                ObjectMapper objectMapper = new ObjectMapper();

                if(!file.getName().contains(".json")){
                    String filePath = file.getPath();
                    logPath = filePath.substring(0, filePath.lastIndexOf('.'));
                    logPath = logPath+".json";
                }
                else{
                    logPath = file.getPath();
                }

                try {
                    log = objectMapper.readValue(new File(logPath), FileLogObject.class);
                    if(ip.equals(log.getDownloadLocation())){
                        if(nodeInfo.getPreviousNode() != null && nodeInfo.getPreviousNode() != nodeInfo.getSelf() && nodeInfo.getNextNode() != nodeInfo.getPreviousNode())
                            //More than 2 node in network
                            if(!nodeInfo.getPreviousNode().getIp().equals(log.getDownloadLocation())){
                                //Previous isn't the same as original location
                                ip = nodeInfo.getPreviousNode().getIp();
                            }
                            else{
                                //Replicate to previous of previous
                                ip = apiCall.getPreviousNode();
                            }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try{
                if(!ip.equals("0")){
                    FileTransferServer fileTransferServer = new FileTransferServer();
                    fileTransferServer.serverRun(file, ip);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
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
