import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {TokenService} from "../../../../../service/token/token.service";
import {ActivatedRoute, Router} from "@angular/router";
import {App, AppToCreate} from "../../../../../model/app.model";
import {AppService} from "../../../../../service/app/app.service";
import {getErrorMessage} from "../../../../../service/utils/error.utils";

@Component({
  selector: 'app-update-app-form',
  templateUrl: './update-app-form.component.html',
  styleUrls: ['./update-app-form.component.css']
})
export class UpdateAppFormComponent {
  public updateForm: FormGroup;
  private _errorMessage: string;

  constructor(private formBuilder: FormBuilder,
              private appService: AppService,
              private tokenService: TokenService,
              private activatedRoute: ActivatedRoute,
              private router: Router) {
    this._errorMessage = '';
    this.updateForm = formBuilder.group({
      nameField: this.formBuilder.group({
        name:  new FormControl('')
      })
    });
  }

  public get errorMessage(): string {
    return this._errorMessage;
  }

  private get requestBody(): AppToCreate {
    const formValue = this.updateForm.value;
    return {
      name: formValue.nameField.name
    }
  }

  public updateApp() {
    const appIdStr = this.activatedRoute.snapshot.paramMap.get('id');
    if (appIdStr != null) {
      const appId = parseInt(appIdStr);
      this.appService.updateUserApp(appId, this.requestBody)
        .subscribe({
          next: this.handleUpdate.bind(this),
          error: this.handleError.bind(this)
        });
    }
  }

  private handleUpdate() {
    this.router.navigate(['/apps']);
  }

  private handleError(error: { status: number; error: { message: string; }; }) {
    this._errorMessage = getErrorMessage(error);
  }
}
