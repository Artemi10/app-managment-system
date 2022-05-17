import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import { TokenService } from 'src/app/service/token/token.service';
import {Authority} from "../../model/user.model";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements AfterViewInit {
  @ViewChild('sidenav')
  public sidenav: ElementRef<HTMLUListElement> | undefined;

  constructor(private tokenService: TokenService) { }

  ngAfterViewInit(): void {
    if (this.sidenav !== undefined) {
      const element = this.sidenav.nativeElement
      const options = {
        edge : 'left',
        inDuration : 300,
        outDuration : 300,
        preventScrolling : true
      }
      // @ts-ignore
      const instances = M.Sidenav.init(element, options)
    }
  }

  public get isAuthenticated(): boolean {
    return !this.tokenService.isExpired()
      && this.tokenService.hasAuthority(Authority.ACTIVE)
  }
}
