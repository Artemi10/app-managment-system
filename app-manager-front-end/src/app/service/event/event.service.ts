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
    return this.http.post<Event>(`${environment.backEndURL}${this.api}/${appId}/event`, eventToAdd);
  }

  public getAppEvent(appId: number, page: number, pageSize: number): Observable<Event[]> {
    const param = {params: {page, pageSize}};
    return this.http.get<Event[]>(`${environment.backEndURL}${this.api}/${appId}/events`, param);
  }

  public getAppEventPageAmount(appId: number, pageSize: number): Observable<number> {
    const param = {params: {pageSize}};
    return this.http.get<number>(`${environment.backEndURL}${this.api}/${appId}/events/count`, param);
  }

  public deleteAppEvent(appId: number, eventId: number): Observable<void> {
    return this.http.delete<void>(`${environment.backEndURL}${this.api}/${appId}/event/${eventId}`);
  }

  public updateAppEvent(appId: number, eventId: number, eventToAdd: EventToAdd): Observable<Event> {
    return this.http.put<Event>(`${environment.backEndURL}${this.api}/${appId}/event/${eventId}`, eventToAdd);
  }
}
