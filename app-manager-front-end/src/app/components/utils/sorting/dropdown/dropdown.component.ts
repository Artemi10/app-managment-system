import {Component, EventEmitter, Input, Output} from '@angular/core';
import {SortCriteria} from "../sort.criteria";

@Component({
  selector: 'app-dropdown',
  templateUrl: './dropdown.component.html',
  styleUrls: ['./dropdown.component.css']
})
export class DropdownComponent {
  @Input()
  public sortCriteria: SortCriteria;
  @Output()
  public sortCriteriaChange: EventEmitter<SortCriteria>;

  constructor() {
    this.sortCriteria = new SortCriteria();
    this.sortCriteriaChange = new EventEmitter<SortCriteria>();
  }

  public selectItem(key: string) {
    this.sortCriteria.selectSortField(key);
    this.sortCriteriaChange.emit(this.sortCriteria);
  }

}
