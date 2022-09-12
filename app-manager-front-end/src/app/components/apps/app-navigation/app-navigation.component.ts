import {AfterViewInit, Component, EventEmitter, Input, Output} from '@angular/core';
import {SortCriteria} from "../../utils/sorting/sort.criteria";
import {Router} from "@angular/router";

@Component({
  selector: 'app-app-navigation',
  templateUrl: './app-navigation.component.html',
  styleUrls: ['./app-navigation.component.css']
})
export class AppNavigationComponent implements AfterViewInit {
  @Input()
  public _sortCriteria: SortCriteria;
  @Output()
  public _sortCriteriaChange: EventEmitter<SortCriteria>;
  @Output()
  public openPanelEvent: EventEmitter<any>;

  constructor(private router: Router) {
    this._sortCriteria = new SortCriteria();
    this._sortCriteriaChange = new EventEmitter<SortCriteria>();
    this.openPanelEvent = new EventEmitter();
  }

  ngAfterViewInit() {
    const elems = document.querySelectorAll('.dropdown-trigger');
    // @ts-ignore
    const instances = M.Dropdown.init(elems, {});
  }

  get sortCriteria(): SortCriteria {
    return this._sortCriteria;
  }

  set sortCriteria(value: SortCriteria) {
    this._sortCriteria = value;
    this._sortCriteriaChange.emit(this._sortCriteria);
  }

  public changeDescending() {
    this.sortCriteria.changeOrderType();
    this._sortCriteriaChange.emit(this._sortCriteria);
  }

  public createApp() {
    this.router.navigate(['/app/create']);
  }

  public openPanel() {
    this.openPanelEvent.emit();
  }
}
