package group1.dist.node.Replication;

import group1.dist.model.NodeInfo;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class FileCheckThread implements Runnable {

    private long sleepDuration;
    private String path;
    private static ArrayList<File> oldFileList = new ArrayList<File>();
    private NodeInfo nodeInfo;
    private FileLogHandler fileLogHandler;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public FileCheckThread(String _path, long _sleepDuration, NodeInfo nodeInfo){
        sleepDuration = _sleepDuration;
        path = _path;
        this.nodeInfo = nodeInfo;
        this.fileLogHandler = new FileLogHandler();
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

    public ArrayList<File> listLastModifiedFiles(File folder, long sleepDuration) throws Exception {
        ArrayList<File> newFileList = new ArrayList<File>();
        if(folder.listFiles() != null) {
            for (File fileEntry : Objects.requireNonNull(folder.listFiles())){
                //check if file was edited/added between executions of the code
                if ((System.currentTimeMillis() - fileEntry.lastModified()) <= sleepDuration){
                    newFileList.add(fileEntry);
                    if(!fileEntry.getName().split("\\.")[1].equals("json")){
                        LocalDateTime now = LocalDateTime.now();
                        if(!fileLogHandler.logFileExists(fileEntry.getPath())) {
                            fileLogHandler.createNewFileLog(fileEntry.getPath(), nodeInfo.getSelf().getIp());
                        }
                        this.fileLogHandler.updateFileLog(fileEntry.getPath(), fileEntry.getName(), dtf.format(now));

                    }
                }
            }
        }

        return newFileList;
    }

    private ArrayList<File> listDeletedFiles(File folder) throws Exception {

        ArrayList<File> newFileList = new ArrayList<File>();
        ArrayList<File> deletedFileList = new ArrayList<File>();

        //Add all the files found in the directory to the array.
        if(folder.listFiles() != null) {
            newFileList.addAll(Arrays.asList(Objects.requireNonNull(folder.listFiles())));
        }

        //Check which files are in the oldFileList but not in newFileList ==> these files were deleted.
        for(File fileEntry: oldFileList){
            if(!newFileList.contains(fileEntry))
                deletedFileList.add(fileEntry);
        }
        oldFileList = newFileList;

        return deletedFileList;
    }
}
