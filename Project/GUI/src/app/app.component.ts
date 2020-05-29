import { Component, OnInit } from '@angular/core';
import { NamingService } from './services/naming.service';
import { Node } from './model/node';
import { NodeService } from './services/node.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit{
  title = 'GUI';
  public nodes: Node[] = [];
  public nodesEmpty = false;
  public namingFetchingError = false;
  public loadingNodes = false;

  constructor(private namingService: NamingService, public nodeService: NodeService) {}

  ngOnInit() {
    this.loadNodes();
  }

  loadNodes() {
    this.loadingNodes = true;
    this.nodes = [];
    this.namingService.getMap().subscribe(result => {
      console.log(result);
      if (result.succes) {
        for (const value of Object.values(result.body)) {
          this.nodes.push(value);
        }
        this.showNode(this.nodes[0]);
        this.namingFetchingError = false;
      }
      this.loadingNodes = false;
    },
    error => {
      this.namingFetchingError = true;
      this.loadingNodes = false;
    });
  }

  showNode(node: Node) {
    const port = '808' + node.name.substr(node.name.length - 1, node.name.length);
    this.nodeService.selectNode(node.ip, port);
  }
}
