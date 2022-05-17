import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import {isAccessTokenRequired} from "../util.interceptor";
import {TokenService} from "../../service/token/token.service";

@Injectable()
export class AccessTokenInterceptor implements HttpInterceptor {

  constructor(private tokenService: TokenService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (isAccessTokenRequired(request.url)){
      const token = this.tokenService.getAccessToken();
      if (token !== ''){
        const clonedRequest = request
          .clone({headers: request.headers.append('Authorization', `Bearer_${token}`)});
        return next.handle(clonedRequest);
      }
    }
    return next.handle(request);
  }
}
