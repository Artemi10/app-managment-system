import {Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Event} from "../../model/event.model";
import {EventService} from "../../service/event/event.service";
import {TokenService} from "../../service/token/token.service";
import {HttpResponse} from "@angular/common/http";
import {OrderType} from "../../model/app.model";
import {DropdownElement, SortCriteria} from "../utils/sorting/sort.criteria";
import {PageCriteria} from "../utils/pagination/page.criteria";

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {
  private _pageCriteria: PageCriteria;
  private _sortCriteria: SortCriteria;
  public eventsAmount: number;
  public events: Event[];
  public isSearchPanelShown: boolean;
  private _searchParam: string;

  constructor(
    private activatedRoute: ActivatedRoute,
    private eventService: EventService,
    private tokenService: TokenService,
    private router: Router
  ) {
    this.eventsAmount = 0;
    this.events = [];
    this._pageCriteria = new PageCriteria(1, 6);
    this._sortCriteria = new SortCriteria(
      'id',
      OrderType.ASC,
      [
        new DropdownElement('id', 'Id', true),
        new DropdownElement('name', 'Name', false),
        new DropdownElement('creationTime', 'Creation Time', false),
        new DropdownElement('extraInformation', 'Extra information', false)
      ]);
    this.isSearchPanelShown = false;
    this._searchParam = '';
  }

  ngOnInit(): void {
    this.retrieveEvents();
  }

  public get searchParam(): string {
    return this._searchParam;
  }

  public set searchParam(value: string){
    this._searchParam = value;
    this.pageCriteria.page = 1;
    this.retrieveEventsByName();
  }

  public get pageCriteria(): PageCriteria {
    return this._pageCriteria;
  }

  public set pageCriteria(value: PageCriteria) {
    this._pageCriteria = value;
    if (this.isSearchPanelShown) {
      this.retrieveEventsByName();
    }
    else {
      this.retrieveEvents();
    }
  }

  public get sortCriteria(): SortCriteria {
    return this._sortCriteria;
  }

  public set sortCriteria(value: SortCriteria) {
    this._sortCriteria = value;
    this.retrieveEvents();
  }

  public get isEmpty(): boolean {
    return this.events.length === 0;
  }

  public get appId(): number | undefined {
    const appIdStr = this.activatedRoute.snapshot.paramMap.get('id');
    if (appIdStr !== null) {
      return parseInt(appIdStr);
    }
    else {
      return undefined;
    }
  }

  public closePanel() {
    this.isSearchPanelShown = false;
    this.pageCriteria.page = 1;
    this.retrieveEvents();
  }

  public openPanel() {
    this.isSearchPanelShown = true;
  }

  public getSortedEvents() {
    this.pageCriteria.page = 1;
    this.retrieveEvents();
  }

  public updateEvent(event: Event) {
    this.router.navigate([`/app/${this.appId}/event/${event.id}/update`]);
  }

  public deleteEvent(event: Event) {
    const appId = this.appId;
    if (appId !== undefined) {
      this.eventService.deleteAppEvent(appId, event.id)
        .subscribe({
          next: this.retrieveEvents.bind(this),
          error: this.errorHandler.bind(this)
        });
    }
  }

  public retrieveEvents() {
    if (this.appId !== undefined) {
      this.eventService.getAppEvent(this.appId, this.pageCriteria, this.sortCriteria)
        .subscribe({
          next : this.initEvents.bind(this),
          error : this.errorHandler.bind(this)
        });
    }
  }

  public retrieveEventsByName() {
    if (this.appId !== undefined) {
      this.eventService.searchAppEventsByName(this.appId, this.searchParam, this.pageCriteria)
        .subscribe({
          next : this.initEvents.bind(this),
          error : this.errorHandler.bind(this)
        });
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

}
