import {Client} from "./client";
import {Insurer} from "./insurer";
import {PolicyType} from "./policytype";
import {PaymentType} from "./paymenttype";
import {Agent} from "./agent";
import {User} from "./user";

export class Policy {
  public id: number;
  public client: Client;
  public insurer: Insurer;
  public policyType: PolicyType;
  public paymentType: PaymentType;
  public agent: Agent;
  public user: User;
  public conclusionDate: Date;
  public startDate: Date;
  public endDate: Date;
  public stopDate: Date;
  public reminderDate: Date;
  public reminder: boolean;
  public object: string;
  public folder: string;
  public policyNumber: string;
  public sum: number;
  public percent: number;
  public provision: number;
  public comment: string;
  public parts: number;

  constructor() {
    this.id = 0;
    this.client = null;
    this.insurer = null;
    this.policyType = null;
    this.paymentType = null;
    this.agent = null;
    this.user = null;
    this.conclusionDate = null;
    this.startDate = null;
    this.endDate = null;
    this.stopDate = null;
    this.reminderDate = null;
    this.reminder = true;
    this.object = '';
    this.folder = '';
    this.policyNumber = '';
    this.sum = 0;
    this.percent = 0;
    this.provision = 0;
    this.comment = '';
    this.parts = 0;
  }

  static fromJson(json: any): Policy {
    const policy = new Policy();

    policy.id = json.id;
    policy.client = json.client;
    policy.insurer = json.insurer;
    policy.policyType = json.policyType;
    policy.paymentType = json.paymentType;
    policy.agent = json.agent;
    policy.user = json.user;

    if (json.conclusionDate) policy.conclusionDate = new Date(json.conclusionDate);
    if (json.startDate) policy.startDate = new Date(json.startDate);
    if (json.endDate) policy.endDate = new Date(json.endDate);
    if (json.stopDate) policy.stopDate = new Date(json.stopDate);
    if (json.reminderDate) policy.reminderDate = new Date(json.reminderDate);

    policy.reminder = json.reminder;
    policy.object = json.object;
    policy.folder = json.folder;
    policy.policyNumber = json.policyNumber;
    policy.sum = json.sum;
    policy.percent = json.percent;
    policy.provision = json.provision;
    policy.comment = json.comment;
    policy.parts = json.parts;

    return policy;
  }

  static fromJsonList(jsonList: any[]): Policy[] {
    return jsonList.map(json => Policy.fromJson(json));
  }
}
