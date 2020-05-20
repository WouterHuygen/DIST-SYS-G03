package group1.dist.naming;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import group1.dist.model.Node;
import group1.dist.model.StatusObject;

@CrossOrigin
@RestController
public class NamingController {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private MapManager mapManager;

    @GetMapping("/resolve")
    public StatusObject<Node> ResolveNaming(@RequestParam(value = "filename") String filename)  {
        Node node = mapManager.findNodeIp(filename);
        StatusObject<Node> result = new StatusObject<>(node != null, "IP found", node);
        return result; //TODO: map to JSON?
    }

    @GetMapping("/nodes")
    public StatusObject<HashMap<String, Node>> GetMap(){
        StatusObject<HashMap<String, Node>> result = new StatusObject<>(true, "Node collection", mapManager.getMap());
        return result;
    }

    @PostMapping("/nodes")
    public StatusObject<Node> AddNode(@RequestParam(value = "name") String nodeName, @RequestParam(value = "ip") String ipAddress){ //TODO: maybe json body?
        Node node = new Node(nodeName, ipAddress);
        boolean result = mapManager.addNode(node);
        String status;
        if (result){
            status = "Node added to map";
        }
        else {
            status = "Node Already exists";
        }
        return new StatusObject<Node>(result, status, node);
    }

    @DeleteMapping("/nodes")
    public StatusObject<Node> RemoveNode(@RequestParam(value = "name") String nodeName){
        String status;
        Node node = mapManager.deleteNode(nodeName);
        if (node != null)
            status = "Removed Node";
        else
            status = "Node does not exist";
        System.out.println("Removing response: " + status);
        return new StatusObject<Node>(node != null, status, node);
    }
}
