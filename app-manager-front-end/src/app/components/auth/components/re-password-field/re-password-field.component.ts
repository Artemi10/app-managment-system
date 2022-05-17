import {Component, OnInit} from '@angular/core';
import {ControlContainer, FormGroup} from "@angular/forms";
import {isInputInvalid} from "../../utils/utils";

@Component({
  selector: 'app-re-password-field',
  templateUrl: './re-password-field.component.html',
  styleUrls: ['./re-password-field.component.css']
})
export class RePasswordFieldComponent implements OnInit {
  public rePasswordFormGroup: FormGroup | null;

  constructor(private controlContainer: ControlContainer) {
    this.rePasswordFormGroup = null;
  }

  ngOnInit(): void {
    // @ts-ignore
    this.rePasswordFormGroup = this.controlContainer.control;
  }

  public get isInputInvalid(): boolean {
    if (this.rePasswordFormGroup != null){
      return isInputInvalid(this.rePasswordFormGroup, 'rePassword');
    }
    return false;
  }
}
