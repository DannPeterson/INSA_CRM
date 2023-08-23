import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment.prod";
import {HttpClient} from "@angular/common/http";
import {PolicyPart} from "../model/policypart";
import {Observable} from "rxjs";
import {Insurer} from "../model/insurer";

@Injectable({
  providedIn: 'root'
})
export class GoogleService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) {  }

  public isPartInserted(part: PolicyPart): Observable<{status: string}> {
    return this.http.post<{status: string}>(`${this.host}/google/check`, part);
  }

  public insertPart(part: PolicyPart): Observable<{status: string}> {
    return this.http.post<{status: string}>(`${this.host}/google/insert`, part);
  }
}
