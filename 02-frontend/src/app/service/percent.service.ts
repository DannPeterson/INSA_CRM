import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment.prod";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Insurer} from "../model/insurer";
import {InsurerTypeProfitPercent} from "../model/insurertypeprofitpercent";

@Injectable({
  providedIn: 'root'
})
export class PercentService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) {
  }

  public findAll(): Observable<InsurerTypeProfitPercent[]> {
    return this.http.get<InsurerTypeProfitPercent[]>(`${this.host}/percent/all`);
  }

  public update(insurerTypeProfitPercent: InsurerTypeProfitPercent): Observable<InsurerTypeProfitPercent> {
    return this.http.post<InsurerTypeProfitPercent>(`${this.host}/percent/update`, insurerTypeProfitPercent);
  }
}
