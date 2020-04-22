package group1.dist.naming;

public class Node {
    private int id;
    private String name;
    private String ip;

    public Node() {
    }

    public Node(String name, String ip) {
        this.name = name;
        this.id = MapManager.calculateHash(name);
        this.ip = ip;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public void setName(String name) {
        this.name = name;
        this.id = MapManager.calculateHash(name);
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
