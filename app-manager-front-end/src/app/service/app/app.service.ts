import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import { Observable } from 'rxjs';
import {App, AppToCreate} from "../../model/app.model";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class AppService {
  private readonly api: string;

  constructor(private http: HttpClient) {
    this.api = '/apps';
  }

  public getUserApps(page: number, pageSize: number, value: string, descending: boolean): Observable<App[]> {
    const param = {params: {page, pageSize, value, descending}};
    return this.http.get<App[]>(`${environment.url}${this.api}`, param);
  }

  public getPageAmount(pageSize: number): Observable<number> {
    const param = {params: {pageSize}};
    return this.http.get<number>(`${environment.url}${this.api}/count`, param);
  }

  public createUserApp(appToCreate: AppToCreate): Observable<App> {
    return this.http.post<App>(`${environment.url}${this.api}`, appToCreate);
  }

  public updateUserApp(id: number, appToUpdate: AppToCreate): Observable<App> {
    return this.http.put<App>(`${environment.url}${this.api}/${id}`, appToUpdate);
  }

  public deleteUserApp(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.url}${this.api}/${id}`);
  }
}
