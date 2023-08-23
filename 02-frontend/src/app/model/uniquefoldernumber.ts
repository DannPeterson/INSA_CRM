export class UniqueFolderNumber {
  public id: number;
  public date: Date;
  public lastUsedNumber: number;

  constructor() {
    this.id = 0;
    this.date = null;
    this.lastUsedNumber = 0;
  }
}
