import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
import { Observable } from 'rxjs';
import {App, AppToCreate} from "../../model/app.model";
import {environment} from "../../../environments/environment";
import { PageCriteria } from 'src/app/components/utils/pagination/page.criteria';
import {SortCriteria} from "../../components/utils/sorting/sort.criteria";

@Injectable({
  providedIn: 'root'
})
export class AppService {
  private readonly api: string;

  constructor(private http: HttpClient) {
    this.api = '/apps';
  }

  public getUserApps(pageCriteria: PageCriteria, sortCriteria: SortCriteria): Observable<HttpResponse<App[]>> {
    const params = {
      page : pageCriteria.page,
      pageSize : pageCriteria.pageSize,
      sortValue : sortCriteria.sortField,
      orderType : sortCriteria.orderType
    };
    return this.http.get<App[]>(
      `${environment.backEndURL}${this.api}`,
      {observe: 'response', params: params});
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

  public searchUserAppsByName(pageCriteria: PageCriteria, searchParam: string): Observable<HttpResponse<App[]>> {
    const params = {
      page : pageCriteria.page,
      pageSize : pageCriteria.pageSize
    };
    return this.http.get<App[]>(
      `${environment.backEndURL}${this.api}/name/${searchParam}`,
      {observe: 'response', params: params});
  }
}
