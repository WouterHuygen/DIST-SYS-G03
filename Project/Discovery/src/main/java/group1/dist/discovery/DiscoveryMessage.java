package group1.dist.discovery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DiscoveryMessage {
    private MessageType type;
    private String name;
    private String ip;
    private int existingNodes;
    private String newHostname;
    private String filename;

    public DiscoveryMessage() {
    }

    public DiscoveryMessage(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getExistingNodes() {
        return existingNodes;
    }

    public void setExistingNodes(int existingNodes) {
        this.existingNodes = existingNodes;
    }

    public String getNewHostname() {
        return newHostname;
    }

    public void setNewHostname(String newHostname) {
        this.newHostname = newHostname;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper(); //TODO: in this class??
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Type: " + type + "\nInfo: " + (name.equals("")? "": name + "; ") + (ip.equals("")? "": ip + ";")
                    + (existingNodes == 0? "": " " + existingNodes + "; ") + (newHostname.equals("")? "": newHostname + ";");
        }
    }
}
