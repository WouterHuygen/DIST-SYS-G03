package group1.dist.model;

public class NodeInfo {
    private Node self;
    private Node previousNode;
    private Node nextNode;
    private String namingIp;

    public NodeInfo() { }

    public NodeInfo(Node self) {
        this.self = self;
    }

    public NodeInfo(Node self, Node previousNode, Node nextNode) {
        this.self = self;
        this.previousNode = previousNode;
        this.nextNode = nextNode;
    }

    public Node getSelf() {
        return self;
    }

    public void setSelf(Node self) {
        this.self = self;
    }

    public Node getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }

    public Node getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public String getNamingIp() {
        return namingIp;
    }

    public void setNamingIp(String namingIp) {
        this.namingIp = namingIp;
    }
}
