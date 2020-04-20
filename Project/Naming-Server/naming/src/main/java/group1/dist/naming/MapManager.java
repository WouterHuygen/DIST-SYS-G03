package group1.dist.naming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.print.DocFlavor;
import java.io.*;
import java.util.HashMap;
import java.util.Set;

public class MapManager {
    private final String filename = "map.json";
    private HashMap<String, Node> map = new HashMap<>();

    MapManager() {
        getMap();
    }

    boolean addNode(Node node) {
        String key = Integer.toString(node.getId());
        if (map.containsKey(key)) return false;
        map.put(key, node);
        return saveMap(); //TODO: enum for succes, key already exists, failed saving map?
    }

    Node deleteNode(String nodeName){
        String key = Integer.toString(calculateHash(nodeName));
        if (map.containsKey(key)){
            Node node = map.get(key);
            map.remove(key);
            return node;
        }
        return null;
    }

    static int calculateHash(String name) {
        /*int hash = name.hashCode() % 32768;
        int hash2 = name.hashCode() & (32768 - 1);
        int hash3 = hash*hash2;
        int hash4 = hash3 % 32768;
        return hash4;*/
        int hash = (name.hashCode() & (32768-1));
        return ((hash * hash) & (32768 - 1)); // & (32768 - 1) = performance % 32768
    }

    Node findNodeIp(String filename) {
        return findNodeIp(calculateHash(filename));
    }

    Node findNodeIp(int hash) {
        Set<String> keys = map.keySet();
        int highestKey = 0;
        int smallestDiffKey = 0;
        for (String key : keys) {
            int keyHash = Integer.parseInt(key); //TODO: save names as keys and calculate hash here everytime?
            if (keyHash < hash && keyHash > smallestDiffKey)
                smallestDiffKey = keyHash;
            if (keyHash > highestKey)
                highestKey = keyHash;
        }

        if (smallestDiffKey == 0) smallestDiffKey = highestKey;

        return map.get(Integer.toString(smallestDiffKey));
    }

    HashMap<String, Node> getMap() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonString = readFromFile();
            if (!jsonString.equals(""))
                map = mapper.readValue(jsonString, new TypeReference<HashMap<String, Node>>(){}); //TODO: maps to hasmap<string,string> => problem
            else map = new HashMap<>();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return map;
    }

    private boolean saveMap(){
        ObjectMapper mapper = new ObjectMapper();
        boolean success = true;
        try {
            String jsonMap = mapper.writeValueAsString(map);
            byte [] byteMap = mapper.writeValueAsBytes(map);
            String test = new String(byteMap);
            success = writeToFile(jsonMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    private boolean writeToFile(String input){
        boolean success = true;
        File f = new File(filename);
        try (FileOutputStream o = new FileOutputStream(f)) {
            o.write(input.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    private String readFromFile() {
        String output = "";
        File f = new File(filename);
        try (FileInputStream i = new FileInputStream(f)) {
            byte[] byteOutput = new byte[i.available() + 5];
            i.read(byteOutput);
            output = new String(byteOutput);
        }catch (FileNotFoundException ignored){
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }
}
