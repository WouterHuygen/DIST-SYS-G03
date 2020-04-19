package group1.dist.naming;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class NamingController {
    private MapManager mapManager = new MapManager();

    NamingController(){
        mapManager.addNode("Node1", "192.168.0.1");
        mapManager.addNode("Node2", "192.168.0.2");
    }

    @GetMapping("/resolve")
    public String ResolveNaming(@RequestParam(value = "filename") String filename)  {
        return mapManager.findNodeIp(filename);
    }

    @GetMapping("/map")
    public HashMap<String, String> GetMap(){
        HashMap<String, String> map = mapManager.getMap();
        return map;
    }

    @PutMapping("/nodes")
    public String AddNode(@RequestParam(value = "name") String nodeName, @RequestParam(value = "ip") String ipAddress){
        String ret;
        if (mapManager.addNode(nodeName, ipAddress)){
            ret = "{\"status\": \"OK\"}";
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
