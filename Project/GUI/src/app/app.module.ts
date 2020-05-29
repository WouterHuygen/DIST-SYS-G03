import { BrowserModule } from '@angular/platform-browser';
import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { MDBBootstrapModule } from 'angular-bootstrap-md';

import { AppComponent } from './app.component';

import { NamingService } from './services/naming.service';
import { HttpClientModule } from '@angular/common/http';
import { NodeService } from './services/node.service';
import { NodeComponent } from './node.component';

@NgModule({
  declarations: [
    AppComponent,
    NodeComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    MDBBootstrapModule.forRoot()
  ],
  schemas: [ NO_ERRORS_SCHEMA ],
  providers: [
    NamingService,
    NodeService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
