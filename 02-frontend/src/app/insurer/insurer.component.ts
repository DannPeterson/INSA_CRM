import { Component, OnInit } from '@angular/core';
import {Insurer} from "../model/insurer";
import {InsurerService} from "../service/insurer.service";
import {NotificationType} from "../enum/notification-type.enum";
import {NotificationService} from "../service/notification.service";

@Component({
  selector: 'app-insurer',
  templateUrl: './insurer.component.html',
  styleUrls: ['./insurer.component.css']
})
export class InsurerComponent implements OnInit {
  displayedColumns: string[] = ['name', 'invoice', 'url', 'username', 'password', 'comment'];

  insurers: Insurer[];

  constructor(private insurerService: InsurerService,
              private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.getInsurers();
  }

  getInsurers(): void {
    this.insurerService.findAll().subscribe(
      (response: Insurer[]) => {
        this.insurers = response;
      }
    )
  }

  onInsurerChange(event: any, insurer: Insurer, field: string): void {
    if(field === 'invoice') {
      insurer.invoicePrefix = event.target.value;
    }
    if(field === 'url') {
      insurer.url = event.target.value;
    }
    if(field === 'username') {
      insurer.username = event.target.value;
    }
    if(field === 'password') {
      insurer.password = event.target.value;
    }
    if(field === 'comment') {
      insurer.comment = event.target.value;
    }
    this.insurerService.save(insurer).subscribe(
      (response: Insurer) => {
        const index = this.insurers.findIndex(i => i.id === response.id);
        this.insurers[index] = response;
        if(index > -1) {
          this.insurers[index] = response;
          this.notificationService.notify(NotificationType.SUCCESS, `Данные ${response.name} обновлены`);
        } else {
          this.notificationService.notify(NotificationType.WARNING, `Ошибка обновления данных`);

        }
      },
      (errorResponse) => {
        this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
      }
    );
  }

  sendNotification(notificationType: NotificationType, message: string): void {
    this.notificationService.notify(notificationType, message);
  }
}
