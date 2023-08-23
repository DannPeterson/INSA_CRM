import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment.prod";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Agent} from "../model/agent";

@Injectable({
  providedIn: 'root'
})
export class AgentService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) { }

  public findAll(): Observable<Agent[]> {
    return this.http.get<Agent[]>(`${this.host}/agent/all`);
  }
}
