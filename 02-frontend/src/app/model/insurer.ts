import {Firm} from "./firm";

export class Insurer {
  public id: number;
  public firm: Firm;
  public name: string;
  public invoicePrefix: string;
  public comment: string;
  public url: string;
  public username: string;
  public password: string;

  constructor() {
    this.id = 0;
    this.firm = null;
    this.name = '';
    this.invoicePrefix = '';
    this.comment = '';
    this.url = '';
    this.username = '';
    this.password = '';
  }
}
