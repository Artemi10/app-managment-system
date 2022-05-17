import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { TokenService } from 'src/app/service/token/token.service';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent {
  @Input()
  public isAuthenticated: boolean;

  constructor(private tokenService: TokenService,
              private router: Router) {
    this.isAuthenticated = false;
  }

  public logOut() {
    this.tokenService.removeToken();
    this.router.navigate(['/'])
  }
}
