import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment.prod";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class SmsService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) {
  }

  sendSms(smsData: SmsData): Observable<SmsResponse> {
    return this.http.post<SmsResponse>(`${this.host}/sms`, smsData);
  }
}

export interface SmsData {
  policyId: number;
  folder: string;
  from: string;
  to: string;
  message: string;
}

interface SmsResponse {
  message: string;
}
