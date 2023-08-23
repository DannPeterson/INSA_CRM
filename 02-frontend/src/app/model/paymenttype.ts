export class PaymentType {
  public id: number;
  public type: string;
  public prefix: string;

  constructor() {
    this.id = 0;
    this.type = '';
    this.prefix = '';
  }
}
