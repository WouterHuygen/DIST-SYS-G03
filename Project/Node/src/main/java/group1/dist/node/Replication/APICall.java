package group1.dist.node.Replication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import group1.dist.model.Node;
import group1.dist.model.NodeInfo;
import group1.dist.model.StatusObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APICall {
    private NodeInfo nodeInfo;

    public APICall(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    //Class makes call to namingserver, namingserver will return IP the file has to be replicated to.
    public String call(String filename){
        String IP = null;
        try{
            //Call
            System.out.println("making API call");

            String namingIP = nodeInfo.getNamingIp();
            if (namingIP == null) throw new Exception("no naming ip");
            URL url = new URL("http://" + namingIP + ":8080/resolve?filename=" + filename);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            //Response
            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //JSON parsing
                ObjectMapper mapper = new ObjectMapper();

                try {
                    StatusObject<Node> statusObject = mapper.readValue(response.toString(), new TypeReference<StatusObject<Node>>(){});

                    if(statusObject.isSucces()){
                        IP = statusObject.getBody().getIp();
                        System.out.println("Name: " + statusObject.getBody().getName());
                        System.out.println("IP: " + IP);
                        return IP;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

                // print result
                System.out.println(response.toString());
                con.disconnect();
            } else {
                System.out.println("GET request failed");
                con.disconnect();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return IP;
    }
}
