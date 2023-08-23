import {Bank} from "./bank";

export class Firm {
  public id: number;
  public name: string;
  public code: string;
  public address: string;
  public phone: string;
  public fax: string;
  public email: string;
  public bank: Bank;
  public bankAccount: string;
  public comment: string;

  constructor() {
    this.id = 0;
    this.name = '';
    this.code = '';
    this.address = '';
    this.phone = '';
    this.fax = '';
    this.email = '';
    this.bank = null;
    this.bankAccount = '';
    this.comment = '';
  }
}
