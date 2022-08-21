import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import {isAccessTokenRequired} from "../util.interceptor";

@Injectable()
export class TimeZoneInterceptor implements HttpInterceptor {

  constructor() {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const secondsOffset = new Date().getTimezoneOffset() * -60;
    const clonedRequest = request
      .clone({headers: request.headers.append('Time-Zone-Offset', secondsOffset.toString())});
    return next.handle(clonedRequest);
  }
}
