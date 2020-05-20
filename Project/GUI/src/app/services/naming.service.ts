import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StatusObject } from '../model/statusObject';
import { Node } from '../model/node';

@Injectable()
export class NamingService{

  constructor(private http: HttpClient){}

  public getMap(): Observable<StatusObject<Map<string, Node>>> {
    return this.http.get<StatusObject<Map<string, Node>>>('http://localhost:8080/nodes');
  }
}
