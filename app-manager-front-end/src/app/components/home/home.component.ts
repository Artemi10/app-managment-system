import {Component, OnInit} from '@angular/core';
import { TokenService } from 'src/app/service/token/token.service';
import {Authority} from "../../model/user.model";
import {ActivatedRoute} from "@angular/router";
import {UserAuthService} from "../../service/auth/user-auth.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor(private tokenService: TokenService,
              private route: ActivatedRoute,
              private userAuthService: UserAuthService) {
  }

  public get isAuthenticated(): boolean {
    return !this.tokenService.isExpired()
      && this.tokenService.hasAuthority(Authority.ACTIVE)
  }

  ngOnInit(): void {
    this.route.queryParams
      .subscribe(params => {
        // @ts-ignore
        const token = params.token;
        if (token !== undefined) {
          this.userAuthService.logInViaEnterToken(token)
            .subscribe(tokens => this.tokenService.setToken(tokens));
        }
      });
  }
}
