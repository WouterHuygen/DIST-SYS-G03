package group1.dist.node.Replication;

import java.util.ArrayList;
import java.util.List;

public class FileLogObject {
    private String name;
    private String downloadLocation;
    private List<String> replicatedToNodes;
    private List<String> fileUpdates;
    private String logPath;


    public FileLogObject(){
        replicatedToNodes = new ArrayList<String>();
        fileUpdates = new ArrayList<String>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDownloadLocation(){
        return downloadLocation;
    }

    public void setDownloadLocation(String _downloadLocation){
        this.downloadLocation = _downloadLocation;
    }

    public List<String> getReplicatedToNodes() {
        return replicatedToNodes;
    }

    public void addReplicatedToNode(String replicatedNode) {
        this.replicatedToNodes.add(replicatedNode);
    }

    public List<String> getFileUpdates() {
        return fileUpdates;
    }

    public void addFileUpdate(String fileUpdate) {
        this.fileUpdates.add(fileUpdate);
    }


}
