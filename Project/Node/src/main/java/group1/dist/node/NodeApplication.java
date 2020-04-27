package group1.dist.node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NodeApplication {

    @Autowired
    private ApplicationArguments args;

    public static void main(String[] args) {
        SpringApplication.run(NodeApplication.class, args);
    }

    @Bean
    public NodeInfo nodeInfo(){
        if (args.containsOption("name")){
            String name = args.getOptionValues("name").get(0);
            System.out.println("Creating nodeInfo object with name: " + name);
            return new NodeInfo(name);
        }
        String standardName = "StandardNodeName"; //TODO: standard node name
        System.out.println("Creating nodeInfo object with name: " + standardName);
        return new NodeInfo(standardName);
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
        UDPListenThread thread = new UDPListenThread();
        thread.start();
    }
}
