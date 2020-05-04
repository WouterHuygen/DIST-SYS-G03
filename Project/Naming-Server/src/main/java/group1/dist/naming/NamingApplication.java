package group1.dist.naming;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

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
        System.out.println("started listening");
        UDPListener listener = new UDPListener(context);
        new Thread(listener).start();
    }
}
