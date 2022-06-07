import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {TokenService} from "../../service/token/token.service";
import {Authority} from "../../model/user.model";
import {Token} from "../../model/auth.models";
import {UserAuthService} from "../../service/auth/user-auth.service";

@Injectable({
  providedIn: 'root'
})
export class UnAuthUserGuard implements CanActivate {

  constructor(private tokenService: TokenService,
              private router: Router,
              private authService: UserAuthService) {

  }

  canActivate(): boolean {
    if ((this.tokenService.isExpired() && !this.tokenService.isRefreshTokenPresent())
      || !this.tokenService.hasAuthority(Authority.ACTIVE)) {
      return true;
    }
    else if (this.tokenService.isExpired()
      && this.tokenService.hasAuthority(Authority.ACTIVE)
      && this.tokenService.isRefreshTokenPresent()) {
      this.sendRefreshToken();
      return false;
    }
    else {
      this.router.navigate(['/']);
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
    this.router.navigate(['/']);
  }

  private handleError() {
    this.tokenService.removeToken();
    this.router.navigate(['/auth/log-in']);
  }

}
