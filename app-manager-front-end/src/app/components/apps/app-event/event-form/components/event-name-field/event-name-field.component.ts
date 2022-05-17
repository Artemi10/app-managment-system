import { Component, OnInit } from '@angular/core';
import {ControlContainer, FormGroup} from "@angular/forms";
import { isInputInvalid } from 'src/app/components/auth/utils/utils';

@Component({
  selector: 'app-event-name-field',
  templateUrl: './event-name-field.component.html',
  styleUrls: ['./event-name-field.component.css']
})
export class EventNameFieldComponent implements OnInit {
  public eventNameFormGroup: FormGroup | null;

  constructor(private controlContainer: ControlContainer) {
    this.eventNameFormGroup = null;
  }

  ngOnInit(): void {
    // @ts-ignore
    this.eventNameFormGroup = this.controlContainer.control;
  }

  public get isInputInvalid(): boolean {
    if (this.eventNameFormGroup != null){
      return isInputInvalid(this.eventNameFormGroup, 'eventName');
    }
    return false;
  }
}
