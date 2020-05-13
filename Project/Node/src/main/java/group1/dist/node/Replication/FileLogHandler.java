package group1.dist.node.Replication;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class FileLogHandler {
    public FileLogHandler(){
    }

    public String createNewFileLog(String _filePath, String IP){
        String[] split = _filePath.split("/");
        String[] fileName = split[split.length - 1].split(".");

        FileLogObject fileLogObject = new FileLogObject();
        fileLogObject.setName(fileName[0]);
        fileLogObject.setDownloadLocation(IP);

        String[] filePathSplit = _filePath.split(".");
        String filePath = filePathSplit[0] + ".json";


        writeFile(fileLogObject, filePath);

        return filePath;
    }

    public void updateFileLog(String filePath){
        String[] split = filePath.split(".");
        File file = new File(split[0] + ".json");


    }

    public void transferFileLog(){

    }

    public void writeFile(FileLogObject logObject, String _filePath){
        ObjectMapper obj = new ObjectMapper();

        try{
            String jsonString = obj.writeValueAsString(logObject);

            FileWriter fileWriter = new FileWriter(_filePath);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.write(jsonString);
        } catch(Exception e){
            e.printStackTrace();
        }

    }
}
