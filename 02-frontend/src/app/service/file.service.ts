import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment.prod";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FileService {
  private host = environment.apiUrl;
  constructor(private http: HttpClient) { }

  public getFolderFiles(folder: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.host}/files/${folder}`);
  }

  public openPolicyFolder(folder: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.host}/files/open/${folder}`);
  }
}
