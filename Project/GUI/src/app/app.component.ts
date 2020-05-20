import { Component } from '@angular/core';
import { NamingService } from './services/naming.service';
import { Node } from './model/node';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'GUI';
  public nodes: Node[] = [];

  constructor(private namingService: NamingService) {
    this.loadNodes();
  }

  loadNodes() {
    this.namingService.getMap().subscribe(result => {
      console.log(result);
      if (result.succes) {
        for (const value of Object.values(result.body)) {
          this.nodes.push(value);
        }
      }
    });
  }
}
