import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment.prod";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {PolicyType} from "../model/policytype";

@Injectable({
  providedIn: 'root'
})
export class PolicyTypeService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) {
  }

  public findAll(): Observable<PolicyType[]> {
    return this.http.get<PolicyType[]>(`${this.host}/policy-type/all`);
  }
}
