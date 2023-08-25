import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, NavigationEnd, Router} from "@angular/router";
import {filter} from "rxjs/operators";
import {Title} from "@angular/platform-browser";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  title: string;
  urlTitleMapping = {
    '': 'Полисы',
    '/': 'Полисы',
    '/main': 'Полисы',
    '/client': 'Клиенты',
    '/percent': 'Провизия',
    '/insurer': 'Страховщики',
    '/reminder-policies': 'Напоминания по полисам',
    '/reminder-payments': 'Напоминания по платежам',
    '/agent-report': 'Отчет по агентам',
    '/insurer-report': 'Отчет по страховым кампаниям',
    '/insurer-report-control': 'Контроль платежей по страховым кампаниям',
    '/debts': 'Долги',
  };
  constructor(private router: Router, private titleService: Title) { }

  ngOnInit(): void {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.title = this.urlTitleMapping[event.urlAfterRedirects];
      this.titleService.setTitle('INSA - ' + this.title);
      console.log(this.title);
    });
  }
}
