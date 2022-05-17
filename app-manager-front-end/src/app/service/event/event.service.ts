import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {EventToAdd} from "../../model/event.model";
import {Event} from "../../model/event.model";

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private readonly api: string;

  constructor(private http: HttpClient) {
    this.api = '/app';
  }

  public addAppEvent(appId: number, eventToAdd: EventToAdd): Observable<Event> {
    return this.http.post<Event>(`${environment.url}${this.api}/${appId}/event`, eventToAdd);
  }
}
