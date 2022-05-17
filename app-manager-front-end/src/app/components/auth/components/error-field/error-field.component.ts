import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-error-field',
  templateUrl: './error-field.component.html',
  styleUrls: ['./error-field.component.css']
})
export class ErrorFieldComponent implements OnInit {
  @Input() public error: string;

  constructor() {
    this.error = '';
  }

  public get isVisible(): boolean {
    return this.error != '';
  }

  ngOnInit(): void {
  }

}
