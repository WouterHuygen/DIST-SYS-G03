import { Component, OnInit } from '@angular/core';
import { NamingService } from './services/naming.service';
import { Node } from './model/node';
import { NodeService } from './services/node.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'GUI';
  public nodes: Node[] = [];
  public nodesEmpty = false;
  public namingFetchingError = false;
  public loadingNodes = false;

  constructor(private namingService: NamingService, public nodeService: NodeService) { }

  ngOnInit() {
    this.loadNodes();
    setInterval(() => this.loadNodes(), 5000);
  }

  loadNodes() {
    this.loadingNodes = true;
    this.namingService.getMap().subscribe(result => {
      this.namingFetchingError = false;
      this.nodes = [];
      if (result.succes) {
        for (const value of Object.values(result.body)) {
          this.nodes.push(value);
        }
        if (this.nodes[0] && this.nodeService.selectedNode === undefined) {
          this.showNode(this.nodes[0]);
        }
        if (this.nodes.length === 0) {
          this.nodeService.selectedNode = undefined;
          this.nodeService.selectedNodeInfo = undefined;
        }
      }
      this.loadingNodes = false;
    },
      error => {
        this.nodes = [];
        this.nodeService.selectedNode = undefined;
        this.nodeService.selectedNodeInfo = undefined;
        this.namingFetchingError = true;
        this.loadingNodes = false;
      });
  }

  showNode(node: Node) {
    const port = '808' + node.name.substr(node.name.length - 1, node.name.length);
    this.nodeService.selectNode(node.ip, port);
  }
}
