import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-search-panel',
  templateUrl: './search-panel.component.html',
  styleUrls: ['./search-panel.component.css']
})
export class SearchPanelComponent  {
  @Output()
  public closeEvent: EventEmitter<any>;
  @Input()
  public searchName: string;
  @Output()
  public searchNameChange: EventEmitter<string>;

  constructor() {
    this.closeEvent = new EventEmitter<any>();
    this.searchNameChange = new EventEmitter<string>();
    this.searchName = '';
  }

  public closePanel() {
    this.closeEvent.emit();
  }

  public search() {
    if (this.searchName !== '') {
      this.searchNameChange.emit(this.searchName);
    }
  }


}
