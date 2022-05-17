import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {LogInModel, SignUpModel, Token} from "../../model/auth.models";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly api: string;

  constructor(private http: HttpClient) {
    this.api = '/auth';
  }

  public logIn(logInModel: LogInModel): Observable<Token> {
    return this.http.post<Token>(`${environment.url}${this.api}/log-in`, logInModel);
  }

  public signUp(signUpModel: SignUpModel): Observable<Token> {
    return this.http.post<Token>(`${environment.url}${this.api}/sign-up`, signUpModel);
  }

  public refreshToken(token: Token): Observable<Token> {
    return this.http.post<Token>(`${environment.url}${this.api}/refresh`, token);
  }
}
