package group1.dist.naming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.util.HashMap;
import java.util.Set;

import group1.dist.model.Node;

public class MapManager {
    private final String filename = "map.json";
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
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
        String key = Integer.toString(Node.calculateHash(nodeName));
        if (map.containsKey(key)){
            Node node = map.get(key);
            map.remove(key);
            saveMap();
            System.out.println("map updated with delete");
            return node;
        }
        return null;
    }

    Node findNodeIp(String filename) {
        return findNodeIp(Node.calculateHash(filename));
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

    HashMap<String, Node> getMap() { //TODO: not read everytime, only on startup
        try {
            String jsonString = readFromFile();
            if (!jsonString.equals(""))
                map = mapper.readValue(jsonString, new TypeReference<HashMap<String, Node>>(){});
            else map = new HashMap<>();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return map;
    }

    private boolean saveMap(){
        boolean success = true;
        try {
            String jsonMap = mapper.writeValueAsString(map);
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
