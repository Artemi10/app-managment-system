import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
import { Observable } from 'rxjs';
import {App, AppToCreate, OrderType} from "../../model/app.model";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class AppService {
  private readonly api: string;

  constructor(private http: HttpClient) {
    this.api = '/apps';
  }

  public getUserApps(page: number, pageSize: number, sortValue: string, orderType: OrderType): Observable<HttpResponse<App[]>> {
    return this.http.get<App[]>(
      `${environment.backEndURL}${this.api}`,
      {observe: 'response', params: {page, pageSize, sortValue, orderType}});
  }

  public createUserApp(appToCreate: AppToCreate): Observable<App> {
    return this.http.post<App>(`${environment.backEndURL}${this.api}`, appToCreate);
  }

  public updateUserApp(id: number, appToUpdate: AppToCreate): Observable<App> {
    return this.http.put<App>(`${environment.backEndURL}${this.api}/${id}`, appToUpdate);
  }

  public deleteUserApp(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.backEndURL}${this.api}/${id}`);
  }

  public searchUserAppsByName(page: number, pageSize: number, searchParam: string): Observable<HttpResponse<App[]>> {
    return this.http.get<App[]>(
      `${environment.backEndURL}${this.api}/name/${searchParam}`,
      {observe: 'response', params: {page, pageSize}});
  }
}
