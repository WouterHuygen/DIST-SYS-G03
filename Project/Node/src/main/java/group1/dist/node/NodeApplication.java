package group1.dist.node;

import group1.dist.node.Replication.FileCheckThread;
import group1.dist.node.Replication.TCPListenerThread;
import group1.dist.model.NodeInfo;
import group1.dist.model.Node;
import group1.dist.node.Replication.*;
import group1.dist.discovery.DiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Objects;

@SpringBootApplication
public class NodeApplication {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ApplicationArguments args;

    public static void main(String[] args) {
        SpringApplication.run(NodeApplication.class, args);

    }

    @Bean
    public NodeInfo nodeInfo(){
        String ip = null;
        try {
            NetworkInterface networkInterface = NetworkInterface.getByName("ethwe0");
            if(networkInterface != null)
                ip = networkInterface.getInetAddresses().nextElement().getHostAddress();
       } catch (Exception s){
            System.out.println("Failed to obtain host ip address");
            s.printStackTrace();
        }
        if (ip == null || ip.equals("0.0.0.0")) ip = "127.0.0.1";
        if (args.containsOption("name")){
            String name = args.getOptionValues("name").get(0);
            System.out.println("Creating nodeInfo object with name: " + name);
            return new NodeInfo(new Node(name, ip));
        }
        String standardName; //TODO: standard node name
        try {
            standardName = InetAddress.getLocalHost().getHostName();
            System.out.println("Creating nodeInfo object with name: " + standardName);
        } catch (UnknownHostException e) {
            standardName = "StandardNodeName";
            e.printStackTrace();
        }
        return new NodeInfo(new Node(standardName, ip));
    }


    @Bean
    public void tcpMessage(){
        //thread to check incoming TCP messages
        Thread tcpThread = new Thread(new TCPListenerThread(context.getBean(NodeInfo.class)));
        tcpThread.start();
    }

    @Bean
    public DiscoveryService discoveryService(){
        return new DiscoveryService(context.getBean(NodeInfo.class));
    }

    @Bean
    public FileReplicationHandler replicationHandler() {
        return new FileReplicationHandler(context.getBean(NodeInfo.class));
    }

    @Bean
    public void startDiscovery(){
        if (discoveryService().sendJoin()) {
            System.out.println("join sent successfully");
        }
        else {
            System.out.println("join failed");
        }
        System.out.println("started listening");
        NodeUDPListener listener = new NodeUDPListener(context.getBean(NodeInfo.class));
        new Thread(listener).start();
    }

    @Bean
    public void startReplication(){
        File folder = new File("/home/pi/node/ownFiles");
        FileLogHandler logHandler = new FileLogHandler();
        if(folder.listFiles() != null) {
            for (File fileEntry : Objects.requireNonNull(folder.listFiles())){
                //Create log file
                logHandler.createNewFileLog(fileEntry.getPath(), context.getBean(NodeInfo.class).getSelf().getIp());
                //Replicate file ==> log file is automatically replicated when created
                replicationHandler().replicateFile(fileEntry);
            }
        }
    }

    @PreDestroy
    public void onExit(){
        System.out.println("Starting shutdown procedure");
        try {
            discoveryService().shutdown(context.getBean(NodeInfo.class));

            TCPMessage msg = new TCPMessage();
            msg.startConnection(nodeInfo().getNextNode().getIp(), 5556);
            msg.sendShutdownMessageToNextNode(nodeInfo());
            msg.stopConnection();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            msg.startConnection(nodeInfo().getPreviousNode().getIp(), 5556);
            msg.sendShutdownMessageToPreviousNode(nodeInfo());
            msg.stopConnection();

            replicationHandler().shutDown();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Bean
    public void startFileChecker(){
        //thread to check new files or file changes
        Runnable fileChecker = new FileCheckThread("/home/pi/node/ownFiles", 10000, context.getBean(NodeInfo.class));
        new Thread(fileChecker).start();
    }
}
