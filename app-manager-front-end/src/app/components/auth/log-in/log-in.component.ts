import { Component } from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import { Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth/auth.service';
import { TokenService } from 'src/app/service/token/token.service';
import { getErrorMessage } from 'src/app/service/utils/error.utils';
import {LogInModel, Token} from "../../../model/auth.models";

@Component({
  selector: 'app-log-in',
  templateUrl: './log-in.component.html',
  styleUrls: ['./log-in.component.css']
})
export class LogInComponent {
  public logInForm: FormGroup;
  private _errorMessage: string;

  constructor(private formBuilder: FormBuilder,
              private authService: AuthService,
              private tokenService: TokenService,
              private router: Router) {
    this._errorMessage = '';
    this.logInForm = formBuilder.group({
      emailField: this.formBuilder.group({
        email:  new FormControl('')
      }),
      passwordField: this.formBuilder.group({
        password:  new FormControl('')
      })
    });
  }

  public get errorMessage(): string {
    return this._errorMessage;
  }

  private get requestBody(): LogInModel {
    const formValue = this.logInForm.value;
    return {
      email: formValue.emailField.email,
      password: formValue.passwordField.password
    }
  }

  public logIn() {
    this.authService.logIn(this.requestBody)
      .subscribe({
        next : this.handleLogIn.bind(this),
        error : this.handleError.bind(this)
      });
  }

  private handleLogIn(token: Token) {
    this.tokenService.setToken(token);
    this.router.navigate(['/']);
  }

  private handleError(error: { status: number; error: { message: string; }; }) {
    this._errorMessage = getErrorMessage(error);
  }

}
