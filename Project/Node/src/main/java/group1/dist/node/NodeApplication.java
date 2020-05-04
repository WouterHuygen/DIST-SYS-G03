package group1.dist.node;

import group1.dist.node.Replication.APICall;
import group1.dist.node.Replication.FileCheckThread;
import group1.dist.node.Replication.FileTransferServer;
import group1.dist.node.Replication.TCPListenerThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

@SpringBootApplication
public class NodeApplication {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ApplicationArguments args;

    public static void main(String[] args) {
        SpringApplication.run(NodeApplication.class, args);
        //first replication when joining the network
        startReplication();
        //thread to check new files or file changes
        Runnable fileChecker = new FileCheckThread("/home/pi/node/ownFiles", 10000);
        new Thread(fileChecker).start();
        //thread to check incoming TCP messages
        Thread tcpThread = new Thread(new TCPListenerThread());
        tcpThread.start();
    }

    @Bean
    public NodeInfo nodeInfo(){
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getAddress().toString();
        } catch (UnknownHostException e) {
            System.out.println("Failed to obtain host ip address");
            ip = "0.0.0.0";
            e.printStackTrace();
        }
        if (args.containsOption("name")){
            String name = args.getOptionValues("name").get(0);
            System.out.println("Creating nodeInfo object with name: " + name);
            return new NodeInfo(new Node(name, ip));
        }
        String standardName = "StandardNodeName"; //TODO: standard node name
        System.out.println("Creating nodeInfo object with name: " + standardName);
        return new NodeInfo(new Node(standardName, ip));
    }

    @Bean
    public DiscoveryService discoveryService(){
        return new DiscoveryService();
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
        UDPListenThread thread = new UDPListenThread(context);
        thread.start();
    }


    private static void startReplication(){
        ArrayList<File> newFileList = new ArrayList<File>();
        File folder = new File("/home/pi/node/ownFiles");
        if(folder.listFiles() != null) {
            for (File fileEntry : folder.listFiles()){
                String ip = APICall.Call(fileEntry.getName());

                //TODO: check on own ip


                if(ip != null){
                    try{
                        FileTransferServer.ServerRun(fileEntry.getName(), ip);
                    } catch(Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}
