import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-dropdown',
  templateUrl: './dropdown.component.html',
  styleUrls: ['./dropdown.component.css']
})
export class DropdownComponent {
  @Input()
  public elements: DropdownElement[];
  @Output()
  public selectedItemChange: EventEmitter<string>;

  constructor() {
    this.elements = []
    this.selectedItemChange = new EventEmitter<string>();
  }

  public selectItem(key: string) {
    this.elements
      .forEach((value) => value.selected = value.key == key);
    this.selectedItemChange.emit(key);
  }

}

export class DropdownElement {
  private readonly _key: string;
  private readonly _name: string;
  private _selected: boolean;

  constructor(key: string, name: string, selected: boolean) {
    this._key = key;
    this._name = name;
    this._selected = selected;
  }

  get key(): string {
    return this._key;
  }

  get name(): string {
    return this._name;
  }

  get selected(): boolean {
    return this._selected;
  }

  set selected(value: boolean) {
    this._selected = value;
  }
}
