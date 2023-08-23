import {PolicyPart} from "./policypart";

export class Invoice {
  public id: number;
  public invoiceNumber: string;
  public text: string;
  public conclusionDate: Date;
  public maxDate: Date;
  public paidDate: Date;

  constructor() {
    this.id = 0;
    this.invoiceNumber = '';
    this.text = '';
    this.conclusionDate = null;
    this.maxDate = null;
    this.paidDate = null;
  }

  static fromJson(json: any): Invoice {
    const invoice = new Invoice();

    invoice.id = json.id;
    invoice.invoiceNumber = json.invoiceNumber;
    invoice.text = json.text;


    if (json.conclusionDate) invoice.conclusionDate = new Date(json.conclusionDate);
    if (json.maxDate) invoice.maxDate = new Date(json.maxDate);
    if (json.paidDate) invoice.paidDate = new Date(json.paidDate);


    return invoice;
  }

  static fromJsonList(jsonList: any[]): Invoice[] {
    return jsonList.map(json => Invoice.fromJson(json));
  }
}
