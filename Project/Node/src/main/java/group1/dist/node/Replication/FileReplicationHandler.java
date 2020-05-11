package group1.dist.node.Replication;


import group1.dist.model.NodeInfo;
import org.springframework.context.ApplicationContext;

import java.io.File;

public class FileReplicationHandler {
    private ApplicationContext context;
    private TCPMessage tcpMessage;
    public FileReplicationHandler(ApplicationContext _context){
        context = _context;
        tcpMessage = new TCPMessage();
    }

    public void replicateFile(File file){
        String ip = APICall.call(file.getName());
        NodeInfo nodeInfo = context.getBean(NodeInfo.class);
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
        String ip = APICall.call(file.getName());

        NodeInfo nodeInfo = context.getBean(NodeInfo.class);
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
        File folder = new File("/home/pi/node/ownFiles");
        if(folder.listFiles() != null) {
            for (File fileEntry : folder.listFiles()){
                deleteFile(fileEntry);
            }
        }

        folder = new File("/home/pi/node/replicatedFiles");
        if(folder.listFiles() != null) {
            for (File fileEntry : folder.listFiles()){
                replicateFile(fileEntry);
            }
        }
    }
}
