import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MainComponent} from "./main/main.component";
import {ClientsComponent} from "./clients/clients.component";
import {ReminderPoliciesComponent} from "./reminder-policies/reminder-policies.component";
import {ReminderPaymentsComponent} from "./reminder-payments/reminder-payments.component";
import {PercentComponent} from "./percent/percent.component";
import {InsurerComponent} from "./insurer/insurer.component";
import {AgentReportComponent} from "./agent-report/agent-report.component";
import {InsurerReportComponent} from "./insurer-report/insurer-report.component";
import {InsurerReportControlComponent} from "./insurer-report-control/insurer-report-control.component";
import {DebtsComponent} from "./debts/debts.component";

const routes: Routes = [
  { path: 'main', component: MainComponent },
  { path: 'client', component: ClientsComponent },
  { path: 'percent', component: PercentComponent },
  { path: 'insurer', component: InsurerComponent },
  { path: 'reminder-policies', component: ReminderPoliciesComponent },
  { path: 'reminder-payments', component: ReminderPaymentsComponent },
  { path: 'agent-report', component: AgentReportComponent },
  { path: 'insurer-report', component: InsurerReportComponent },
  { path: 'insurer-report-control', component: InsurerReportControlComponent },
  { path: 'debts', component: DebtsComponent },
  { path: '', redirectTo: '/main', pathMatch: 'full' },
  { path: 'index.html', redirectTo: '/index.html' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
