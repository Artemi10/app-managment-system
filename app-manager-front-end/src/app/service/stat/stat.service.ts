import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {StatData, StatType} from 'src/app/model/stat.model';

@Injectable({
  providedIn: 'root'
})
export class StatService {
  private readonly api: string;

  constructor(private http: HttpClient) {
    this.api = '/app';
  }

  public createStats(appId: number, type: StatType, from: string | null = null, to: string | null = null): Observable<StatData[]> {
    const timeZone = new Date().getTimezoneOffset() / -60
    if (from != null && to != null && from.trim() != "" && to.trim() != "") {
      const body = {
        from : `${from} 00:00`,
        to : `${to} 00:00`,
        timeZone : `+0${timeZone}00`,
        type
      };
      return this.http.post<StatData[]>(`${environment.backEndURL}${this.api}/${appId}/stat`, body);
    }
    else {
      const body = {
        from : null,
        to : null,
        timeZone : `+0${timeZone}00`,
        type
      };
      return this.http.post<StatData[]>(`${environment.backEndURL}${this.api}/${appId}/stat`, body);
    }
  }
}
