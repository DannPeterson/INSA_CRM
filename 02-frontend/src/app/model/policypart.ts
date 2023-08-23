import {Policy} from "./policy";
import {Invoice} from "./invoice";

export class PolicyPart {
  public id: number;
  public policy: Policy;
  public invoice: Invoice;
  public part: number;
  public sum: number;
  public date: Date;
  public datePaid: Date;
  public dateConfirmed: Date;
  public sumReal: number;
  public reminder: boolean;

  constructor() {
    this.id = 0;
    this.policy = null;
    this.invoice = null;
    this.part = 0;
    this.sum = 0;
    this.date = null;
    this.datePaid = null;
    this.dateConfirmed = null;
    this.sumReal = null;
    this.reminder = true;
  }

  static fromJson(json: any): PolicyPart {
    const policyPart = new PolicyPart();

    policyPart.id = json.id;
    policyPart.policy = Policy.fromJson(json.policy);
    if(json.invoice) policyPart.invoice = Invoice.fromJson(json.invoice);
    policyPart.part = json.part;
    policyPart.sum = json.sum;

    if (json.date ) policyPart.date = new Date(json.date);
    if (json.datePaid) policyPart.datePaid = new Date(json.datePaid);
    if (json.dateConfirmed) policyPart.dateConfirmed = new Date(json.dateConfirmed);

    policyPart.sumReal = json.sumReal;
    policyPart.reminder = json.reminder;

    return policyPart;
  }

  static fromJsonList(jsonList: any[]): PolicyPart[] {
    return jsonList.map(json => PolicyPart.fromJson(json));
  }
}
