package group1.dist.node;

public class NodeInfo {
    private String nodeName;
    private String previousNode;
    private String nextNode;

    public NodeInfo(String nodeName) {
        this.nodeName = nodeName;
    }

    public NodeInfo(String nodeName, String previousNode, String nextNode) {
        this.nodeName = nodeName;
        this.previousNode = previousNode;
        this.nextNode = nextNode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(String previousNode) {
        this.previousNode = previousNode;
    }

    public String getNextNode() {
        return nextNode;
    }

    public void setNextNode(String nextNode) {
        this.nextNode = nextNode;
    }
}
