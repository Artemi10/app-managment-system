import { Component, OnInit } from '@angular/core';
import {ControlContainer, FormGroup} from "@angular/forms";
import { isInputInvalid } from 'src/app/components/auth/utils/utils';

@Component({
  selector: 'app-name-field',
  templateUrl: './name-field.component.html',
  styleUrls: ['./name-field.component.css']
})
export class NameFieldComponent implements OnInit {
  public nameFormGroup: FormGroup | null;

  constructor(private controlContainer: ControlContainer) {
    this.nameFormGroup = null;
  }

  ngOnInit(): void {
    // @ts-ignore
    this.nameFormGroup = this.controlContainer.control;
  }

  public get isInputInvalid(): boolean {
    if (this.nameFormGroup != null){
      return isInputInvalid(this.nameFormGroup, 'name');
    }
    return false;
  }
}
