import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment.prod";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class LogService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) { }

  public getLogs(folder: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.host}/logs/${folder}`);
  }

  public getBanks(folders: string[]): Observable<string[]> {
    return this.http.post<string[]>(`${this.host}/logs/banks`, folders);
  }
}
