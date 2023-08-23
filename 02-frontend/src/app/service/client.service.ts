import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment.prod";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Client} from "../model/client";

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) { }

  public findAll(): Observable<Client[]> {
    return this.http.get<Client[]>(`${this.host}/client/all`);
  }

  public findAllByNameContainsIgnoreCase(name: string): Observable<Client[]> {
    return this.http.get<Client[]>(`${this.host}/client/searchByName?name=${name}`);
  }

  public save(client: Client): Observable<Client> {
    return this.http.post<Client>(`${this.host}/client/save`, client);
  }

  public getAllByPage(pageIndex: number, pageSize: number): Observable<any> {
    return this.http.get<any>(`${this.host}/client?page=${pageIndex}&size=${pageSize}`);
  }

  public search(name: string, email: string, phone: string, pageIndex: number, pageSize: number): Observable<any> {
    let queryParams = `page=${pageIndex}&size=${pageSize}`;
    if (name) queryParams += `&name=${name}`;
    if (email) queryParams += `&email=${email}`;
    if (phone) queryParams += `&phone=${phone}`;
    return this.http.get<any>(`${this.host}/client/search?${queryParams}`);
  }
}
