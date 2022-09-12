import {Component, EventEmitter, Input, Output} from '@angular/core';
import {DropdownElement} from "../../sort.criteria";

@Component({
  selector: 'app-dropdown-element',
  templateUrl: './dropdown-element.component.html',
  styleUrls: ['./dropdown-element.component.css']
})
export class DropdownElementComponent {
  @Input()
  public element: DropdownElement;
  @Output()
  public selectItemEvent: EventEmitter<string>;

  constructor() {
    this.element = new DropdownElement();
    this.selectItemEvent = new EventEmitter();
  }

  public selectElement() {
    this.selectItemEvent.emit(this.element.key);
  }

}
