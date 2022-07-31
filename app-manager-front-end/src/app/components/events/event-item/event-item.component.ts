import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Event} from "../../../model/event.model";

@Component({
  selector: '[app-event-item]',
  templateUrl: './event-item.component.html',
  styleUrls: ['./event-item.component.css']
})
export class EventItemComponent  {
  @Input("event")
  public event: Event | undefined;
  public isActionButtonsHidden: boolean;
  @Output()
  public deleteEventItemEvent: EventEmitter<Event>;
  @Output()
  public updateEventItemEvent: EventEmitter<Event>;

  constructor() {
    this.isActionButtonsHidden = true;
    this.deleteEventItemEvent = new EventEmitter<Event>();
    this.updateEventItemEvent = new EventEmitter<Event>();
  }

  public showActionButtons() {
    this.isActionButtonsHidden = false;
  }

  public hideActionButtons() {
    this.isActionButtonsHidden = true;
  }

  public deleteEventItem() {
    this.deleteEventItemEvent.emit(this.event);
  }

  public updateEventItem() {
    this.updateEventItemEvent.emit(this.event);
  }

}
