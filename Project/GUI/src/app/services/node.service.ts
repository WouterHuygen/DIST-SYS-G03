import { Injectable } from '@angular/core';
import { Node } from '../model/node';
import { Observable } from 'rxjs';
import { StatusObject } from '../model/statusObject';
import { NodeInfo } from '../model/nodeInfo';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class NodeService {
  public selectedNode: Node;
  public selectedNodeInfo: NodeInfo;
  public loadingNode = false;
  public failedLoading = false;
  public loadingFiles = false;
  public replicateMessage: string;
  public ownMessage: string;

  constructor(private http: HttpClient) { }

  public getNodeInfo(ip: string, port: string): Observable<StatusObject<NodeInfo>> {
    return this.http.get<StatusObject<NodeInfo>>(`http://${ip}:${port}/info`);
  }

  public getOwnFiles(ip: string, port: string): Observable<StatusObject<string[]>> {
    return this.http.get<StatusObject<string[]>>(`http://${ip}:${port}/ownfiles`);
  }

  public getReplicatedFiles(ip: string, port: string): Observable<StatusObject<string[]>> {
    return this.http.get<StatusObject<string[]>>(`http://${ip}:${port}/replicatedfiles`);
  }

  public selectNode(ip: string, port: string) {
    this.loadingNode = this.loadingFiles = true;
    this.getNodeInfo(ip, port).subscribe(result => {
      this.failedLoading = false;
      if (result.succes) {
        this.selectedNode = result.body.self;
        this.selectedNodeInfo = result.body;
        this.loadFiles(ip, port);
      }
      this.loadingNode = false;
    },
      error => {
        this.failedLoading = true;
        this.loadingNode = false;
        this.selectedNode = undefined;
      });

  }

  public shutdownNode(ip: string, port: string): Observable<string> {
    return this.http.get<string>(`http://${ip}:${port}/shutdown`);
  }

  private loadFiles(ip: string, port: string) {
    this.getReplicatedFiles(ip, port).subscribe(result => {
      if (result.succes) {
        this.selectedNode.replicatedFiles = result.body;
        this.replicateMessage = '';
      }
      else {
        this.replicateMessage = result.message;
      }
      this.loadingFiles = false;
    },
      error => this.selectedNodeInfo = undefined);
    this.loadingFiles = true;
    this.getOwnFiles(ip, port).subscribe(result => {
      if (result.succes) {
        this.selectedNode.ownFiles = result.body;
        this.ownMessage = '';
      }
      else {
        this.ownMessage = result.message;
      }
      this.loadingFiles = false;
    },
      error => this.selectedNodeInfo = undefined);
  }
}
