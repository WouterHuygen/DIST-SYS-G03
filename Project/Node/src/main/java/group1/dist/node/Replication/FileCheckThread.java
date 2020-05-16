package group1.dist.node.Replication;

import group1.dist.model.NodeInfo;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.util.ArrayList;

public class FileCheckThread implements Runnable {

    private long sleepDuration;
    private String path;
    private static ArrayList<File> oldFileList = new ArrayList<File>();
    private NodeInfo nodeInfo;
    //TODO: context ? is dat oke als die gewoon hier ook meegegeven wordt ?
    public FileCheckThread(String _path, long _sleepDuration, NodeInfo nodeInfo){
        sleepDuration = _sleepDuration;
        path = _path;
        this.nodeInfo = nodeInfo;
    }

    @Override
    public void run() {
        ArrayList<File> newFileList;
        ArrayList<File> deletedFileList;
        FileReplicationHandler replicationHandler = new FileReplicationHandler(nodeInfo);

        while (true) {
            try {
                newFileList = listLastModifiedFiles(new File(path), sleepDuration);
                deletedFileList = listDeletedFiles(new File(path));

                //print new files
                for (File file : newFileList) {
                    System.out.println("New/Updated file: " + file.getName());
                    replicationHandler.replicateFile(file);
                }

                //print deleted files
                for (File file: deletedFileList) {
                    System.out.println("Deleted file: " + file.getName());
                    replicationHandler.deleteFile(file);
                }

                Thread.sleep(sleepDuration);

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<File> listLastModifiedFiles(File folder, long sleepDuration) throws Exception {

        ArrayList<File> newFileList = new ArrayList<File>();
        if(folder.listFiles() != null) {
            for (File fileEntry : folder.listFiles())
                if ((System.currentTimeMillis() - fileEntry.lastModified()) <= sleepDuration)
                    newFileList.add(fileEntry);
        }

        return newFileList;
    }

    private static ArrayList<File> listDeletedFiles(File folder) throws Exception {

        ArrayList<File> newFileList = new ArrayList<File>();
        ArrayList<File> deletedFileList = new ArrayList<File>();

        if(folder.listFiles() != null) {
            for (File fileEntry : folder.listFiles()){
                newFileList.add(fileEntry);
            }
        }

        for(File fileEntry: oldFileList){
            if(!newFileList.contains(fileEntry))
                deletedFileList.add(fileEntry);
        }
        oldFileList = newFileList;

        return deletedFileList;
    }
}
