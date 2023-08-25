import {AfterViewInit, ChangeDetectorRef, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {BsModalRef, BsModalService} from "ngx-bootstrap/modal";
import {Subscription} from "rxjs";
import {Policy} from "../model/policy";
import {Agent} from "../model/agent";
import {Client} from "../model/client";
import {Insurer} from "../model/insurer";
import {Bank} from "../model/bank";
import {PaymentType} from "../model/paymenttype";
import {PolicyType} from "../model/policytype";
import {PolicyPart} from "../model/policypart";
import {Invoice} from "../model/invoice";
import {FormBuilder, FormControl} from "@angular/forms";
import {Router} from "@angular/router";
import {AgentService} from "../service/agent.service";
import {BankService} from "../service/bank.service";
import {ClientService} from "../service/client.service";
import {InsurerService} from "../service/insurer.service";
import {InvoiceService} from "../service/invoice.service";
import {PaymentTypeService} from "../service/payment-type.service";
import {PolicyService} from "../service/policy.service";
import {PolicyPartService} from "../service/policy-part.service";
import {PolicyTypeService} from "../service/policy-type.service";
import {FileService} from "../service/file.service";
import {NotificationService} from "../service/notification.service";
import {DatePipe} from "@angular/common";
import {MatTable, MatTableDataSource} from "@angular/material/table";
import {PageEvent} from "@angular/material/paginator";
import {MatSort, Sort} from '@angular/material/sort';
import {NotificationType} from "../enum/notification-type.enum";
import {ObjectUtils} from "../shared/object/object-utils";
import {LogService} from "../service/log.service";
import {PropertyService} from "../service/property.service";
import {InsurerTypeProfitPercent} from "../model/insurertypeprofitpercent";
import {PercentService} from "../service/percent.service";
import {EmailService} from "../service/email.service";
import {SmsData, SmsService} from "../service/sms.service";
import {GoogleService} from "../service/google.service";

@Component({
  selector: 'app-reminder-policies',
  templateUrl: './reminder-policies.component.html',
  styleUrls: ['./reminder-policies.component.css']
})
export class ReminderPoliciesComponent implements OnInit, AfterViewInit {
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild('policyTable') policyTable: MatTable<any>;
  displayedColumns: string[] = ['menu', 'policyNumber', 'policyType', 'insurer', 'client', 'clientComment', 'object', 'startDate', 'endDate', 'reminderDate', 'sum', 'reminder', 'comment'];
  modalRef?: BsModalRef | null;
  modalRef2?: BsModalRef;
  subscriptions: Subscription[] = [];

  //PAGINATION
  totalPolicies = 0;
  pageSize = 100;
  pageIndex = 0;

  // COMMON
  properties: { [key: string]: string } = {};
  policies: Policy[];
  policiesDataSource = new MatTableDataSource<Policy>();
  agents: Agent[];
  clients: Client[];
  insurers: Insurer[];
  banks: Bank[];
  paymentTypes: PaymentType[];
  percents: InsurerTypeProfitPercent[];
  policyTypes: PolicyType[];
  parts = [1, 2, 3, 4, 6, 12];

  // MODAL POLICY
  modalPolicy: Policy;
  modalPolicyParts: PolicyPart[];
  modalPolicyPartsDb: PolicyPart[];
  modalPolicyLogs: string[];
  modalPolicyLocked = true;
  modalPolicyType = new FormControl({value: null, disabled: this.modalPolicyLocked});
  modalPolicyInsurer = new FormControl({value: null, disabled: this.modalPolicyLocked});
  modalPolicyPaymentType = new FormControl({value: null, disabled: this.modalPolicyLocked});
  modalPolicyAgent = new FormControl({value: null, disabled: this.modalPolicyLocked});
  modalPolicyObject = new FormControl({value: null, disabled: this.modalPolicyLocked});
  modalPolicyNumber = new FormControl({value: null, disabled: this.modalPolicyLocked});
  modalPolicySum = new FormControl({value: null, disabled: this.modalPolicyLocked});
  modalPolicyTotalParts = new FormControl({value: null, disabled: this.modalPolicyLocked});
  modalPolicyPercent = new FormControl({value: null});

  //MODAL INVOICE
  modalPart: PolicyPart;
  modalInvoice: Invoice;

  //SELECTED CLIENT
  selectedClient: Client;
  selectedClientBank = new FormControl({value: null});

  // EMAIL
  mailSubject = new FormControl(null);
  email1 = new FormControl(null);
  email2 = new FormControl(null);
  email3 = new FormControl(null);
  mailBody = new FormControl(null);
  policyFiles: string[];
  filesSelected = new FormControl('');
  sendMeCopy = true;

  // SEARCH
  searchPolicyType = new FormControl(null);

  // MAILING TAB
  tabMailSubject = new FormControl(null);
  tabEmail1 = new FormControl(null);
  tabEmail2 = new FormControl(null);
  tabEmail3 = new FormControl(null);
  tabMailBody = new FormControl(null);
  tabBank = new FormControl(null);
  tabFilesSelected = new FormControl('');
  tabSendMeCopy = true;
  tabEmailLoading = false;
  tabSmsLoading = false;

  // SMS TAB
  tabSmsSender = new FormControl(null);
  tabSmsReceiver = new FormControl(null);
  tabSmsText = new FormControl(null);
  tabSmsLanguageRus = true;

  // SMS MODAL
  modalSmsSender = new FormControl(null);
  modalSmsReceiver = new FormControl(null);
  modalSmsText = new FormControl(null);
  modalSmsLanguageRus = true;

  // GOOGLE SHEETS
  gSheetsLoading: false;
  modalPolicyPartsExcelStatus: boolean[];


  constructor(private router: Router,
              private cdr: ChangeDetectorRef,
              private propertyService: PropertyService,
              private emailService: EmailService,
              private smsService: SmsService,
              private modalService: BsModalService,
              private agentService: AgentService,
              private bankService: BankService,
              private clientService: ClientService,
              private insurerService: InsurerService,
              private invoiceService: InvoiceService,
              private paymentTypeService: PaymentTypeService,
              private policyService: PolicyService,
              private policyPartService: PolicyPartService,
              private policyTypeService: PolicyTypeService,
              private percentService: PercentService,
              private fileService: FileService,
              private logService: LogService,
              private notificationService: NotificationService,
              private googleService: GoogleService,
              private datePipe: DatePipe) {
  }

  ngOnInit(): void {
    this.getProperties();
    this.getAgents();
    this.getClients();
    this.getInsurers();
    this.getPaymentTypes();
    this.getPolicyTypes();
    this.getBanks();
    this.getPercents();
  }

  ngAfterViewInit() {
    this.policiesDataSource.sort = this.sort;
    this.policiesDataSource.sortingDataAccessor = (item, property) => {
      switch(property) {
        case 'client': return item.client?.name;
        case 'insurer': return item.insurer?.name;
        default: return item[property];
      }
    };
    this.policiesDataSource.sort = this.sort;
    this.searchPolicies();
  }

  // COMMON
  getProperties(): void {
    this.propertyService.getProperties()
      .subscribe(data => {
        this.properties = data;
      });
  }

  getAgents(): void {
    this.subscriptions.push(
      this.agentService.findAll().subscribe(
        (response: Agent[]) => {
          this.agents = response;
        }
      )
    );
  }

  getClients(): void {
    this.subscriptions.push(
      this.clientService.findAll().subscribe(
        (response: Client[]) => {
          this.clients = response;
        }
      )
    );
  }

  getInsurers(): void {
    this.subscriptions.push(
      this.insurerService.findAll().subscribe(
        (response: Insurer[]) => {
          this.insurers = response;
        }
      )
    );
  }

  getPaymentTypes(): void {
    this.subscriptions.push(
      this.paymentTypeService.findAll().subscribe(
        (response: PaymentType[]) => {
          this.paymentTypes = response;
        }
      )
    );
  }

  getPolicyTypes(): void {
    this.subscriptions.push(
      this.policyTypeService.findAll().subscribe(
        (response: PolicyType[]) => {
          this.policyTypes = response;
        }
      )
    );
  }

  getBanks(): void {
    this.subscriptions.push(
      this.bankService.findAll().subscribe(
        (response: Bank[]) => {
          this.banks = response;
        }
      )
    );
  }

  getPercents(): void {
    this.subscriptions.push(
      this.percentService.findAll().subscribe(
        (response: InsurerTypeProfitPercent[]) => {
          this.percents = response;
        }
      )
    );
  }

  sendNotification(notificationType: NotificationType, message: string): void {
    this.notificationService.notify(notificationType, message);
  }


  // REMINDER POLICIES SPECIFIC
  onPageChange(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.searchPolicies();
  }

  searchPolicies(): void {
    const policyType: PolicyType = this.searchPolicyType.value;

    this.policyService.searchReminderPolicies(policyType, this.pageIndex, this.pageSize)
      .subscribe(data => {
        this.totalPolicies = data.totalElements;
        this.policiesDataSource.data = Policy.fromJsonList(data.content);
      });
  }

  updateClientFromTable(client: Client) {
    this.clientService.save(client).subscribe(
      (updatedClient: Client) => {
        // find policies with this client
        const policies = this.policiesDataSource.data.filter(policy => policy.client.id === updatedClient.id);
        // update policies with new client
        policies.forEach(policy => policy.client = updatedClient);
      }
    );
  }

  updatePolicyFromTable(policy: Policy) {
    this.policyService.update(policy.id, policy).subscribe(
      (updatedPolicy: Policy) => {
        const index = this.policiesDataSource.data.findIndex(policy => policy.id === updatedPolicy.id);
        if (index > -1) {
          this.policiesDataSource.data[index] = updatedPolicy;
        } else {
          this.sendNotification(NotificationType.ERROR, 'Ошибка обновления полиса');
        }
      },
      (error) => {
        this.sendNotification(NotificationType.ERROR, 'Ошибка сохранения полиса');
      }
    );
  }

  onPolicyCommentChange(event: any, policy: Policy) {
    policy.comment = event.target.value;
    this.updatePolicyFromTable(policy);
  }

  onPolicyReminderChange(policy: Policy) {
    policy.reminder = !policy.reminder;
    this.updatePolicyFromTable(policy);
  }

  onClientCommentChange(event: any, client: Client) {
    client.comment = event.target.value;
    this.updateClientFromTable(client);
  }


  // POLICY MODAL, POLICY TAB
  openPolicyModal(policy: Policy, template: TemplateRef<any>): void {
    this.modalPolicyLocked = true;
    this.resetFormControls();
    this.modalPolicy = ObjectUtils.deepClone(policy);
    this.modalPolicyParts = undefined;


    this.getModalPolicyParts(this.modalPolicy.id);
    this.fillModalPolicyFormControls();
    this.getPolicyLogs();

    const config = {
      id: 1,
      class: 'modal-xl',
      ignoreBackdropClick: true,
      keyboard: false,
    };

    this.modalRef = this.modalService.show(template, config);
  }

  resetFormControls() {
    this.modalPolicyType = new FormControl({value: null, disabled: this.modalPolicyLocked});
    this.modalPolicyInsurer = new FormControl({value: null, disabled: this.modalPolicyLocked});
    this.modalPolicyPaymentType = new FormControl({value: null, disabled: this.modalPolicyLocked});
    this.modalPolicyAgent = new FormControl({value: null, disabled: this.modalPolicyLocked});
    this.modalPolicyObject = new FormControl({value: null, disabled: this.modalPolicyLocked});
    this.modalPolicyNumber = new FormControl({value: null, disabled: this.modalPolicyLocked});
    this.modalPolicySum = new FormControl({value: null, disabled: this.modalPolicyLocked});
    this.modalPolicyTotalParts = new FormControl({value: 1, disabled: this.modalPolicyLocked});
    this.modalPolicyPercent = new FormControl(0);

    this.tabMailSubject = new FormControl(null);
    this.tabEmail1 = new FormControl(null);
    this.tabEmail2 = new FormControl(null);
    this.tabEmail3 = new FormControl(null);
    this.tabMailBody = new FormControl(null);
    this.tabBank = new FormControl(null);
    this.tabFilesSelected = new FormControl('');
    this.tabSendMeCopy = true;

    // SMS
    this.tabSmsSender = new FormControl(null);
    this.tabSmsReceiver = new FormControl(null);
    this.tabSmsText = new FormControl(null);
  }

  fillModalPolicyFormControls() {
    const existingPolicyType = this.policyTypes.find(
      (policyType) => policyType.id === this.modalPolicy.policyType?.id
    );
    if (existingPolicyType) {
      this.modalPolicyType.setValue(existingPolicyType);
    }

    const existingInsurer = this.insurers.find(
      (insurer) => insurer.id === this.modalPolicy.insurer?.id
    );
    if (existingInsurer) {
      this.modalPolicyInsurer.setValue(existingInsurer);
    }

    const existingAgent = this.agents.find(
      (agent) => agent.id === this.modalPolicy.agent?.id
    );
    if (existingAgent) {
      this.modalPolicyAgent.setValue(existingAgent);
    }

    const existingPayment = this.paymentTypes.find(
      (paymentType) => paymentType.id === this.modalPolicy.paymentType?.id
    );
    if (existingPayment) {
      this.modalPolicyPaymentType.setValue(existingPayment);
    }

    const existingParts = this.parts.find(
      (part) => part === this.modalPolicy.parts
    );
    if (existingParts) {
      this.modalPolicyTotalParts.setValue(existingParts);
    }

    if (this.modalPolicy.sum) {
      this.modalPolicySum.setValue(this.modalPolicy.sum);
    }

    if (this.modalPolicy.object) {
      this.modalPolicyObject.setValue(this.modalPolicy.object);
    }

    if (this.modalPolicy.policyNumber) {
      this.modalPolicyNumber.setValue(this.modalPolicy.policyNumber);
    }

    if (this.modalPolicy.percent) {
      this.modalPolicyPercent.setValue(this.modalPolicy.percent);
    } else {
      this.modalPolicyPercent.setValue(null);
    }
  }

  getModalPolicyParts(id: number): void {
    this.subscriptions.push(
      this.policyPartService.findAllByPolicyId(id).subscribe(
        (response) => {
          this.modalPolicyParts = PolicyPart.fromJsonList(response);
          this.modalPolicyPartsDb = PolicyPart.fromJsonList(response);
          this.modalPolicyPartsExcelStatus = Array(this.modalPolicyParts.length).fill(undefined);
          this.getModalPolicyPartsExcelStatuses();
        }
      )
    );
  }

  getPolicyLogs() {
    this.logService.getLogs(this.modalPolicy.folder).subscribe(
      (response) => {
        if (response.length > 0) {
          this.modalPolicyLogs = response;
        }
      }
    )
  }

  onModalLockButtonClicked() {
    this.modalPolicyLocked = !this.modalPolicyLocked;

    this.modalPolicyLocked ? this.modalPolicyNumber.disable() : this.modalPolicyNumber.enable();
    this.modalPolicyLocked ? this.modalPolicyObject.disable() : this.modalPolicyObject.enable();
    this.modalPolicyLocked ? this.modalPolicySum.disable() : this.modalPolicySum.enable();
    this.modalPolicyLocked ? this.modalPolicyTotalParts.disable() : this.modalPolicyTotalParts.enable();
    this.modalPolicyLocked ? this.modalPolicyType.disable() : this.modalPolicyType.enable();
    this.modalPolicyLocked ? this.modalPolicyAgent.disable() : this.modalPolicyAgent.enable();
    this.modalPolicyLocked ? this.modalPolicyInsurer.disable() : this.modalPolicyInsurer.enable();
    this.modalPolicyLocked ? this.modalPolicyType.disable() : this.modalPolicyType.enable();
    this.modalPolicyLocked ? this.modalPolicyPaymentType.disable() : this.modalPolicyPaymentType.enable();
  }

  openPolicyFolder(folder: string) {
    this.fileService.openPolicyFolder(folder).subscribe();
  }

  setPolicyClient(client: Client) {
    this.modalPolicy.client = client;
  }

  setAutoDay(propertyName: string) {
    if (propertyName === 'endDate' && this.modalPolicy.startDate) {
      const today = this.modalPolicy.startDate;
      const nextYear = new Date(today.getFullYear() + 1, today.getMonth(), today.getDate() - 1);
      this.modalPolicy.endDate = nextYear;
      this.modalPolicy.reminderDate = new Date(nextYear.getFullYear(), nextYear.getMonth() - 1, nextYear.getDate());
    } else if (propertyName === 'reminderDate' && this.modalPolicy.endDate) {
      const today = new Date(this.modalPolicy.endDate);
      const nextYear = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
      this.modalPolicy.reminderDate = nextYear;
    } else if (propertyName === 'startDate') {
      this.modalPolicy[propertyName] = new Date();
      this.onModalPolicyPartsChange();
    } else {
      this.modalPolicy[propertyName] = new Date();
    }
  }

  onModalPolicyPartsChange() {
    this.modalPolicyParts = [];
    for (let i = 0; i < this.modalPolicyTotalParts.value; i++) {
      let modalPolicyPart = this.modalPolicyPartsDb.find(part => part.part === i + 1);
      if (!modalPolicyPart) {
        modalPolicyPart = new PolicyPart();
        modalPolicyPart.policy = this.modalPolicy;
        modalPolicyPart.part = i + 1;
      }
      this.modalPolicyParts.push(modalPolicyPart);
    }

    // check if modalPolicyParts size are less than modalPolicyPartsDb size and hidden parts contains any invoices
    if (this.modalPolicyPartsDb.length > this.modalPolicyParts.length) {
      for (let i = this.modalPolicyParts.length; i < this.modalPolicyPartsDb.length; i++) {
        let partInvoice = this.modalPolicyPartsDb[i].invoice;
        if (partInvoice) {
          this.sendNotification(NotificationType.ERROR, "Удалится часть, в которой есть счет!");
        }
      }
    }

    // set modalPolicyParts dates and sums depends on modalPolicy.parts
    const durationPerPart = 12 / this.modalPolicyTotalParts.value;
    const startDate = new Date(this.modalPolicy.startDate);
    const sumPerPart = this.modalPolicySum.value / this.modalPolicyTotalParts.value;
    let sumOfPartsSoFar = 0;

    for (let i = 0; i < this.modalPolicyParts.length; i++) {
      this.modalPolicyParts[i].date = new Date(startDate.getFullYear(), startDate.getMonth() + durationPerPart * i, startDate.getDate());
      this.modalPolicyParts[i].sum = parseFloat(sumPerPart.toFixed(2));
      sumOfPartsSoFar += parseFloat(sumPerPart.toFixed(2));
    }

    // Adjust last part's sum
    let lastPartSum = this.modalPolicySum.value - sumOfPartsSoFar + this.modalPolicyParts[this.modalPolicyTotalParts.value - 1].sum;
    this.modalPolicyParts[0].sum = parseFloat(lastPartSum.toFixed(2));
  }

  saveModalPolicyParts() {
    this.modalPolicyParts.map(part => part.policy = this.modalPolicy);
    this.policyPartService.saveAll(this.modalPolicyParts).subscribe(
      (updatedParts) => {
        this.modalPolicyParts = updatedParts;
      }
    );
  }

  setAutoPercent(){
    if(this.modalPolicyInsurer.value && this.modalPolicyType.value){
      // find percent from percents
      const percent = this.percents.find(percent => percent.insurer.id === this.modalPolicyInsurer.value.id && percent.policyType.id === this.modalPolicyType.value.id);
      if(percent){
        this.modalPolicyPercent.setValue(percent.percent);
      }
    }
  }

  modalPolicySave() {
    this.updateModalPolicyValues();
    this.updateExistingModalPolicy(false);
  }

  updateExistingModalPolicy(fromInvoiceTab: boolean) {
    this.policyService.update(this.modalPolicy.id, this.modalPolicy).subscribe(
      (updatedPolicy) => {
        const index = this.policiesDataSource.data.findIndex(policy => policy.id === updatedPolicy.id);
        if (index !== -1) {
          this.saveModalPolicyParts();
          this.policiesDataSource.data[index] = Policy.fromJson(updatedPolicy);
          if(!fromInvoiceTab){
            this.modalRef.hide();
          }
          this.policiesDataSource.data = [...this.policiesDataSource.data];
        } else {
          this.sendNotification(NotificationType.ERROR, 'Ошибка обновления полиса');
        }
      },
      (error) => {
        this.sendNotification(NotificationType.ERROR, 'Ошибка сохранения полиса');
      }
    );
  }

  fetchPolicy(id: number): void {
    this.policyService.getById(id).subscribe(
      (policy) => {
        const index = this.policiesDataSource.data.findIndex(policy => policy.id === id);
        if (index !== -1) {
          this.policiesDataSource.data[index] = Policy.fromJson(policy);
          this.policiesDataSource.data = [...this.policiesDataSource.data];
        }
      }
    );
  }

  updateModalPolicyValues() {
    this.modalPolicyType.value?.id && (this.modalPolicy.policyType = this.modalPolicyType.value);
    this.modalPolicyInsurer?.value?.id && (this.modalPolicy.insurer = this.modalPolicyInsurer.value);
    this.modalPolicyPaymentType.value?.id && (this.modalPolicy.paymentType = this.modalPolicyPaymentType.value);
    this.modalPolicyAgent.value?.id && (this.modalPolicy.agent = this.modalPolicyAgent.value);
    this.modalPolicy.object = this.modalPolicyObject.value;
    this.modalPolicy.policyNumber = this.modalPolicyNumber.value;
    this.modalPolicy.sum = this.modalPolicySum.value;
    this.modalPolicy.parts = this.modalPolicyTotalParts.value;
    this.modalPolicy.percent = this.modalPolicyPercent.value;
  }

  setAutoPaymentDay(part: PolicyPart) {
    part.datePaid = new Date();
    part.sumReal = part.sum;
    // update policy data?
  }

  onPolicyProlongation(policy: Policy) {
    let newPolicy = new Policy();
    newPolicy.client = policy.client;
    newPolicy.object = policy.object;
    newPolicy.policyType = policy.policyType;
    newPolicy.sum = 0;
    newPolicy.parts = 1;
    newPolicy.reminder = true;
    newPolicy.startDate = new Date(policy.endDate.getFullYear(), policy.endDate.getMonth(), policy.endDate.getDate() + 2);

    let parts = [];
    let part = new PolicyPart();
    part.policy = newPolicy;
    part.part = 1;
    part.date = newPolicy.startDate;
    part.sum = 0;
    part.reminder = true;
    parts.push(part);

    this.policyService.save(newPolicy).subscribe(
      (response: Policy) => {
        parts.map(part => part.policy = response);
        this.policyPartService.saveAll(parts).subscribe(
          (response: PolicyPart[]) => {
            this.sendNotification(NotificationType.SUCCESS, 'Продление добавлено');
          },
          (error: any) => {
            this.sendNotification(NotificationType.ERROR, "Ошибка при добавлении продления (сохранение частей)");
          }
        )
      },
      (error: any) => {
        this.sendNotification(NotificationType.ERROR, "Ошибка при добавлении продления");
      }
    );
  }


  // INVOICE MODAL
  openInvoiceModal(part: PolicyPart, template: TemplateRef<any>) {
    if (!this.modalPolicy.id) {
      this.sendNotification(NotificationType.WARNING, 'Сохраните полис перед добавлением счета!');
      return;
    }

    // Check for missing fields in modalPolicy
    const missingPolicyFields = [];
    if (!this.modalPolicy.policyType) {
      missingPolicyFields.push('Тип полиса');
    }
    if (!this.modalPolicy.insurer) {
      missingPolicyFields.push('Страховщик');
    }
    if (!this.modalPolicy.client) {
      missingPolicyFields.push('Клиент');
    }
    if (!this.modalPolicy.object) {
      missingPolicyFields.push('Объект');
    }
    if (!this.modalPolicy.paymentType) {
      missingPolicyFields.push('Оплата');
    }
    if (!this.modalPolicy.policyNumber) {
      missingPolicyFields.push('Номер полиса');
    }
    if (!this.modalPolicy.startDate) {
      missingPolicyFields.push('Начало');
    }
    if (!this.modalPolicy.endDate) {
      missingPolicyFields.push('Конец');
    }
    if (!this.modalPolicy.sum) {
      missingPolicyFields.push('Сумма');
    }
    if (!this.modalPolicy.parts) {
      missingPolicyFields.push('Части');
    }

    // Check for missing fields in part
    const missingPartFields = [];
    if (!part.id) {
      missingPartFields.push('Часть');
    }
    if (!part.sum) {
      missingPartFields.push('Сумма части');
    }
    if (!part.date) {
      missingPartFields.push('Дата части');
    }

    if (missingPolicyFields.length > 0 || missingPartFields.length > 0) {
      const missingFields = missingPolicyFields.concat(missingPartFields);
      const missingFieldsString = missingFields.join(', ');
      this.sendNotification(NotificationType.WARNING, `Не хватает полей: ${missingFieldsString}!`);
      return;
    }

    this.modalPart = part;
    this.getPolicyFiles();
    this.modalInvoice = part.invoice;
    if (!this.modalInvoice) {
      this.modalInvoice = new Invoice();
      this.modalInvoice.conclusionDate = new Date();
      this.modalInvoice.maxDate = new Date();
      this.modalInvoice.maxDate.setDate(this.modalInvoice.maxDate.getDate() + 14);
      this.modalInvoice.text = this.getInvoiceText();
    }

    this.fillInvoiceForms();

    const config = {
      id: 2,
      class: 'modal-lg',
      ignoreBackdropClick: true,
      keyboard: false,
    };
    this.modalRef2 = this.modalService.show(template, config);
    document.getElementsByClassName('modal-lg')[0].parentElement.style.backgroundColor = 'rgba(0, 0, 0, 0.4)';
  }

  getInvoiceText(): string {
    let text = 'Kindlustuspoliis nr ' + this.modalPolicy.policyNumber + ', ';
    text += 'objekt ' + this.modalPolicy.object + ', ';
    text += 'kehtivus ' + this.datePipe.transform(this.modalPolicy.startDate, 'dd.MM.yyyy') + ' - ' + this.datePipe.transform(this.modalPolicy.endDate, 'dd.MM.yyyy') + ', ';
    text += 'osa ' + this.modalPolicy.parts + '/' + this.modalPart.part;
    return text;
  }

  fillInvoiceForms() {
    this.mailSubject.setValue(this.modalPart.policy.insurer.name + ' kindlustuse poliis');
    this.email1.setValue(this.modalPart.policy.client.email1);
    this.email2.setValue(this.modalPart.policy.client.email2);
    this.email3.setValue(this.modalPart.policy.client.email3);
    this.mailBody.setValue(this.properties['insa.email.default-text']);
  }

  generateInvoice() {
    if (this.modalPolicy.id) {
      this.invoiceService.save(this.modalInvoice, this.modalPart).subscribe(
        (result) => {
          this.modalInvoice = Invoice.fromJson(result);
          this.modalPart.invoice = Invoice.fromJson(result);
          this.getPolicyFiles();
          if(this.modalPolicy.paymentType.type != 'Ülekanne') {
            this.modalPolicy.paymentType = this.paymentTypes.find(pt => pt.type === 'Ülekanne');
            this.updateExistingModalPolicy(true);
            this.fillModalPolicyFormControls();
          }
        }
      );
    } else {
    }
  }

  sendInvoiceEmail() {
    let emailList = [this.email1.value, this.email2.value, this.email3.value];
    emailList = emailList.filter(email => email !== null && email !== '' && email !== undefined);
    const emailData = {
      from: this.properties['spring.mail.username'],
      to: emailList,
      subject: this.mailSubject.value,
      body: this.mailBody.value,
      folder: this.modalPolicy.folder,
      attachments: Array.isArray(this.filesSelected?.value) ? this.filesSelected?.value.map(s => s) : [],
      sendMeCopy: this.sendMeCopy
    };

    this.tabEmailLoading = true;
    this.emailService.sendEmailWithAttachments(emailData).subscribe(
      (response: any) => {
        this.sendNotification(NotificationType.SUCCESS, 'Письмо отправлено!');
        this.tabEmailLoading = false;
        this.getPolicyLogs();
      },
      (error: any) => {
        console.log(error);
        this.sendNotification(NotificationType.ERROR, error.error.message);
        this.tabEmailLoading = false;
      }
    );
  }


  // POLICY MODAL, MAILING-SMS TAB + SMS MODAL
  onMailingTabSelect() {
    this.fillMailingTabMailSection();
    this.fillSmsSection();
    this.getPolicyFiles();
  }

  fillMailingTabMailSection() {
    this.tabMailSubject.setValue(this.modalPolicy.insurer?.name + ' kindlustuse poliis');
    this.tabEmail1.setValue(this.modalPolicy.client?.email1);
    this.tabEmail2.setValue(this.modalPolicy.client?.email2);
    this.tabEmail3.setValue(this.modalPolicy.client?.email3);
    this.tabMailBody.setValue(this.properties['insa.email.default-text']);

    const clientBank = this.banks.find(bank => bank.id === this.modalPolicy.client?.bank?.id);
    if (clientBank) {
      this.tabBank.setValue(clientBank);
    }
  }

  fillSmsSection() {
    this.tabSmsSender.setValue(this.properties['insa.sms.sender']);
    this.tabSmsReceiver.setValue(this.modalPolicy.client?.mobile1);
    if (this.modalPolicy.policyType?.type === 'LK') {
      this.tabSmsText.setValue(
        this.modalPolicy.object + ' ' +
        (this.tabSmsLanguageRus ? this.properties['insa.sms.text-rus'] : this.properties['insa.sms.text-est']) + ' ' +
        this.datePipe.transform(this.modalPolicy.endDate, 'dd.MM.yyyy') +
        '\n\nKB Steiger');
    }
  }

  sendSms(fromTab: boolean) {
    let smsData: SmsData;
    if(fromTab) {
      smsData = {
        policyId: this.modalPolicy.id,
        from: this.tabSmsSender.value,
        to: this.tabSmsReceiver.value,
        message: this.tabSmsText.value,
        folder: this.modalPolicy.folder
      }
    } else {
      smsData = {
        policyId: this.modalPolicy.id,
        from: this.modalSmsSender.value,
        to: this.modalSmsReceiver.value,
        message: this.modalSmsText.value,
        folder: this.modalPolicy.folder
      }
    }

    this.tabSmsLoading = true;

    this.smsService.sendSms(smsData).subscribe(
      (response: any) => {
        this.sendNotification(NotificationType.SUCCESS, 'Сообщение отправлено! Баланс: ' + response.balance);
        this.tabSmsLoading = false;
        this.getPolicyLogs();
        this.fetchPolicy(this.modalPolicy.id);
      },
      (error: any) => {
        console.log(error);
        this.sendNotification(NotificationType.ERROR, "Ошибка! " + error.error.message);
        this.tabSmsLoading = false;
      }
    );
  }

  smsChangeLanguage(modal: boolean) {
    if(modal){
      this.modalSmsLanguageRus = !this.modalSmsLanguageRus;
      if (this.modalPolicy.policyType?.type === 'LK') {
        this.modalSmsText.setValue(
          this.modalPolicy.object + ' ' +
          (this.modalSmsLanguageRus ? this.properties['insa.sms.text-rus'] : this.properties['insa.sms.text-est']) + ' ' +
          this.datePipe.transform(this.modalPolicy.endDate, 'dd.MM.yyyy') +
          '\n\nKB Steiger');
      }
    } else {
      this.tabSmsLanguageRus = !this.tabSmsLanguageRus;
      if (this.modalPolicy.policyType?.type === 'LK') {
        this.tabSmsText.setValue(
          this.modalPolicy.object + ' ' +
          (this.tabSmsLanguageRus ? this.properties['insa.sms.text-rus'] : this.properties['insa.sms.text-est']) + ' ' +
          this.datePipe.transform(this.modalPolicy.endDate, 'dd.MM.yyyy') +
          '\n\nKB Steiger');
      }
    }
  }a

  sendTabEmail() {
    let emailList = [this.tabEmail1.value, this.tabEmail2.value, this.tabEmail3.value, this.tabBank.value?.email];
    emailList = emailList.filter(email => email !== null && email !== '' && email !== undefined);
    const emailData = {
      from: this.properties['spring.mail.username'],
      to: emailList,
      subject: this.tabMailSubject.value,
      body: this.tabMailBody.value,
      folder: this.modalPolicy.folder,
      attachments: Array.isArray(this.tabFilesSelected?.value) ? this.tabFilesSelected?.value.map(s => s) : [],
      sendMeCopy: this.tabSendMeCopy
    };

    this.tabEmailLoading = true;
    this.emailService.sendEmailWithAttachments(emailData).subscribe(
      (response: any) => {
        this.sendNotification(NotificationType.SUCCESS, 'Письмо отправлено!');
        this.tabEmailLoading = false;
        this.getPolicyLogs();
      },
      (error: any) => {
        console.log(error);
        this.sendNotification(NotificationType.ERROR, error.error.message);
        this.tabEmailLoading = false;
      }
    );
  }

  getPolicyFiles() {
    this.fileService.getFolderFiles(this.modalPolicy.folder).subscribe(
      (result) => {
        console.log('getPolicyFiles 2')
        this.policyFiles = result;
      }
    );
  }

  openSmsModal(policy: Policy, template: TemplateRef<any>) {
    const config = {
      id: 1,
      class: 'modal-sm',
    };
    this.modalPolicy = policy;
    this.modalRef = this.modalService.show(template, config);
    this.modalSmsReceiver = new FormControl(null);
    this.modalSmsText = new FormControl(null);
    this.modalSmsSender.setValue(this.properties['insa.sms.sender']);
    this.modalSmsReceiver.setValue(policy.client?.mobile1);
    if (policy.policyType?.type === 'LK') {
      this.modalSmsText.setValue(
        policy.object + ' ' +
        (this.modalSmsLanguageRus ? this.properties['insa.sms.text-rus'] : this.properties['insa.sms.text-est']) + ' ' +
        this.datePipe.transform(policy.endDate, 'dd.MM.yyyy') +
        '\n\nKB Steiger');
    }
  }


  // CLIENT MODAL
  onEditClientModal(client: Client, template: TemplateRef<any>) {
    this.selectedClient = client;
    this.selectedClientBank = new FormControl({value: null});

    if (client.bank) {
      const clientBank = this.banks.find(bank => bank.id === client.bank.id);
      if (clientBank) {
        this.selectedClientBank.setValue(clientBank);
      }
    }

    const config = {
      id: 1,
      class: 'modal-lg',
      ignoreBackdropClick: true,
      keyboard: false,
    };
    this.modalRef = this.modalService.show(template, config);
    document.getElementsByClassName('modal-lg')[0].parentElement.style.backgroundColor = 'rgba(0, 0, 0, 0.4)';
  }

  onClientSave() {
    this.selectedClientBank.value?.id && (this.selectedClient.bank = this.selectedClientBank.value);
    this.clientService.save(this.selectedClient).subscribe(
      (response: Client) => {
        this.policiesDataSource.data.forEach(policy => {
          if (policy.client.id === response.id) {
            policy.client = response;
          }
        })
        this.modalRef?.hide();
      }
    )
  }

  // GOOGLE SHEETS
  checkPartExcel(part: PolicyPart) {
    this.modalPolicyPartsExcelStatus[this.modalPolicyParts.indexOf(part)] = undefined;
    this.googleService.isPartInserted(part).subscribe(
      (response) => {
        if (response.status === 'inserted') {
          this.modalPolicyPartsExcelStatus[this.modalPolicyParts.indexOf(part)] = true;
        } else if (response.status === 'missing') {
          this.modalPolicyPartsExcelStatus[this.modalPolicyParts.indexOf(part)] = false;
        }
      },
      (error) => {
        this.sendNotification(NotificationType.ERROR, 'Ошибка проверки части в Excel');
      }
    );
  }

  getModalPolicyPartsExcelStatuses() {
    this.modalPolicyParts.forEach(part => {
      this.googleService.isPartInserted(part).subscribe(
        (response) => {
          if (response.status === 'inserted') {
            this.modalPolicyPartsExcelStatus[this.modalPolicyParts.indexOf(part)] = true;
          } else if (response.status === 'missing') {
            this.modalPolicyPartsExcelStatus[this.modalPolicyParts.indexOf(part)] = false;
          }
        },
        (error) => {
          this.sendNotification(NotificationType.ERROR, 'Ошибка проверки Excel');
        }
      );
    });
  }

  insertPartExcel(part: PolicyPart) {
    this.modalPolicyPartsExcelStatus[this.modalPolicyParts.indexOf(part)] = undefined;
    this.googleService.insertPart(part).subscribe(
      (response) => {
        if (response.status === 'inserted') {
          this.modalPolicyPartsExcelStatus[this.modalPolicyParts.indexOf(part)] = true;
        } else if (response.status === 'not inserted') {
          this.modalPolicyPartsExcelStatus[this.modalPolicyParts.indexOf(part)] = false;
        }
      },
      (error) => {
        this.sendNotification(NotificationType.ERROR, 'Ошибка добавления Excel');
      }
    );
  }
}
