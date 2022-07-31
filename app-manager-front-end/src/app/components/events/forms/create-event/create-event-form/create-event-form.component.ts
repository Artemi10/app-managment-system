import { Component } from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {TokenService} from "../../../../../service/token/token.service";
import {ActivatedRoute} from "@angular/router";
import {getErrorMessage} from "../../../../../service/utils/error.utils";
import {EventToAdd, Event} from "../../../../../model/event.model";
import {EventService} from "../../../../../service/event/event.service";
import { Location } from '@angular/common';

@Component({
  selector: 'app-create-event-form',
  templateUrl: './create-event-form.component.html',
  styleUrls: ['./create-event-form.component.css']
})
export class CreateEventFormComponent {
  public createEventForm: FormGroup;
  private _errorMessage: string;

  constructor(private formBuilder: FormBuilder,
              private eventService: EventService,
              private tokenService: TokenService,
              private activatedRoute: ActivatedRoute,
              private location: Location) {
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
    this.location.back();
  }

  private handleError(error: { status: number; error: { message: string; }; }) {
    this._errorMessage = getErrorMessage(error);
  }

}
