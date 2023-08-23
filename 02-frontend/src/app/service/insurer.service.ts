import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment.prod";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Agent} from "../model/agent";
import {Insurer} from "../model/insurer";

@Injectable({
  providedIn: 'root'
})
export class InsurerService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) { }

  public findAll(): Observable<Insurer[]> {
    return this.http.get<Insurer[]>(`${this.host}/insurer/all`);
  }

  public save(insurer: Insurer): Observable<Insurer> {
    return this.http.post<Insurer>(`${this.host}/insurer`, insurer);
  }
}
