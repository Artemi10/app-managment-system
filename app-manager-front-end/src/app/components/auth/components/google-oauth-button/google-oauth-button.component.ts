import {Component} from '@angular/core';
import {environment} from "../../../../../environments/environment";

@Component({
  selector: 'app-google-oauth-button',
  templateUrl: './google-oauth-button.component.html',
  styleUrls: ['./google-oauth-button.component.css']
})
export class GoogleOauthButtonComponent {

  constructor() {}

  public logInWithGoogle() {
    window.location.href = `${environment.backEndURL}/auth/oauth2/google?redirect_uri=${environment.frontEndURL}&error_uri=${environment.frontEndURL}/auth/log-in`;
  }
}
