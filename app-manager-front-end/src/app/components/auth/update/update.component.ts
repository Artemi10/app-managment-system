import { Component } from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import { Router } from '@angular/router';
import { TokenService } from 'src/app/service/token/token.service';
import { UserService } from 'src/app/service/user/user.service';
import {UserToUpdate} from "../../../model/user.model";
import {getErrorMessage} from "../../../service/utils/error.utils";

@Component({
  selector: 'app-update',
  templateUrl: './update.component.html',
  styleUrls: ['./update.component.css']
})
export class UpdateComponent {
  public updateForm: FormGroup;
  public _errorMessage: string;

  constructor(private formBuilder: FormBuilder,
              private userService: UserService,
              private tokenService: TokenService,
              private router: Router) {
    this._errorMessage = '';
    this.updateForm = formBuilder.group({
      passwordField: formBuilder.group({
        password: new FormControl('', new FormControl(''))
      }),
      rePasswordField: formBuilder.group({
        rePassword: new FormControl('', new FormControl(''))
      }),
    });
  }

  public get errorMessage(): string {
    return this._errorMessage;
  }

  private get requestBody(): UserToUpdate {
    const formValue = this.updateForm.value;
    return {
      newPassword: formValue.passwordField.password,
      rePassword: formValue.rePasswordField.rePassword
    }
  }

  public update() {
    this.userService.updateUser(this.requestBody)
      .subscribe({
        next: this.handleUpdate.bind(this),
        error: this.handleError.bind(this)
      });
  }

  private handleUpdate(){
    this.tokenService.removeToken();
    this.router.navigate(['/auth/log-in']);
  }

  private handleError(error: { status: number; error: { message: string; }; }) {
    this._errorMessage = getErrorMessage(error);
  }

}
