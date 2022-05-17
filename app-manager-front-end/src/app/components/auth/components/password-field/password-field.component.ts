import {Component, Input, OnInit} from '@angular/core';
import {ControlContainer, FormGroup} from "@angular/forms";
import {isInputInvalid} from "../../utils/utils";

@Component({
  selector: 'app-password-field',
  templateUrl: './password-field.component.html',
  styleUrls: ['./password-field.component.css']
})
export class PasswordFieldComponent implements OnInit {
  @Input()
  public name: string;
  public passwordFormGroup: FormGroup | null;
  public password: string;

  constructor(private controlContainer: ControlContainer) {
    this.name = 'Password';
    this.passwordFormGroup = null;
    this.password = '';
  }

  ngOnInit(): void {
    // @ts-ignore
    this.passwordFormGroup = this.controlContainer.control;
  }

  public get isInputInvalid(): boolean {
    if (this.passwordFormGroup != null){
      return isInputInvalid(this.passwordFormGroup, 'password');
    }
    return false;
  }
}
