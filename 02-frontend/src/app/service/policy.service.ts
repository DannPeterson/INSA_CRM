import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment.prod";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Policy} from "../model/policy";
import {map} from "rxjs/operators";
import {PolicyType} from "../model/policytype";
import {PaymentType} from "../model/paymenttype";

@Injectable({
  providedIn: 'root'
})
export class PolicyService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) {
  }

  public getAllByPage(pageIndex: number, pageSize: number): Observable<any> {
    return this.http.get<any>(`${this.host}/policy?page=${pageIndex}&size=${pageSize}`);
  }

  public update(id: number, policy: Policy): Observable<Policy> {
    return this.http.post<Policy>(`${this.host}/policy/update/${id}`, policy);
  }

  public save(policy: Policy): Observable<Policy> {
    return this.http.put<Policy>(`${this.host}/policy/save`, policy);
  }

  public searchPolicies(policyType: PolicyType, paymentType: PaymentType, clientName: string, policyObject: string, invoiceNumber: string, policyNumber: string, current: boolean, pageIndex: number, pageSize: number): Observable<any> {
    let queryParams = `page=${pageIndex}&size=${pageSize}`;
    if (policyType && policyType.id) queryParams += `&policyTypeId=${policyType.id}`;
    if (paymentType && paymentType.id) queryParams += `&paymentTypeId=${paymentType.id}`;
    if (clientName) queryParams += `&clientName=${clientName}`;
    if (policyObject) queryParams += `&object=${policyObject}`;
    if (invoiceNumber) queryParams += `&invoiceNumber=${invoiceNumber}`;
    if (policyNumber) queryParams += `&policyNumber=${policyNumber}`;
    queryParams += `&current=${current}`;

    return this.http.get<any>(`${this.host}/policy/search?${queryParams}`);
  }

  public searchReminderPolicies(policyType: PolicyType, pageIndex: number, pageSize: number): Observable<any> {
    let queryParams = `page=${pageIndex}&size=${pageSize}`;
    if (policyType && policyType.id) queryParams += `&policyTypeId=${policyType.id}`;

    return this.http.get<any>(`${this.host}/policy/search/reminder?${queryParams}`);
  }

  public getPolicyFromClipboard(clipboard: string): Observable<Policy> {
    return this.http.put<Policy>(`${this.host}/policy/clipboard`, clipboard);
  }

  public getDeletionReport(policyId: number): Observable<string[]> {
    return this.http.get<string[]>(`${this.host}/policy/deletionReport/${policyId}`);
  }

  public delete(policyId: number): Observable<any> {
    return this.http.delete<any>(`${this.host}/policy/delete/${policyId}`);
  }

  public getById(policyId: number): Observable<Policy> {
    return this.http.get<Policy>(`${this.host}/policy/${policyId}`);
  }
}
