import { Component } from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {TokenService} from "../../../../service/token/token.service";
import {ActivatedRoute, Router} from "@angular/router";
import {getErrorMessage} from "../../../../service/utils/error.utils";
import {EventToAdd, Event} from "../../../../model/event.model";
import {EventService} from "../../../../service/event/event.service";

@Component({
  selector: 'app-event-form',
  templateUrl: './event-form.component.html',
  styleUrls: ['./event-form.component.css']
})
export class EventFormComponent {
  public createEventForm: FormGroup;
  private _errorMessage: string;

  constructor(private formBuilder: FormBuilder,
              private eventService: EventService,
              private tokenService: TokenService,
              private activatedRoute: ActivatedRoute,
              private router: Router) {
    this._errorMessage = '';
    this.createEventForm = formBuilder.group({
      eventNameField: this.formBuilder.group({
        eventName:  new FormControl('')
      }),
      extraInfField: this.formBuilder.group({
        extraInf:  new FormControl('')
      })
    });
  }

  private get appId(): number | undefined {
    const appIdStr = this.activatedRoute.snapshot.paramMap.get('id');
    if (appIdStr !== null) {
      return parseInt(appIdStr);
    }
    else {
      return undefined;
    }
  }

  public get errorMessage(): string {
    return this._errorMessage;
  }

  private get requestBody(): EventToAdd {
    const formValue = this.createEventForm.value;
    return {
      name: formValue.eventNameField.eventName,
      extraInformation: formValue.extraInfField.extraInf
    }
  }

  public createEvent() {
    const appId = this.appId;
    if (appId !== undefined) {
      this.eventService.addAppEvent(appId, this.requestBody)
        .subscribe({
          next : this.handleCreateEvent.bind(this),
          error : this.handleError.bind(this)
        });
    }
  }

  private handleCreateEvent(event: Event) {
    const appId = this.appId;
    if (appId !== undefined) {
      this.router.navigate([`/app/${appId}/chart`]);
    }
  }

  private handleError(error: { status: number; error: { message: string; }; }) {
    this._errorMessage = getErrorMessage(error);
  }

}
