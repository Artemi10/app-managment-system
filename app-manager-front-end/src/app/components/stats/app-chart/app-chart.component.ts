import {AfterViewInit, Component, ElementRef, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {StatData} from "../../../model/stat.model";

@Component({
  selector: 'app-app-chart',
  templateUrl: './app-chart.component.html',
  styleUrls: ['./app-chart.component.css']
})
export class AppChartComponent implements AfterViewInit, OnChanges {
  @ViewChild("chart")
  public chart: ElementRef<HTMLCanvasElement> | undefined;
  public chartInstance: any;
  @Input("stats")
  public stats: StatData[];

  constructor() {
    this.stats = [];
  }

  ngAfterViewInit(): void {
    this.initChart();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.initChart();
  }

  public initChart() {
    if (this.chart !== undefined) {
      if (this.chartInstance !== undefined){
        this.chartInstance.destroy();
      }
      const element = this.chart.nativeElement.getContext('2d');
      // @ts-ignore
      this.chartInstance = new Chart(element, {
        type: 'line',
        data: {
          labels: this.stats.map(stat => stat.date),
          datasets: [{
            label: 'App Events',
            data: this.stats.map(stat => stat.amount),
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
  }

}
