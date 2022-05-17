import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import {catchError, Observable, switchMap, throwError} from 'rxjs';
import { TokenService } from 'src/app/service/token/token.service';
import { AuthService } from 'src/app/service/auth/auth.service';
import {isAccessTokenRequired} from "../util.interceptor";
import {isAuthorizationError} from "../../service/utils/error.utils";

@Injectable()
export class RefreshTokenInterceptor implements HttpInterceptor {

  constructor(private tokenService: TokenService,
              private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.tokenService.getToken();
    if (isAccessTokenRequired(request.url) && this.tokenService.isExpired()) {
      return this.authService.refreshToken(token).pipe(
        switchMap(token => {
          this.tokenService.setToken(token);
          return next.handle(request);
        }),
        catchError((error => throwError(error)))
      );
    }
    else if (isAccessTokenRequired(request.url)) {
      return next.handle(request).pipe(
        catchError(error => {
          const token = this.tokenService.getToken();
          if (isAuthorizationError(error)) {
            return this.authService.refreshToken(token).pipe(
              switchMap(newToken => {
                this.tokenService.setToken(newToken);
                return next.handle(request);
              }),
              catchError((error => throwError(error))));
          }
          else return throwError(error);
        }));
    }
    else return next.handle(request);
  }
}
