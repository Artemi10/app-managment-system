import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-search-panel',
  templateUrl: './search-panel.component.html',
  styleUrls: ['./search-panel.component.css']
})
export class SearchPanelComponent implements OnInit {
  @Output() public closeEvent: EventEmitter<any>;
  @Output() public searchEvent: EventEmitter<string>;
  public appName: string;

  constructor() {
    this.closeEvent = new EventEmitter<any>();
    this.searchEvent = new EventEmitter<string>();
    this.appName = '';
  }

  ngOnInit(): void {
  }

  public closePanel() {
    this.closeEvent.emit();
  }

  public search() {
    if (this.appName !== '') {
      this.searchEvent.emit(this.appName);
    }
  }


}
