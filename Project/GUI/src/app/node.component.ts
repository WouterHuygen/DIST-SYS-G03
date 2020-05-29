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
    this.nodeService.loadingNode = true;
    const node = this.nodeService.selectedNode;
    const port = '808' + node.name.substr(node.name.length - 1, node.name.length);
    this.nodeService.selectNode(node.ip, port);
  }
}
