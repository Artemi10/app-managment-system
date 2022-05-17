import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import { Router } from '@angular/router';
import { AccessToken } from 'src/app/model/auth.models';
import { UserService } from 'src/app/service/user/user.service';
import {TokenService} from "../../../service/token/token.service";
import {getErrorMessage} from "../../../service/utils/error.utils";

@Component({
  selector: 'app-reset-email',
  templateUrl: './reset-email.component.html',
  styleUrls: ['./reset-email.component.css']
})
export class ResetEmailComponent {
  public resetEmailForm: FormGroup;
  public _errorMessage: string;

  constructor(private formBuilder: FormBuilder,
              private tokenService: TokenService,
              private userService: UserService,
              private router: Router) {
    this._errorMessage = '';
    this.resetEmailForm = formBuilder.group({
      emailField: this.formBuilder.group({
        email:  new FormControl('')
      })
    });
  }

  public get errorMessage(): string {
    return this._errorMessage;
  }

  public resetEmail() {
    const email = this.resetEmailForm.value.emailField.email;
    this.userService.resetUser(email)
      .subscribe({
        next : this.addToken.bind(this),
        error : this.handleError.bind(this)
      });
  }

  private handleError(error: { status: number; error: { message: string; }; }) {
    this._errorMessage = getErrorMessage(error);
  }

  private addToken(accessToken: AccessToken) {
    this.tokenService.removeToken();
    this.tokenService.setAccessToken(accessToken);
    this.router.navigate(['/auth/reset/confirm']);
  }

}
