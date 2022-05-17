import { Component } from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {getErrorMessage} from "../../../service/utils/error.utils";
import {AuthService} from "../../../service/auth/auth.service";
import {TokenService} from "../../../service/token/token.service";
import {Router} from "@angular/router";
import {SignUpModel, Token} from "../../../model/auth.models";

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css']
})
export class SignUpComponent {
  public signUpForm: FormGroup;
  public _errorMessage: string;

  constructor(private formBuilder: FormBuilder,
              private authService: AuthService,
              private tokenService: TokenService,
              private router: Router) {
    this._errorMessage = '';
    this.signUpForm = formBuilder.group({
      emailField: formBuilder.group({
        email: new FormControl('', Validators.required)
      }),
      passwordField: formBuilder.group({
        password: new FormControl('', new FormControl(''))
      }),
      rePasswordField: formBuilder.group({
        rePassword: new FormControl('', new FormControl(''))
      })
    });
  }

  public get errorMessage(): string {
    return this._errorMessage;
  }

  private get requestBody(): SignUpModel {
    const formValue = this.signUpForm.value;
    return {
      email: formValue.emailField.email,
      password: formValue.passwordField.password,
      rePassword: formValue.rePasswordField.rePassword
    }
  }

  public signUp() {
    this.authService.signUp(this.requestBody)
      .subscribe({
        next : this.signedUpHandler.bind(this),
        error : this.errorHandler.bind(this)
      });
  }

  private signedUpHandler(token: Token) {
    this.tokenService.setToken(token);
    this.router.navigate(['/']);
  }

  private errorHandler(error: { status: number; error: { message: string; }; }) {
    this._errorMessage = getErrorMessage(error);
  }
}
