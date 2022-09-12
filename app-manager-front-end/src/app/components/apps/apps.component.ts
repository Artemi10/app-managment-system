import {AfterViewInit, Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AppService} from 'src/app/service/app/app.service';
import {TokenService} from 'src/app/service/token/token.service';
import {App, OrderType} from "../../model/app.model";
import {HttpResponse} from "@angular/common/http";
import {DropdownElement} from "../utils/dropdown/dropdown.component";

@Component({
  selector: 'app-apps',
  templateUrl: './apps.component.html',
  styleUrls: ['./apps.component.css']
})
export class AppsComponent implements OnInit, AfterViewInit {
  public appSortElements: DropdownElement[];
  public apps: App[];
  public _page: number;
  private appsAmount: number;
  private readonly pageSize: number;
  private sortField: string;
  public orderType: OrderType;
  public isSearchPanelShown: boolean;
  private searchParam: string;

  constructor(private appService: AppService,
              private router: Router,
              private tokenService: TokenService) {
    this.sortField = 'id';
    this.apps = [];
    this.pageSize = 3;
    this._page = 1;
    this.appsAmount = 0;
    this.orderType = OrderType.ASC;
    this.isSearchPanelShown = false;
    this.searchParam = '';
    this.appSortElements = [
      new DropdownElement('id', 'Id', true),
      new DropdownElement('name', 'Name', false),
      new DropdownElement('creationTime', 'Creation Time', false)
    ];
  }

  public get isDescending(): boolean {
    return this.orderType == OrderType.DESC;
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
      if (this.isSearchPanelShown) {
        this.appService.searchUserAppsByName(this._page, this.pageSize, this.searchParam)
          .subscribe({
            next : this.initApps.bind(this),
            error : this.logOut.bind(this)
          });
      }
      else {
        this.appService.getUserApps(this._page, this.pageSize, this.sortField, this.orderType)
          .subscribe({
            next : this.initApps.bind(this),
            error : this.logOut.bind(this)
          });
      }
    }
  }

  public get pageAmount(): number {
    if (this.appsAmount > 0 && this.appsAmount % this.pageSize == 0) {
      return Math.floor(this.appsAmount / this.pageSize);
    }
    else {
      return Math.ceil(this.appsAmount / this.pageSize);
    }
  }

  ngOnInit() {
    this.appService.getUserApps(this._page, this.pageSize, this.sortField, this.orderType)
      .subscribe({
        next : this.initApps.bind(this),
        error : this.logOut.bind(this)
      });
  }

  ngAfterViewInit() {
    const elems = document.querySelectorAll('.dropdown-trigger');
    // @ts-ignore
    const instances = M.Dropdown.init(elems, {});
  }

  private initApps(response: HttpResponse<App[]>) {
    this.apps = response.body ?? [];
    const amount = Number.parseInt(response.headers.get('X-Total-Count') ?? '0');
    if (Number.isNaN(amount)) {
      this.appsAmount = 0;
    }
    else  {
      this.appsAmount = amount;
    }
  }

  private logOut() {
    this.tokenService.removeToken();
    this.router.navigate(['/']);
  }

  public updateApps() {
    this.appService.getUserApps(this._page, this.pageSize, this.sortField, this.orderType)
      .subscribe({
        next : this.initApps.bind(this),
        error : this.logOut.bind(this)
      });
  }

  public getSortedApps(sortField: string) {
    this._page = 1;
    this.sortField = sortField;
    this.appService.getUserApps(this._page, this.pageSize, this.sortField, this.orderType)
      .subscribe({
        next : this.initApps.bind(this),
        error : this.logOut.bind(this)
      });
  }

  public changeDescending(){
    this.orderType = this.orderType == OrderType.DESC ? OrderType.ASC : OrderType.DESC;
    this.appService.getUserApps(this._page, this.pageSize, this.sortField, this.orderType)
      .subscribe({
        next : this.initApps.bind(this),
        error : this.logOut.bind(this)
      });
  }

  public createApp() {
    this.router.navigate(['/app/create']);
  }

  public closePanel() {
    this.isSearchPanelShown = false;
    this._page = 1;
    this.appService.getUserApps(this._page, this.pageSize, this.sortField, this.orderType)
      .subscribe({
        next : this.initApps.bind(this),
        error : this.logOut.bind(this)
      });
  }

  public openPanel() {
    this.isSearchPanelShown = true;
  }

  public searchApps(searchParam: string) {
    this._page = 1;
    this.searchParam = searchParam;
    this.appService.searchUserAppsByName(this._page, this.pageSize, searchParam)
      .subscribe({
        next : this.initApps.bind(this),
        error : this.logOut.bind(this)
      });
  }
}
