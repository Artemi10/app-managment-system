import {AfterViewInit, Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';

@Component({
  selector: 'app-date-field',
  templateUrl: './date-field.component.html',
  styleUrls: ['./date-field.component.css']
})
export class DateFieldComponent implements AfterViewInit {
  @ViewChild("dateElement")
  public dateElement: ElementRef<HTMLInputElement> | undefined;
  @Input() public name: string;
  @Output() public dateChange: EventEmitter<string>;
  private datePicker: any;

  constructor() {
    this.name = '';
    this.dateChange = new EventEmitter<string>();
  }

  ngAfterViewInit(): void {
    const dateOptions = {
      format : 'yyyy-mm-dd',
      onSelect : () => this.dateChange.emit(this.datePicker.toString())
    };
    if (this.dateElement !== undefined) {
      // @ts-ignore
      this.datePicker = M.Datepicker.init(this.dateElement.nativeElement, dateOptions)
    }
  }
}
