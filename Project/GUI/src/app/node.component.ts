import { Component } from '@angular/core';
import { NodeService } from './services/node.service';


@Component({
  selector: 'app-node',
  templateUrl: './node.component.html',
  //styleUrls: ['./app.component.scss']
})
export class NodeComponent {

  constructor(public nodeService: NodeService) { }

  public reload() {
    const node = this.nodeService.selectedNode;
    const port = '808' + node.name.substr(node.name.length - 1, node.name.length);
    this.nodeService.selectNode(node.ip, port);
  }

  public shutdown() {
    const node = this.nodeService.selectedNode;
    const port = '808' + node.name.substr(node.name.length - 1, node.name.length);
    this.nodeService.shutdownNode(node.ip, port).subscribe(result => {
      this.nodeService.selectedNode = undefined;
      this.nodeService.selectedNodeInfo = undefined;
    });
  }
}
