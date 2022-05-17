import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AppService} from 'src/app/service/app/app.service';
import {TokenService} from 'src/app/service/token/token.service';
import {App} from "../../model/app.model";

@Component({
  selector: 'app-apps',
  templateUrl: './apps.component.html',
  styleUrls: ['./apps.component.css']
})
export class AppsComponent implements OnInit {
  public apps: App[];
  public _page: number;
  public pageAmount: number;
  private readonly pageSize: number;

  constructor(private appService: AppService,
              private router: Router,
              private tokenService: TokenService) {
    this.apps = [];
    this.pageSize = 3;
    this._page = 1;
    this.pageAmount = 1;
  }

  public get isEmpty(): boolean {
    return this.apps.length == 0;
  }

  public get page(): number {
    return this._page;
  }

  public set page(value: number) {
    if (value > 0) {
      this._page = value;
      this.appService.getUserApps(this._page, this.pageSize)
        .subscribe({
          next : this.initApps.bind(this),
          error : this.logOut.bind(this)
        });
    }
  }

  ngOnInit() {
    this.appService.getUserApps(this._page, this.pageSize)
      .subscribe({
        next : this.initApps.bind(this),
        error : this.logOut.bind(this)
      });
    this.appService.getPageAmount(this.pageSize)
      .subscribe({
        next : this.initPageAmount.bind(this),
        error : this.logOut.bind(this)
      });
  }

  private initApps(apps: App[]) {
    this.apps = apps;
  }

  private initPageAmount(pageAmount: number) {
    this.pageAmount = pageAmount;
  }

  private logOut() {
    this.tokenService.removeToken();
    this.router.navigate(['/']);
  }

  public updateApps() {
    this.appService.getUserApps(this._page, this.pageSize)
      .subscribe({
        next : this.initApps.bind(this),
        error : this.logOut.bind(this)
      });
    this.appService.getPageAmount(this.pageSize)
      .subscribe({
        next : this.initPageAmount.bind(this),
        error : this.logOut.bind(this)
      });
  }

  public createApp() {
    this.router.navigate(['/app/create']);
  }
}
