import {AfterViewInit, ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {MatSort} from "@angular/material/sort";
import {MatTable, MatTableDataSource} from "@angular/material/table";
import {BsModalRef, BsModalService} from "ngx-bootstrap/modal";
import {Subscription} from "rxjs";
import {Policy} from "../model/policy";
import {Agent} from "../model/agent";
import {Client} from "../model/client";
import {Insurer} from "../model/insurer";
import {Bank} from "../model/bank";
import {PaymentType} from "../model/paymenttype";
import {InsurerTypeProfitPercent} from "../model/insurertypeprofitpercent";
import {PolicyType} from "../model/policytype";
import {PolicyPart} from "../model/policypart";
import {FormControl} from "@angular/forms";
import {Invoice} from "../model/invoice";
import {Router} from "@angular/router";
import {PropertyService} from "../service/property.service";
import {EmailService} from "../service/email.service";
import {SmsService} from "../service/sms.service";
import {AgentService} from "../service/agent.service";
import {BankService} from "../service/bank.service";
import {ClientService} from "../service/client.service";
import {InsurerService} from "../service/insurer.service";
import {InvoiceService} from "../service/invoice.service";
import {PaymentTypeService} from "../service/payment-type.service";
import {PolicyService} from "../service/policy.service";
import {PolicyPartService} from "../service/policy-part.service";
import {PolicyTypeService} from "../service/policy-type.service";
import {PercentService} from "../service/percent.service";
import {FileService} from "../service/file.service";
import {LogService} from "../service/log.service";
import {NotificationService} from "../service/notification.service";
import {GoogleService} from "../service/google.service";
import {DatePipe} from "@angular/common";
import {NotificationType} from "../enum/notification-type.enum";

@Component({
  selector: 'app-debts',
  templateUrl: './debts.component.html',
  styleUrls: ['./debts.component.css']
})
export class DebtsComponent implements OnInit, AfterViewInit  {
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild('partsTable') partsTable: MatTable<any>;
  displayedColumns: string[] = ['menu', 'policyType', 'paymentType', 'client', 'object', 'insurer', 'agent', 'policyNumber', 'startDate', 'endDate', 'policySum', 'parts', 'partDate', 'invoice', 'maxPaymentDay', 'paymentSum', 'percent', 'provision'];
  modalRef?: BsModalRef | null;
  modalRef2?: BsModalRef;
  subscriptions: Subscription[] = [];

  // COMMON
  properties: { [key: string]: string } = {};
  policies: Policy[];
  partsDataSource = new MatTableDataSource<PolicyPart>();
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
              private datePipe: DatePipe) {}

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

  ngAfterViewInit(): void {
    this.partsDataSource.sort = this.sort;
    this.partsDataSource.sortingDataAccessor = (item, property) => {
      switch(property) {
        case 'client': return item.policy.client?.name;
        case 'insurer': return item.policy.insurer?.name;
        default: return item[property];
      }
    };
    this.partsDataSource.sort = this.sort;
    // this.searchPolicies();
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

}
