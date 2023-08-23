import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {MainComponent} from './main/main.component';
import {NotifierModule} from "angular-notifier";
import {NotificationService} from './service/notification.service';
import {NavbarComponent} from './navbar/navbar.component';
import {HttpClientModule} from '@angular/common/http';
import {ModalModule} from "ngx-bootstrap/modal";
import {TabsModule} from 'ngx-bootstrap/tabs';
import {NgSelectModule} from '@ng-select/ng-select';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BsDatepickerModule} from "ngx-bootstrap/datepicker";
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatPaginatorIntl, MatPaginatorModule} from "@angular/material/paginator";
import {MatCardModule} from "@angular/material/card";
import {MatTableModule} from "@angular/material/table";
import {MatInputModule} from '@angular/material/input';
import {MatExpansionModule} from "@angular/material/expansion";
import {MatIconModule} from "@angular/material/icon";
import {MatSelectModule} from "@angular/material/select";

import {MatFormFieldModule} from '@angular/material/form-field';
import {MatOptionModule} from '@angular/material/core';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {ClientSelectComponent} from "./main/components/searchable-select/client-select.component";

import {CommonModule, DatePipe} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule} from "@angular/material/core";

import {DateAdapter, MAT_DATE_FORMATS, NativeDateAdapter} from '@angular/material/core';
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatListModule} from "@angular/material/list";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {NotificationModule} from "./notification.module";
import {ClientsComponent} from './clients/clients.component';
import {ReminderPoliciesComponent} from './reminder-policies/reminder-policies.component';
import {MatSortModule} from '@angular/material/sort';
import {MatMenuModule} from "@angular/material/menu";
import { ReminderPaymentsComponent } from './reminder-payments/reminder-payments.component';
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import { PercentComponent } from './percent/percent.component';
import { InsurerComponent } from './insurer/insurer.component';
import { AgentReportComponent } from './agent-report/agent-report.component';
import { InsurerReportComponent } from './insurer-report/insurer-report.component';
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import { InsurerReportControlComponent } from './insurer-report-control/insurer-report-control.component';

export class CustomDateAdapter extends NativeDateAdapter {
  format(date: Date, displayFormat: Object): string {
    const day = date.getDate();
    const month = date.getMonth() + 1;
    const year = date.getFullYear();
    return `${this._to2digit(day)}.${this._to2digit(month)}.${year}`;
  }

  parse(value: any): Date | null {
    if ((typeof value === 'string') && (value.indexOf('.') > -1)) {
      const str = value.split('.');
      if (str.length < 2 || isNaN(+str[0]) || isNaN(+str[1]) || isNaN(+str[2])) {
        return null;
      }
      return new Date(Number(str[2]), Number(str[1]) - 1, Number(str[0]));
    }
    return value ? new Date(value) : null;
  }

  private _to2digit(n: number): string {
    return ('00' + n).slice(-2);
  }
}

export const CUSTOM_DATE_FORMATS = {
  parse: {dateInput: {day: 'numeric', month: 'numeric', year: 'numeric'}},
  display: {
    dateInput: 'input',
    monthYearLabel: {year: 'numeric', month: 'short'},
    dateA11yLabel: {year: 'numeric', month: 'long', day: 'numeric'},
    monthYearA11yLabel: {year: 'numeric', month: 'long'},
  },
};

@NgModule({
  declarations: [
    AppComponent,
    MainComponent,
    NavbarComponent,
    ClientSelectComponent,
    ClientsComponent,
    ReminderPoliciesComponent,
    ReminderPaymentsComponent,
    PercentComponent,
    InsurerComponent,
    AgentReportComponent,
    InsurerReportComponent,
    InsurerReportControlComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        NotifierModule,
        HttpClientModule,
        ModalModule.forRoot(),
        TabsModule.forRoot(),
        NgSelectModule,
        FormsModule,
        ReactiveFormsModule,
        BsDatepickerModule,
        NoopAnimationsModule,
        BrowserAnimationsModule,
        MatPaginatorModule,
        MatCardModule,
        MatTableModule,
        MatInputModule,
        MatExpansionModule,
        MatIconModule,
        MatSelectModule,
        MatFormFieldModule,
        MatOptionModule,
        MatAutocompleteModule,
        CommonModule,
        MatFormFieldModule,
        MatButtonModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatCheckboxModule,
        MatToolbarModule,
        MatSidenavModule,
        MatListModule,
        MatSlideToggleModule,
        MatSortModule,
        NotificationModule,
        MatMenuModule,
        MatProgressSpinnerModule,
        MatButtonToggleModule,
    ],
  providers: [NotificationService, DatePipe,
    {provide: DateAdapter, useClass: CustomDateAdapter},
    {provide: MAT_DATE_FORMATS, useValue: CUSTOM_DATE_FORMATS},
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
