import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import {TokenService} from "../../service/token/token.service";
import {Authority} from "../../model/user.model";
import {AuthService} from "../../service/auth/auth.service";
import {Token} from "../../model/auth.models";

@Injectable({
  providedIn: 'root'
})
export class ActiveUserGuard implements CanActivate {

  constructor(private tokenService: TokenService,
              private authService: AuthService,
              private router: Router) {
  }

  canActivate(): boolean {
    if (!this.tokenService.isExpired()
      && this.tokenService.hasAuthority(Authority.ACTIVE)) {
      return true;
    }
    else if (this.tokenService.isExpired()
      && this.tokenService.hasAuthority(Authority.ACTIVE)
      && this.tokenService.isRefreshTokenPresent()) {
      this.sendRefreshToken();
      return true;
    }
    else {
      this.router.navigate(['/auth/log-in']);
      return false;
    }
  }

  private sendRefreshToken() {
    this.authService.refreshToken(this.tokenService.getToken())
      .subscribe({
        next : this.setToken.bind(this),
        error : this.handleError.bind(this)
      });
  }

  private setToken(token: Token) {
    this.tokenService.setToken(token);
  }

  private handleError() {
    this.tokenService.removeToken();
    this.router.navigate(['/auth/log-in']);
  }
}
