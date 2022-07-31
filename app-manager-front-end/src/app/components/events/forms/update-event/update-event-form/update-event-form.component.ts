import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {EventService} from "../../../../../service/event/event.service";
import {TokenService} from "../../../../../service/token/token.service";
import {ActivatedRoute} from "@angular/router";
import {Location} from "@angular/common";
import {Event, EventToAdd} from "../../../../../model/event.model";
import {getErrorMessage} from "../../../../../service/utils/error.utils";

@Component({
  selector: 'app-update-event-form',
  templateUrl: './update-event-form.component.html',
  styleUrls: ['./update-event-form.component.css']
})
export class UpdateEventFormComponent {
  public updateEventForm: FormGroup;
  private _errorMessage: string;

  constructor(private formBuilder: FormBuilder,
              private eventService: EventService,
              private tokenService: TokenService,
              private activatedRoute: ActivatedRoute,
              private location: Location) {
    this._errorMessage = '';
    this.updateEventForm = formBuilder.group({
      eventNameField: this.formBuilder.group({
        eventName:  new FormControl('')
      }),
      extraInfField: this.formBuilder.group({
        extraInf:  new FormControl('')
      })
    });
  }

  private get eventId(): number | undefined {
    const eventIdStr = this.activatedRoute.snapshot.paramMap.get('eventId');
    if (eventIdStr !== null) {
      return parseInt(eventIdStr);
    }
    else {
      return undefined;
    }
  }

  private get appId(): number | undefined {
    const appIdStr = this.activatedRoute.snapshot.paramMap.get('appId');
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
    const formValue = this.updateEventForm.value;
    return {
      name: formValue.eventNameField.eventName,
      extraInformation: formValue.extraInfField.extraInf
    }
  }

  public updateEvent() {
    const appId = this.appId;
    const eventId = this.eventId;
    if (appId !== undefined && eventId !== undefined) {
      this.eventService.updateAppEvent(appId, eventId, this.requestBody)
        .subscribe({
          next : this.handleUpdateEvent.bind(this),
          error : this.handleError.bind(this)
        });
    }
  }

  private handleUpdateEvent(event: Event) {
    this.location.back();
  }

  private handleError(error: { status: number; error: { message: string; }; }) {
    this._errorMessage = getErrorMessage(error);
  }
}
