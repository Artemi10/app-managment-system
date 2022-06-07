import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {ActivatedRoute, Router} from '@angular/router';
import { UserAuthService } from 'src/app/service/auth/user-auth.service';
import { TokenService } from 'src/app/service/token/token.service';
import { getErrorMessage } from 'src/app/service/utils/error.utils';
import {LogInModel, Token} from "../../../model/auth.models";


@Component({
  selector: 'app-log-in',
  templateUrl: './log-in.component.html',
  styleUrls: ['./log-in.component.css']
})
export class LogInComponent implements OnInit {
  public logInForm: FormGroup;
  private _errorMessage: string;

  constructor(private route: ActivatedRoute,
              private formBuilder: FormBuilder,
              private userAuthService: UserAuthService,
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

  ngOnInit(): void {
    this.route.queryParams
      .subscribe(params => {
        // @ts-ignore
        const errorMessage = params.errorMessage;
        if (errorMessage !== undefined) {
          this._errorMessage = errorMessage;
        }
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
    this.userAuthService.logIn(this.requestBody)
      .subscribe({
        next : this.handleLogIn.bind(this),
        error : this.handleError.bind(this)
      });
  }

  private handleLogIn(token: Token) {
    this.tokenService.setToken(token);
    this.router.navigate(['/']);
  }

  public handleError(error: { status: number; error: { message: string; }; }) {
    this._errorMessage = getErrorMessage(error);
  }

}
