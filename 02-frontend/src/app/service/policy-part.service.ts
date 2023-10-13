import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment.prod";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {PolicyPart} from "../model/policypart";
import {map} from "rxjs/operators";
import {Agent} from "../model/agent";
import {Insurer} from "../model/insurer";
import {PolicyType} from "../model/policytype";
import {PaymentType} from "../model/paymenttype";
import * as moment from 'moment';

@Injectable({
  providedIn: 'root'
})
export class PolicyPartService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) {
  }

  public findAllByPolicyId(policyId: number): Observable<PolicyPart[]> {
    return this.http.get<PolicyPart[]>(`${this.host}/policy-part/${policyId}`);
  }

  public saveAll(policyParts: PolicyPart[]): Observable<PolicyPart[]> {
    return this.http.post<PolicyPart[]>(`${this.host}/policy-part/save-list`, policyParts);
  }

  public update(id: number, policyPart: PolicyPart): Observable<PolicyPart> {
    return this.http.post<PolicyPart>(`${this.host}/policy-part/update/${id}`, policyPart);
  }

  public searchReminderParts(pageIndex: number, pageSize: number): Observable<any> {
    let queryParams = `pageIndex=${pageIndex}&pageSize=${pageSize}`;
    return this.http.get<any>(`${this.host}/policy-part/reminder?${queryParams}`);
  }

  public searchAgentParts(agent: Agent, start: Date, end: Date, payment: number): Observable<PolicyPart[]> {
    let startStr = moment(start).add(1, 'day').utc().format().substring(0, 10);;
    let endStr = moment(end).add(1, 'day').utc().format().substring(0, 10);;
    console.log(startStr, endStr);
    let queryParams = `start=${startStr}&end=${endStr}&payment=${payment}`;
    return this.http.get<PolicyPart[]>(`${this.host}/policy-part/agent/${agent.id}?${queryParams}`);
  }

  public searchInsurerParts(insurer: Insurer, policyType: PolicyType, paymentType: PaymentType, start: Date, end: Date, type: number): Observable<PolicyPart[]> {
    let startStr = moment(start).add(1, 'day').utc().format().substring(0, 10);;
    let endStr = moment(end).add(1, 'day').utc().format().substring(0, 10);;
    let queryParams = '';
    if(policyType && policyType.id) queryParams += `&policyTypeId=${policyType.id}`;
    if(paymentType && paymentType.id) queryParams += `&paymentTypeId=${paymentType.id}`;
    queryParams += `&start=${startStr}&end=${endStr}&type=${type}`;
    return this.http.get<PolicyPart[]>(`${this.host}/policy-part/insurer/${insurer.id}?${queryParams}`);
  }

  public searchInsurerControlParts(insurer: Insurer, policyType: PolicyType, date: Date, client: string, policy: string, type: number): Observable<PolicyPart[]> {
    let dateStr = moment(date).add(1, 'day').utc().format().substring(0, 10);
    let queryParams = `date=${dateStr}&type=${type}`;
    if(policyType && policyType.id) queryParams += `&policyTypeId=${policyType.id}`;
    if(client) queryParams += `&client=${client}`;
    if(policy) queryParams += `&policy=${policy}`;
    if(insurer && insurer.id) queryParams += `&insurer=${insurer.id}`;

    console.log(queryParams);
    return this.http.get<PolicyPart[]>(`${this.host}/policy-part/insurer-control/${insurer.id}?${queryParams}`);
  }

  public generateReport(parts: PolicyPart[]): Observable<string> {
    return this.http.post<string>(`${this.host}/policy-part/report`, parts);
  }

  public getFirstParts(policyIds: number[]): Observable<PolicyPart[]> {
    return this.http.post<PolicyPart[]>(`${this.host}/policy-part/first-parts`, policyIds);
  }

  public findAllDebts(): Observable<PolicyPart[]> {
    return this.http.get<PolicyPart[]>(`${this.host}/policy-part/debts`);
  }
}
