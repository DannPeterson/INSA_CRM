import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment.prod";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Invoice} from "../model/invoice";
import {PolicyPart} from "../model/policypart";

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) { }

  public findByPolicyPartId(policyPartId: number): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.host}/invoice/search-by-policy-part?policyPartId=${policyPartId}`);
  }

  public save(invoice: Invoice, policyPart: PolicyPart): Observable<Invoice> {
    return this.http.post<Invoice>(`${this.host}/invoice/${policyPart.id}/createInvoice`, invoice);
  }
}
