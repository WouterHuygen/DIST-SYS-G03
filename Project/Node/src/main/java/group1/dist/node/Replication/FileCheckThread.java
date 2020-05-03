package group1.dist.node.Replication;

import java.io.File;
import java.util.ArrayList;

public class FileCheckThread implements Runnable {

    private long sleepDuration;
    private String path;
    public FileCheckThread(String _path, long _sleepDuration){
        sleepDuration = _sleepDuration;
        path = _path;
    }

    @Override
    public void run() {
        ArrayList<File> newFileList;
        int counter = 10;

        while (counter-- > 0) {
            try {
                newFileList = listLastModifiedFiles(new File(path), sleepDuration);

                for (File File : newFileList)
                    System.out.println(File.getName());

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
}
