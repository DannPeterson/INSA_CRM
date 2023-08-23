import {Component, OnInit, TemplateRef} from '@angular/core';
import {Client} from "../model/client";
import {Router} from "@angular/router";
import {BankService} from "../service/bank.service";
import {ClientService} from "../service/client.service";
import {BsModalRef, BsModalService} from "ngx-bootstrap/modal";
import {Subscription} from "rxjs";
import {Bank} from "../model/bank";
import {PageEvent} from "@angular/material/paginator";
import {FormControl} from "@angular/forms";

@Component({
  selector: 'app-clients',
  templateUrl: './clients.component.html',
  styleUrls: ['./clients.component.css']
})
export class ClientsComponent implements OnInit {
  displayedColumns: string[] = ['col1', 'col2', 'col3', 'col4', 'col5', 'col6'];
  clients: Client[];
  banks: Bank[];
  modalRef?: BsModalRef | null;
  subscriptions: Subscription[] = [];

  // NEW CLIENT
  modalNewClient: Client;
  modalNewClientBank = new FormControl({value: null});

  //PAGINATION
  totalClients = 0;
  pageSize = 100;
  pageIndex = 0;

  //SELECTED CLIENT
  selectedClient: Client;
  selectedClientBank = new FormControl({value: null});

  //SEARCH
  searchName = new FormControl(null);
  searchEmail = new FormControl(null);
  searchMobile = new FormControl(null);


  constructor(private router: Router,
              private bankService: BankService,
              private clientService: ClientService,
              private modalService: BsModalService) {
  }

  ngOnInit(): void {
    this.searchClients();
    this.getBanks();
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

  onPageChange(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.searchClients();
  }

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

  onNewClientModal(template: TemplateRef<any>) {
    this.modalNewClient = new Client();
    this.selectedClientBank = new FormControl({value: null});
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
        const index = this.clients.findIndex(client => client.id === response.id);
        if (index != -1) {
          this.clients[index] = response;
        }
        this.modalRef?.hide();
      }
    )
  }

  onNewClientSave(){
    this.modalNewClientBank.value?.id && (this.modalNewClient.bank = this.modalNewClientBank.value);
    this.modalNewClient.id = null;
    this.clientService.save(this.modalNewClient).subscribe(
      (response: Client) => {
        this.clients.push(response);
        this.modalRef?.hide();
      }
    )
  }

  searchClients() {
    this.clientService.search(
      this.searchName.value ? this.searchName.value : null,
      this.searchEmail.value ? this.searchEmail.value : null,
      this.searchMobile.value ? this.searchMobile.value : null,
      this.pageIndex,
      this.pageSize
    ).subscribe(
      response => {
        this.clients = response.content;
        this.totalClients = response.totalElements;
      }
    )
  }

  cleanSearchFields() {
    this.searchName.setValue(null);
    this.searchEmail.setValue(null);
    this.searchMobile.setValue(null);
    this.searchClients()
  }
}
