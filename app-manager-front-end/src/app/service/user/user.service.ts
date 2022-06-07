import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {UserToUpdate} from "../../model/user.model";
import {AccessToken} from "../../model/auth.models";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly api: string;

  constructor(private http: HttpClient) {
    this.api = '/user';
  }

  public updateUser(user: UserToUpdate): Observable<void> {
    return this.http.patch<void>(`${environment.backEndURL}${this.api}`, user);
  }

  public resetUser(email: string): Observable<AccessToken> {
    const request = { email };
    return this.http.post<AccessToken>(`${environment.backEndURL}${this.api}/reset`, request);
  }

  public resetUserAgain(): Observable<void> {
    return this.http.post<void>(`${environment.backEndURL}${this.api}/reset/again`, {});
  }

  public confirmResetUser(resetToken: string): Observable<AccessToken> {
    const request = { resetToken };
    return this.http.post<AccessToken>(`${environment.backEndURL}${this.api}/reset/confirm`, request);
  }
}
