import { Injectable } from '@angular/core';
import jwt_decode from 'jwt-decode';
import {Authority} from "../../model/user.model";
import {AccessToken, Token} from "../../model/auth.models";

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private readonly accessTokenHeader: string;
  private readonly refreshTokenHeader: string;

  constructor() {
    this.accessTokenHeader = 'accessToken';
    this.refreshTokenHeader = 'refreshToken';
  }

  public setToken(token: Token) {
    localStorage.setItem(this.accessTokenHeader, token.accessToken);
    localStorage.setItem(this.refreshTokenHeader, token.refreshToken);
  }

  public setAccessToken(token: AccessToken) {
    localStorage.setItem(this.accessTokenHeader, token.accessToken);
  }

  public removeToken() {
    localStorage.removeItem(this.accessTokenHeader);
    localStorage.removeItem(this.refreshTokenHeader);
  }

  public getToken(): Token {
    return {
      accessToken: this.getAccessToken(),
      refreshToken: this.getRefreshToken()
    }
  }

  public getAccessToken(): string {
    const accessToken = localStorage.getItem(this.accessTokenHeader);
    if (accessToken !== null) {
      return accessToken;
    }
    else return '';
  }

  public getRefreshToken(): string {
    const refreshToken = localStorage.getItem(this.refreshTokenHeader);
    if (refreshToken !== null) {
      return refreshToken;
    }
    else return '';
  }

  public isRefreshTokenPresent(): boolean {
    return localStorage.getItem(this.refreshTokenHeader) !== null;
  }

  public getEmail(): string{
    const decodedToken = jwt_decode(this.getAccessToken());
    // @ts-ignore
    return decodedToken.sub;
  }

  public hasAuthority(authority: Authority): boolean {
    const decodedToken = jwt_decode(this.getAccessToken());
    if (decodedToken === undefined) {
      return false;
    }
    // @ts-ignore
    return decodedToken.authority === authority
  }

  public isExpired(){
    try {
      const expirationTime = this.getExpirationTime();
      return expirationTime.valueOf() < new Date().valueOf();
    }
    catch (e) {
      return true;
    }
  }

  private getExpirationTime(): Date{
    const token = this.getAccessToken();
    const decodedToken = jwt_decode(token);
    // @ts-ignore
    if (decodedToken.exp === undefined) {
      throw new Error ('No expiration date in JWT');
    }
    const date = new Date(0);
    // @ts-ignore
    date.setUTCSeconds(decodedToken.exp);
    return date;
  }
}
