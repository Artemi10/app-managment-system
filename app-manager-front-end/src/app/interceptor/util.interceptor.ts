import {environment} from "../../environments/environment";

export const isAccessTokenRequired = (url: string) => !isAccessTokenNotRequired(url);

export const isAccessTokenNotRequired = (url: string) =>
  url.startsWith(`${environment.url}/auth`)
  || (url.startsWith(`${environment.url}/user/reset`)
    && !url.startsWith(`${environment.url}/user/reset/confirm`)
    && !url.startsWith(`${environment.url}/user/reset/again`))
