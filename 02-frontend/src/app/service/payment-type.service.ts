import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment.prod";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Insurer} from "../model/insurer";
import {PaymentType} from "../model/paymenttype";

@Injectable({
  providedIn: 'root'
})
export class PaymentTypeService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) { }

  public findAll(): Observable<PaymentType[]> {
    return this.http.get<PaymentType[]>(`${this.host}/payment-type/all`);
  }
}
