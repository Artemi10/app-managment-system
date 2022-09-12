import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import { PageCriteria } from './page.criteria';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css']
})
export class PaginationComponent implements OnChanges {
  @Input()
  public elementAmount: number;
  @Input()
  public pageCriteria: PageCriteria;
  @Output()
  public pageCriteriaChange: EventEmitter<PageCriteria>;
  public firstPages: number[];

  constructor() {
    this.elementAmount = 0;
    this.pageCriteria = new PageCriteria();
    this.pageCriteriaChange = new EventEmitter<PageCriteria>();
    this.firstPages = [1];
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.pageCriteria.page === 1) {
      const pageAmount = this.pageAmount;
      if (pageAmount <= 10) {
        this.firstPages = Array(pageAmount)
          .fill(0)
          .map((x, i)=> i + 1);
      }
      else {
        this.firstPages = Array(10)
          .fill(0)
          .map((x, i)=> i + 1);
      }
    }
  }

  private get pageAmount(): number {
    return this.pageCriteria.pageAmount(this.elementAmount)
  }

  public get isFirstPage(): boolean {
    return this.pageCriteria.page == 1;
  }

  public get isLastPage(): boolean {
    return this.pageCriteria.page == this.pageAmount || this.pageAmount == 0;
  }

  public isCurrentPage(page: number): boolean {
    return page == this.pageCriteria.page;
  }

  public selectPreviousPage() {
    if (!this.isFirstPage) {
      this.pageCriteria.page--;
      this.pageCriteriaChange.emit(this.pageCriteria);
      const firstValue = this.firstPages[0];
      if (this.pageCriteria.page + 1 == firstValue && firstValue > 1) {
        this.firstPages.splice(this.firstPages.length - 1, 1);
        this.firstPages.unshift(firstValue - 1);
      }
    }
  }

  public selectPage(page: number) {
    const lastValue = this.firstPages[this.firstPages.length - 1];
    if (page > 0 && lastValue >= page) {
      this.pageCriteria.page = page;
      this.pageCriteriaChange.emit(this.pageCriteria);
    }
  }

  public selectNextPage() {
    if (!this.isLastPage) {
      this.pageCriteria.page++;
      this.pageCriteriaChange.emit(this.pageCriteria);
      const lastValue = this.firstPages[this.firstPages.length - 1];
      if (lastValue + 1 === this.pageCriteria.page && lastValue < this.pageAmount) {
        this.firstPages.splice(0, 1);
        this.firstPages.push(lastValue + 1);
      }
    }
  }
}
