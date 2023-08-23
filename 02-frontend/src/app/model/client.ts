import {Bank} from "./bank";

export class Client {
  public id: number;
  public name: string;
  public code: string;
  public address: string;
  public phone: string;
  public mobile1: string;
  public mobile2: string;
  public email1: string;
  public email2: string;
  public email3: string;
  public representative: string;
  public bank: Bank;
  public bankAccount: string;
  public comment: string;

  constructor() {
    this.id = 0;
    this.name = '';
    this.code = '';
    this.address = '';
    this.phone = '';
    this.mobile1 = '';
    this.mobile2 = '';
    this.email1 = '';
    this.email2 = '';
    this.email3 = '';
    this.representative = '';
    this.bank = null;
    this.bankAccount = '';
    this.comment = '';
  }
}
