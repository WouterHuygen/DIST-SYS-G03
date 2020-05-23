import { Component, OnInit } from '@angular/core';
import { NamingService } from './services/naming.service';
import { Node } from './model/node';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit{
  title = 'GUI';
  public nodes: Node[] = [];
  public nodesEmpty = false;
  public fetchingError = false;
  public loading = false;

  constructor(private namingService: NamingService) {}

  ngOnInit() {
    this.loadNodes();
  }

  loadNodes() {
    this.loading = true;
    this.nodes = [];
    this.namingService.getMap().subscribe(result => {
      console.log(result);
      if (result.succes) {
        for (const value of Object.values(result.body)) {
          this.nodes.push(value);
        }
        this.fetchingError = false;
      }
      this.loading = false;
    },
    error => {
      this.fetchingError = true;
      this.loading = false;
    });
  }
}
