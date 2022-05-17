import { Component } from '@angular/core';
import { TokenService } from 'src/app/service/token/token.service';
import {Authority} from "../../model/user.model";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {

  constructor(private tokenService: TokenService) { }

  public get isAuthenticated(): boolean {
    return !this.tokenService.isExpired()
      && this.tokenService.hasAuthority(Authority.ACTIVE)
  }

}
