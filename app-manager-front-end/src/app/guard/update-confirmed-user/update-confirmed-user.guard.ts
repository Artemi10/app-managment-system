import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import { Observable } from 'rxjs';
import {TokenService} from "../../service/token/token.service";
import {Authority} from "../../model/user.model";

@Injectable({
  providedIn: 'root'
})
export class UpdateConfirmedUserGuard implements CanActivate {
  constructor(private tokenService: TokenService,
              private router: Router) {
  }

  canActivate(): boolean {
    if (!this.tokenService.isExpired()
      && this.tokenService.hasAuthority(Authority.UPDATE_CONFIRMED)) {
      return true;
    }
    else {
      this.router.navigate(['/']);
      return false;
    }
  }
}
