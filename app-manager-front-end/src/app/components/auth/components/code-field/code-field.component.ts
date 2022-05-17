import { Component, OnInit } from '@angular/core';
import {ControlContainer, FormGroup} from "@angular/forms";
import { isInputInvalid } from '../../utils/utils';

@Component({
  selector: 'app-code-field',
  templateUrl: './code-field.component.html',
  styleUrls: ['./code-field.component.css']
})
export class CodeFieldComponent implements OnInit {
  public confirmationFormGroup: FormGroup | null;

  constructor(private controlContainer: ControlContainer) {
    this.confirmationFormGroup = null;
  }

  ngOnInit(): void {
    // @ts-ignore
    this.confirmationFormGroup = this.controlContainer.control;
  }

  public get isInputInvalid(): boolean {
    if (this.confirmationFormGroup != null){
      return isInputInvalid(this.confirmationFormGroup, 'code');
    }
    return false;
  }}
