import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {StatData} from 'src/app/model/stat.model';

@Injectable({
  providedIn: 'root'
})
export class StatService {
  private readonly api: string;

  constructor(private http: HttpClient) {
    this.api = '/app';
  }

  public createStats(appId: number, type: string, from: string = '', to: string = ''): Observable<StatData[]> {
    const param = {params: {type, from, to}};
    return this.http.get<StatData[]>(`${environment.url}${this.api}/${appId}/stat`, param);
  }
}
