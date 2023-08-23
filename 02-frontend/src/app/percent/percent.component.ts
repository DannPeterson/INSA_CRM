import {Component, OnInit} from '@angular/core';
import {InsurerTypeProfitPercent} from "../model/insurertypeprofitpercent";
import {PercentService} from "../service/percent.service";
import {NotificationService} from "../service/notification.service";
import {NotificationType} from "../enum/notification-type.enum";

@Component({
  selector: 'app-percent',
  templateUrl: './percent.component.html',
  styleUrls: ['./percent.component.css']
})
export class PercentComponent implements OnInit {
  displayedColumns: string[] = ['BTA', 'ERGO', 'GJENSIDIGE AAS', 'IF', 'INGES', 'PZU', 'Salva', 'Seesam'];
  allColumns: string[] = [];
  policyTypes: string[] = ['VI', 'VJ', 'Õnnetus', 'Cargo', 'CAR Ehitus-Montaaž', 'CMR', 'Vastutus', 'Garantii', 'Ehitusmasinad', 'Kasko', 'LK', 'Reisi Kindlustus'];
  percents: InsurerTypeProfitPercent[];

  constructor(private percentService: PercentService,
              private notificationService: NotificationService) {
    this.allColumns = ['type', ...this.displayedColumns];
    this.percents = [];
  }

  ngOnInit(): void {
    this.getPercents();
  }

  getPercents(): void {
    this.percentService.findAll().subscribe(
      (response: InsurerTypeProfitPercent[]) => {
        this.percents = response;
      });
  }

  getCellPercent(policyType: string, insurer: string): number {
    let percent = this.percents.find(p => p.insurer.name == insurer && p.policyType.type == policyType);
    return percent ? percent.percent : 0;
  }

  updatePercent(newPercent: number, policyType: string, insurer: string): void {
    console.log(newPercent, policyType, insurer);
    let percentObj = this.percents.find(p => p.insurer.name == insurer && p.policyType.type == policyType);
    console.log(percentObj.id);
    if (percentObj) {
      percentObj.percent = newPercent;
      this.percentService.update(percentObj).subscribe(
        (response: InsurerTypeProfitPercent) => {
          this.sendNotification(NotificationType.SUCCESS, `Процент обновлен`);
        },
        (errorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      );
    }
  }

  sendNotification(notificationType: NotificationType, message: string): void {
    this.notificationService.notify(notificationType, message);
  }

  numberOnly(event): boolean {
    const charCode = (event.which) ? event.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode !== 46) {
      return false;
    }
    return true;
  }
}
