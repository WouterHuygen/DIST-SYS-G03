package group1.dist.naming;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class NamingController {
    private MapManager mapManager = new MapManager();

    NamingController(){
        mapManager.addNode(new Node("node1", "10.0.3.15"));
        mapManager.addNode(new Node("node2", "10.0.3.17"));
        //TODO: dependency injection for MapManager?
    }

    @GetMapping("/resolve")
    public Node ResolveNaming(@RequestParam(value = "filename") String filename)  {
        return mapManager.findNodeIp(filename); //TODO: map to JSON?
    }

    @GetMapping("/nodes")
    public HashMap<String, Node> GetMap(){
        HashMap<String, Node> map = mapManager.getMap();
        return map;
    }

    @PostMapping("/nodes")
    public String AddNode(@RequestParam(value = "name") String nodeName, @RequestParam(value = "ip") String ipAddress){ //TODO: maybe json body?
        String ret;
        if (mapManager.addNode(new Node(nodeName, ipAddress))){
            ret = "{\"status\": \"OK\"}"; //TODO: actual JSON objects?
        }
        else {
            ret = "{\"status\": \"Node already exists\"}";
        }
        return ret;
    }

    @DeleteMapping("/nodes")
    public String removeNode(@RequestParam(value = "name") String nodeName){
        String ret;
        if (mapManager.deleteNode(nodeName))
            ret = "{\"status\": \"Removed " + nodeName + "\"}";
        else
            ret = "{\"status\": \"Node does not exist\"}";
        return ret;
    }
}
