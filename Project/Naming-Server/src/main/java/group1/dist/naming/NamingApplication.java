package group1.dist.naming;

import group1.dist.model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import group1.dist.model.NodeInfo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

@SpringBootApplication
public class NamingApplication {

    @Autowired
    private ApplicationArguments args;

    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(NamingApplication.class, args);
    }

    @Bean
    public MapManager mapManager(){
        return new MapManager();
    }

    @Bean
    public NodeInfo nodeInfo(){
        String ip = null;
        try {
            NetworkInterface networkInterface = NetworkInterface.getByName("ethwe0");
            if(networkInterface != null)
                ip = networkInterface.getInetAddresses().nextElement().getHostAddress();
            else
                ip = "0.0.0.0";
        } catch (Exception s){
            System.out.println("Failed to obtain host ip address");
            ip = "0.0.0.0";
            s.printStackTrace();
        }
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
    public DiscoveryService discoveryService(){
        return new DiscoveryService();
    }

    @Bean
    public void startDiscovery(){
        System.out.println("started listening");
        NamingUDPListener listener = new NamingUDPListener(context);
        new Thread(listener).start();
    }
}
