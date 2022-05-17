import {HTTP_INTERCEPTORS} from "@angular/common/http";
import {AccessTokenInterceptor} from "./access-token/access-token.interceptor";
import { RefreshTokenInterceptor } from "./refresh-token/refresh-token.interceptor";

export const interceptorProviders = [
  {
    provide: HTTP_INTERCEPTORS,
    useClass: RefreshTokenInterceptor,
    multi: true
  },
  {
    provide: HTTP_INTERCEPTORS,
    useClass: AccessTokenInterceptor,
    multi: true
  }
];
