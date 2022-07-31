import { Component, OnInit } from '@angular/core';
import {ControlContainer, FormGroup} from "@angular/forms";
import { isInputInvalid } from 'src/app/components/auth/utils/utils';

@Component({
  selector: 'app-extra-inf-field',
  templateUrl: './extra-inf-field.component.html',
  styleUrls: ['./extra-inf-field.component.css']
})
export class ExtraInfFieldComponent implements OnInit {
  public extraInfFormGroup: FormGroup | null;

  constructor(private controlContainer: ControlContainer) {
    this.extraInfFormGroup = null;
  }

  ngOnInit(): void {
    // @ts-ignore
    this.extraInfFormGroup = this.controlContainer.control;
  }

  public get isInputInvalid(): boolean {
    if (this.extraInfFormGroup != null){
      return isInputInvalid(this.extraInfFormGroup, 'extraInf');
    }
    return false;
  }

}
