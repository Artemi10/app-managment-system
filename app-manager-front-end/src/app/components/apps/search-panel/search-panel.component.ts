import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-search-panel',
  templateUrl: './search-panel.component.html',
  styleUrls: ['./search-panel.component.css']
})
export class SearchPanelComponent implements OnInit {
  @Output() public closeEvent: EventEmitter<any>;

  constructor() {
    this.closeEvent = new EventEmitter<any>();
  }

  ngOnInit(): void {
  }

  public closePanel() {
    this.closeEvent.emit();
  }


}
