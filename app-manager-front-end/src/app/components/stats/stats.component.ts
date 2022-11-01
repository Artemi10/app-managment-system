import {Component, OnInit} from '@angular/core';
import {Stat, StatData, StatType} from "../../model/stat.model";
import {StatService} from "../../service/stat/stat.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-stats',
  templateUrl: './stats.component.html',
  styleUrls: ['./stats.component.css']
})
export class StatsComponent implements OnInit {
  public isStatFormOpened: boolean;
  public stats: StatData[];

  constructor(private statService: StatService,
              private router: Router,
              private activatedRoute: ActivatedRoute) {
    this.isStatFormOpened = false;
    this.stats = [];
  }

  public get appId(): number | undefined {
    const appIdStr = this.activatedRoute.snapshot.paramMap.get('id');
    if (appIdStr !== null) {
      return parseInt(appIdStr);
    }
    else {
      return undefined;
    }
  }

  ngOnInit(): void {
    if (this.appId != undefined) {
      this.statService.createStats(this.appId, StatType.MONTH)
        .subscribe({
          next: this.initStats.bind(this),
          error: this.handleError.bind(this)
        });
    }
  }

  private initStats(stats: StatData[]) {
    this.stats = stats;
  }

  private handleError(){
    this.router.navigate(['/']);
  }

  public submitStatsForm(stat: Stat) {
    if (this.appId != undefined) {
      this.statService.createStats(this.appId, stat.type as StatType, stat.from, stat.to)
        .subscribe({
          next: this.initStats.bind(this),
          error: this.handleError.bind(this)
        });
    }
  }

  public openCreateEventForm() {
    this.router.navigate([`/app/${this.appId}/event/create`]);
  }

  public openEventTable() {
    this.router.navigate([`/app/${this.appId}/events`]);
  }

}
