import {Injectable} from "@angular/core";
import {environment} from "../../environments/environment.prod";
import {HttpClient} from "@angular/common/http";
import {Bank} from "../model/bank";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class BankService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) { }

  public findAll(): Observable<Bank[]> {
    return this.http.get<Bank[]>(`${this.host}/bank/all`);
  }
}
