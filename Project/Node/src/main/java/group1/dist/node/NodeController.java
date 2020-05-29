package group1.dist.node;

import group1.dist.model.NodeInfo;
import group1.dist.model.StatusObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@CrossOrigin
@RestController
public class NodeController {

    @Autowired
    private ApplicationContext context;

    @GetMapping("/info")
    public StatusObject<NodeInfo> getNodeInfo() {
        return new StatusObject<>(true, "info found", context.getBean(NodeInfo.class));
    }

    @GetMapping("/ownfiles")
    public StatusObject<List<String>> getOwnFiles() {
        List<String> ownFiles = new ArrayList<>();
        String directory = System.getProperty("user.dir");
        File folder = new File(directory + "\\ownFiles");
        if (folder.listFiles() != null) {
            for (File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                ownFiles.add(fileEntry.getName());
            }
            if (!ownFiles.isEmpty())
                return new StatusObject<>(true, "Files owned by node", ownFiles);
            return new StatusObject<>(false, "No owned files found", null);
        }
        return new StatusObject<>(false, "No owned files found", null);
    }

    @GetMapping("/replicatedfiles")
    public StatusObject<List<String>> getReplicatedFiles() {
        List<String> replicatedFiles = new ArrayList<>();
        String directory = System.getProperty("user.dir");
        File folder = new File(directory + "\\replicatedFiles");
        if (folder.listFiles() != null) {
            for (File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                replicatedFiles.add(fileEntry.getName());
            }
            if (!replicatedFiles.isEmpty())
                return new StatusObject<>(true, "Files replicated on node", replicatedFiles);
            return new StatusObject<>(false, "No replicated files found", null);
        }
        return new StatusObject<>(false, "No replicated files found", null);
    }

    @GetMapping("/restart")
    public void restart(@RequestParam(value = "name", defaultValue = "") String newNodeName) {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);
        String[] strArgs = new String[args.getSourceArgs().length + 1];
        int i;
        boolean containsName = false;
        for (i = 0; i < args.getSourceArgs().length; i++) {
            if (!args.getSourceArgs()[i].contains("--name="))
                strArgs[i] = args.getSourceArgs()[i];
            else {
                strArgs[i] = "--name="+newNodeName;
                containsName = true;
            }
        }
        if (!containsName)
            strArgs[i] = "--name="+newNodeName;

        Thread end = new Thread(() -> {
            ((ConfigurableApplicationContext)context).close();
            context = SpringApplication.run(NodeApplication.class, strArgs);
        });

        end.setDaemon(false);
        end.start();
    }
}
