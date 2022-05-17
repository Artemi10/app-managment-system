import { Component } from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {AppService} from "../../../../../service/app/app.service";
import {TokenService} from "../../../../../service/token/token.service";
import {ActivatedRoute, Router} from "@angular/router";
import {AppToCreate} from "../../../../../model/app.model";
import {getErrorMessage} from "../../../../../service/utils/error.utils";

@Component({
  selector: 'app-create-app-form',
  templateUrl: './create-app-form.component.html',
  styleUrls: ['./create-app-form.component.css']
})
export class CreateAppFormComponent {
  public createForm: FormGroup;
  private _errorMessage: string;

  constructor(private formBuilder: FormBuilder,
              private appService: AppService,
              private tokenService: TokenService,
              private activatedRoute: ActivatedRoute,
              private router: Router) {
    this._errorMessage = '';
    this.createForm = formBuilder.group({
      nameField: this.formBuilder.group({
        name:  new FormControl('')
      })
    });
  }

  public get errorMessage(): string {
    return this._errorMessage;
  }

  private get requestBody(): AppToCreate {
    const formValue = this.createForm.value;
    return {
      name: formValue.nameField.name
    }
  }

  public createApp() {
    this.appService.createUserApp(this.requestBody)
      .subscribe({
        next: this.handleUpdate.bind(this),
        error: this.handleError.bind(this)
      });
  }

  private handleUpdate() {
    this.router.navigate(['/apps']);
  }

  private handleError(error: { status: number; error: { message: string; }; }) {
    this._errorMessage = getErrorMessage(error);
  }
}
