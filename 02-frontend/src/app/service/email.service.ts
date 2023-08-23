import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment.prod";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class EmailService {
  private host = environment.apiUrl;
  constructor(private http: HttpClient) { }

  sendEmailWithAttachments(emailData: EmailData): Observable<EmailResponse> {
    return this.http.post<EmailResponse>(`${this.host}/mail`, emailData);
  }
}

export interface EmailData {
  from: string;
  to: string[];
  subject: string;
  body: string;
  folder: string;
  attachments: string[];
  sendMeCopy: boolean;
}

export interface EmailResponse {
  message: string;
}
