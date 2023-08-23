import {Insurer} from "./insurer";
import {PolicyType} from "./policytype";

export class InsurerTypeProfitPercent {
  public id: number;
  public insurer: Insurer;
  public policyType: PolicyType;
  public percent: number;

  constructor() {
    this.id = 0;
    this.insurer = null;
    this.policyType = null;
    this.percent = 0;
  }
}
