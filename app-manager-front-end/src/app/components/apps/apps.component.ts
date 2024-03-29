import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AppService} from 'src/app/service/app/app.service';
import {TokenService} from 'src/app/service/token/token.service';
import {App, OrderType} from "../../model/app.model";
import {HttpResponse} from "@angular/common/http";
import {PageCriteria} from '../utils/pagination/page.criteria';
import {DropdownElement, SortCriteria} from "../utils/sorting/sort.criteria";

@Component({
  selector: 'app-apps',
  templateUrl: './apps.component.html',
  styleUrls: ['./apps.component.css']
})
export class AppsComponent implements OnInit {
  public _pageCriteria: PageCriteria;
  public _sortCriteria: SortCriteria;
  public apps: App[];
  public appsAmount: number;
  public isSearchPanelShown: boolean;
  private _searchParam: string;

  constructor(private appService: AppService,
              private router: Router,
              private tokenService: TokenService) {
    this.apps = [];
    this.appsAmount = 0;
    this._pageCriteria = new PageCriteria(1, 3);
    this._sortCriteria = new SortCriteria(
      'id',
      OrderType.ASC,
      [
        new DropdownElement('id', 'Id', true),
        new DropdownElement('name', 'Name', false),
        new DropdownElement('creationTime', 'Creation Time', false)
      ]);
    this.isSearchPanelShown = false;
    this._searchParam = '';
  }

  ngOnInit(): void {
    this.retrieveApps();
  }

  public get searchParam(): string {
    return this._searchParam;
  }

  public set searchParam(value: string){
    this._searchParam = value;
    this.pageCriteria.page = 1;
    this.retrieveAppsByName();
  }

  public get isEmpty(): boolean {
    return this.apps.length == 0;
  }

  public get pageCriteria(): PageCriteria {
    return this._pageCriteria;
  }

  public set pageCriteria(value: PageCriteria) {
    this._pageCriteria = value;
    if (this.isSearchPanelShown) {
      this.retrieveAppsByName();
    }
    else {
      this.retrieveApps();
    }
  }

  public get sortCriteria(): SortCriteria {
    return this._sortCriteria;
  }

  public set sortCriteria(value: SortCriteria) {
    this._sortCriteria = value;
    this.retrieveApps();
  }

  public getSortedApps() {
    this.pageCriteria.page = 1;
    this.retrieveApps();
  }

  public closePanel() {
    this.isSearchPanelShown = false;
    this.pageCriteria.page = 1;
    this.retrieveApps();
  }

  public openPanel() {
    this.isSearchPanelShown = true;
  }

  public retrieveApps() {
    this.appService.getUserApps(this.pageCriteria, this.sortCriteria)
      .subscribe({
        next : this.initApps.bind(this),
        error : this.logOut.bind(this)
      });
  }

  private retrieveAppsByName() {
    this.appService.searchUserAppsByName(this.pageCriteria, this.searchParam)
      .subscribe({
        next : this.initApps.bind(this),
        error : this.logOut.bind(this)
      });
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

}
