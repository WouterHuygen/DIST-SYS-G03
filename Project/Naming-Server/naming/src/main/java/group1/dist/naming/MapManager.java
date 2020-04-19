package group1.dist.naming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.HashMap;
import java.util.Set;

public class MapManager {
    final String filename = "map.json";
    private HashMap<String, String> map = new HashMap<>();

    MapManager() {
        getMap();
    }

    boolean addNode(String node, String ipAddress) {
        String key = Integer.toString(calculateHash(node));
        if (map.containsKey(key)) return false;
        map.put(key, ipAddress);
        saveMap();
        return true;
    }

    boolean deleteNode(String node){
        String key = Integer.toString(calculateHash(node));
        if (map.containsKey(key)){
            map.remove(key);
            return true;
        }
        return false;
    }

    private int calculateHash(String name) {
        /*int hash = name.hashCode() % 32768;
        int hash2 = name.hashCode() & (32768 - 1);
        int hash3 = hash*hash2;
        int hash4 = hash3 % 32768;
        return hash4;*/
        int hash = (name.hashCode() & (32768-1));
        return ((hash * hash) & (32768 - 1)); // & (32768 - 1) = performance % 32768
    }

    public String findNodeIp(String filename) {
        return findNodeIp(calculateHash(filename));
    }

    public String findNodeIp(int hash) {
        Set<String> keys = map.keySet();
        int highestKey = 0;
        int smallestDiffKey = 0;
        for (String key : keys) {
            int keyHash = Integer.parseInt(key);
            if (keyHash < hash && keyHash > smallestDiffKey)
                smallestDiffKey = keyHash;
            if (keyHash > highestKey)
                highestKey = keyHash;
        }

        if (smallestDiffKey == 0) smallestDiffKey = highestKey;

        return map.get(Integer.toString(smallestDiffKey));
    }

    HashMap<String, String> getMap() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonString = readFromFile();
            if (!jsonString.equals(""))
                map = (HashMap<String, String>) mapper.readValue(jsonString, map.getClass()); //TODO: maps to hasmap<string,string> => problem
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
            byte[] byteOutput = new byte[100];
            i.read(byteOutput);
            output = new String(byteOutput);
        }catch (FileNotFoundException ignored){
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }
}