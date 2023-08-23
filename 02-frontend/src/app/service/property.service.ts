import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment.prod";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class PropertyService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) {
  }

  getProperties(): Observable<{ [key: string]: string }> {
    return this.http.get<{ [key: string]: string }>(`${this.host}/property`);
  }
}
