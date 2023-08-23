import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import {Client} from "../../../model/client";

@Component({
  selector: 'app-client-select',
  templateUrl: './client-select.component.html',
  styleUrls: ['./client-select.component.scss']
})
export class ClientSelectComponent implements OnInit {
  @Input() clients: any[] = [];
  @Input() modalPolicy: any;
  @Input() modalPolicyLocked: boolean;
  @Output() clientSelected = new EventEmitter<any>();

  clientSearchControl = new FormControl();
  filteredClients: Observable<any[]>;

  ngOnInit() {
    this.clientSearchControl.setValue(this.modalPolicy.client);
    this.filteredClients = this.clientSearchControl.valueChanges.pipe(
      startWith(''),
      map(value => typeof value === 'string' ? value : value.name),
      map(name => this._filterClients(name))
    );
    if (this.modalPolicyLocked) {
      this.clientSearchControl.disable();
    }
  }

  ngOnChanges() {
    if (this.modalPolicyLocked) {
      this.clientSearchControl.disable();
    } else {
      this.clientSearchControl.enable();
    }
  }

  onBlur() {
    if(this.modalPolicy.client && this.modalPolicy.client.id) {
      const selectedClient = this.clients.find(client => client.id === this.modalPolicy.client.id);
      this.clientSearchControl.setValue(selectedClient);
    }
  }

  private _filterClients(name: string): any[] {
    const filterValue = name.toLowerCase();
    return this.clients.filter(client => client.name.toLowerCase().includes(filterValue));
  }

  onSelectionChange(client) {
    if(!this.modalPolicy.client) {
      this.modalPolicy.client = new Client();
    }
    this.modalPolicy.client.id = client.id;
    this.clientSelected.emit(client);
  }

  displayFn(client: any): string {
    return client && client.name ? client.name : '';
  }
}
