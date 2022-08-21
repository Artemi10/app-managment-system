import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
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
    return this.http.post<Event>(`${environment.backEndURL}${this.api}/${appId}/event`, eventToAdd);
  }

  public getAppEvent(appId: number, page: number, pageSize: number): Observable<HttpResponse<Event[]>> {
    return this.http.get<Event[]>(
      `${environment.backEndURL}${this.api}/${appId}/events`,
      { observe: 'response', params: {page, pageSize} });
  }

  public deleteAppEvent(appId: number, eventId: number): Observable<void> {
    return this.http.delete<void>(`${environment.backEndURL}${this.api}/${appId}/event/${eventId}`);
  }

  public updateAppEvent(appId: number, eventId: number, eventToAdd: EventToAdd): Observable<Event> {
    return this.http.put<Event>(`${environment.backEndURL}${this.api}/${appId}/event/${eventId}`, eventToAdd);
  }
}
