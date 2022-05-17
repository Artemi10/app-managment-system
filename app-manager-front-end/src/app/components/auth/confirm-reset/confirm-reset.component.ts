import { Component } from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {TokenService} from "../../../service/token/token.service";
import {UserService} from "../../../service/user/user.service";
import {Router} from "@angular/router";
import {getErrorMessage} from "../../../service/utils/error.utils";
import {AccessToken} from "../../../model/auth.models";

@Component({
  selector: 'app-confirm-reset',
  templateUrl: './confirm-reset.component.html',
  styleUrls: ['./confirm-reset.component.css']
})
export class ConfirmResetComponent {
  public confirmationForm: FormGroup;
  public _errorMessage: string;
  public isSendAgainButtonDisabled: boolean;

  constructor(private formBuilder: FormBuilder,
              private tokenService: TokenService,
              private userService: UserService,
              private router: Router) {
    this._errorMessage = '';
    this.isSendAgainButtonDisabled = true;
    this.confirmationForm = formBuilder.group({
      codeField: this.formBuilder.group({
        code:  new FormControl('')
      })
    });
  }

  public get errorMessage(): string {
    return this._errorMessage;
  }

  public confirmUpdate() {
    const code = this.confirmationForm.value.codeField.code;
    this.userService.confirmResetUser(code)
      .subscribe({
        next : this.addToken.bind(this),
        error : this.handleError.bind(this)
      });
  }

  public handleError(error: { status: number; error: { message: string; }; }) {
    this._errorMessage = getErrorMessage(error);
  }

  private addToken(accessToken: AccessToken) {
    this.tokenService.removeToken();
    this.tokenService.setAccessToken(accessToken);
    this.router.navigate(['/auth/update']);
  }

  public disableButton() {
    this.isSendAgainButtonDisabled = true;
  }

  public enableButton() {
    this.isSendAgainButtonDisabled = false;
  }

}
