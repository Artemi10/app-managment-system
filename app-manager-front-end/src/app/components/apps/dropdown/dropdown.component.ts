import {Component, EventEmitter, Output} from '@angular/core';

@Component({
  selector: 'app-dropdown',
  templateUrl: './dropdown.component.html',
  styleUrls: ['./dropdown.component.css']
})
export class DropdownComponent {
  public isIdSelected: boolean;
  public isNameSelected: boolean;
  public isDateSelected: boolean;
  @Output()
  public selectedItemChange: EventEmitter<string>;

  constructor() {
    this.isIdSelected = true;
    this.isNameSelected = false;
    this.isDateSelected = false;
    this.selectedItemChange = new EventEmitter<string>();
  }

  public selectId(){
    this.isNameSelected = false;
    this.isIdSelected = true;
    this.isDateSelected = false;
    this.selectedItemChange.emit("id");
  }

  public selectName(){
    this.isNameSelected = true;
    this.isIdSelected = false;
    this.isDateSelected = false;
    this.selectedItemChange.emit("name");
  }

  public selectDate(){
    this.isNameSelected = false;
    this.isIdSelected = false;
    this.isDateSelected = true;
    this.selectedItemChange.emit("creationTime");
  }

}
