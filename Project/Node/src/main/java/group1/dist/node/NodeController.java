package group1.dist.node;

import group1.dist.model.NodeInfo;
import group1.dist.model.StatusObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class NodeController {

    @Autowired
    private ApplicationContext context;

    @GetMapping("/info")
    public StatusObject<NodeInfo> getNodeInfo() {
        return new StatusObject<>(true, "info found", context.getBean(NodeInfo.class));
    }
}
