import {environment} from "../../environments/environment";

export const isAccessTokenRequired = (url: string) => !isAccessTokenNotRequired(url);

export const isAccessTokenNotRequired = (url: string) =>
  url.startsWith(`${environment.backEndURL}/auth`)
  || (url.startsWith(`${environment.backEndURL}/user/reset`)
    && !url.startsWith(`${environment.backEndURL}/user/reset/confirm`)
    && !url.startsWith(`${environment.backEndURL}/user/reset/again`))
