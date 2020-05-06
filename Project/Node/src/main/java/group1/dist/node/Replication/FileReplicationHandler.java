package group1.dist.node.Replication;


import group1.dist.node.NodeInfo;
import org.springframework.context.ApplicationContext;

import java.io.File;

public class FileReplicationHandler {
    private ApplicationContext context;
    private TCPMessage tcpMessage;
    public FileReplicationHandler(ApplicationContext _context){
        context = _context;
        tcpMessage = new TCPMessage();
    }

    public void ReplicateFile(File file){
        String ip = APICall.Call(file.getName());

        NodeInfo nodeInfo = context.getBean(NodeInfo.class);
        //TODO: check if there is a previous node
        if(ip != null){
            if(ip.equals(nodeInfo.getSelf().getIp())){
                System.out.println("OWN IP");
                if(nodeInfo.getPreviousNode() != null)
                    ip = nodeInfo.getPreviousNode().getIp();
                System.out.println("New IP: " + ip);
            }
            try{
                FileTransferServer fileTransferServer = new FileTransferServer();
                fileTransferServer.ServerRun(file.getName(), ip);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void DeleteFile(File file){
        String ip = APICall.Call(file.getName());

        NodeInfo nodeInfo = context.getBean(NodeInfo.class);
        //TODO: check if there is a previous node
        if(ip != null){
            if(ip.equals(nodeInfo.getSelf().getIp())){
                System.out.println("OWN IP");

                if(nodeInfo.getPreviousNode() != null)
                    ip = nodeInfo.getPreviousNode().getIp();
                System.out.println("New IP: " + ip);
            }
            try{
                System.out.println("ip used for TCP: " + ip);
                tcpMessage.startConnection(ip, 5556);
                tcpMessage.sendDeleteMessage(file.getName());
                tcpMessage.stopConnection();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }


}
