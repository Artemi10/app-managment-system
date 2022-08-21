import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Event} from "../../model/event.model";
import {EventService} from "../../service/event/event.service";
import {TokenService} from "../../service/token/token.service";
import {HttpResponse} from "@angular/common/http";

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {
  public _page: number;
  private eventsAmount: number;
  private readonly pageSize: number;
  public events: Event[];

  constructor(private activatedRoute: ActivatedRoute,
              private eventService: EventService,
              private tokenService: TokenService,
              private router: Router) {
    this._page = 1;
    this.eventsAmount = 0;
    this.pageSize = 6;
    this.events = [];
  }

  ngOnInit(): void {
    this.updateEvents();
  }

  private updateEvents() {
    const appId = this.appId;
    if (appId != undefined) {
      this.eventService.getAppEvent(appId, this.page, this.pageSize)
        .subscribe({
          next : this.initEvents.bind(this),
          error : this.errorHandler.bind(this)
        });
    }
  }

  public get page(): number {
    return this._page;
  }

  public set page(value: number) {
    const appId = this.appId;
    if (value > 0 && appId !== undefined) {
      this._page = value;
      this.eventService.getAppEvent(appId, this.page, this.pageSize)
        .subscribe({
          next : this.initEvents.bind(this),
          error : this.errorHandler.bind(this)
        });
    }
  }

  public get pageAmount(): number {
    if (this.eventsAmount > 0 && this.eventsAmount % this.pageSize == 0) {
      return Math.floor(this.eventsAmount / this.pageSize);
    }
    else {
      return Math.ceil(this.eventsAmount / this.pageSize);
    }
  }

  private initEvents(response: HttpResponse<Event[]>) {
    this.events = response.body ?? [];
    const amount = Number.parseInt(response.headers.get('X-Total-Count') ?? '0');
    if (Number.isNaN(amount)) {
      this.eventsAmount = 0;
    }
    else  {
      this.eventsAmount = amount;
    }
  }

  private errorHandler() {
    this.tokenService.removeToken();
    this.router.navigate(['/']);
  }

  public get isEmpty(): boolean {
    return this.events.length === 0;
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

  public deleteEvent(event: Event) {
    const appId = this.appId;
    if (appId !== undefined) {
      this.eventService.deleteAppEvent(appId, event.id)
        .subscribe({
          next: this.updateEvents.bind(this),
          error: this.errorHandler.bind(this)
        });
    }
  }

  public updateEvent(event: Event) {
    this.router.navigate([`/app/${this.appId}/event/${event.id}/update`]);
  }

  public createEvent() {
    this.router.navigate([`/app/${this.appId}/event/create`]);
  }

  public openChart() {
    this.router.navigate([`/app/${this.appId}/stats`]);
  }

}
