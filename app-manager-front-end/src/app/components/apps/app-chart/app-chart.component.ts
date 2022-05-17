import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import {Stat, StatData} from "../../../model/stat.model";
import {StatService} from "../../../service/stat/stat.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-app-chart',
  templateUrl: './app-chart.component.html',
  styleUrls: ['./app-chart.component.css']
})
export class AppChartComponent implements AfterViewInit {
  @ViewChild('chart')
  public chart: ElementRef<HTMLCanvasElement> | undefined;
  public chartInstance: any;
  public isStatFormOpened: boolean;

  constructor(private statService: StatService,
              private router: Router,
              private activatedRoute: ActivatedRoute) {
    this.isStatFormOpened = false;
  }

  private get appId(): number | undefined {
    const appIdStr = this.activatedRoute.snapshot.paramMap.get('id');
    if (appIdStr !== null) {
      return parseInt(appIdStr);
    }
    else {
      return undefined;
    }
  }

  ngAfterViewInit(): void {
    if (this.appId != undefined) {
      this.statService.createStats(this.appId, 'days')
        .subscribe({
          next: this.initChart.bind(this),
          error: this.handleError.bind(this)
        });
    }
  }

  private initChart(stats: StatData[]) {
    if (this.chart !== undefined) {
      if (this.chartInstance !== undefined){
        this.chartInstance.destroy();
      }
      const element = this.chart.nativeElement.getContext('2d');
      // @ts-ignore
      this.chartInstance = new Chart(element, {
        type: 'line',
        data: {
          labels: stats.map(stat => stat.date),
          datasets: [{
            label: 'App Events',
            data: stats.map(stat => stat.amount),
            fill: false,
            borderColor: 'rgb(75, 192, 192)',
            tension: 0.1
          }]
        },
        options: {
          scales: {
            y: {
              beginAtZero: true
            }
          }
        }
      });
    }
    this.closeStatsForm();
  }

  private handleError(){
    this.router.navigate(['/']);
  }

  public openStatsForm() {
    this.isStatFormOpened = true;
  }

  public closeStatsForm() {
    this.isStatFormOpened = false;
  }

  public submitStatsForm(stat: Stat) {
    if (this.appId != undefined) {
      this.statService.createStats(this.appId, stat.type, stat.from, stat.to)
        .subscribe({
          next: this.initChart.bind(this),
          error: this.handleError.bind(this)
        });
    }
  }

  public openCreateEventForm() {
    this.router.navigate([`/app/${this.appId}/event`]);
  }

}
