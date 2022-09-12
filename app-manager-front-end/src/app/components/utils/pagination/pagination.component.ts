import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css']
})
export class PaginationComponent implements OnChanges {
  @Input() public pageAmount: number;
  @Input() public currentPage: number;
  @Output() public currentPageChange: EventEmitter<number>;
  public firstPages: number[];

  constructor() {
    this.pageAmount = 1;
    this.currentPage = 1;
    this.currentPageChange = new EventEmitter<number>();
    this.firstPages = [1];
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.currentPage === 1) {
      if (this.pageAmount <= 10) {
        this.firstPages = Array(this.pageAmount)
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

  public get isFirstPage(): boolean {
    return this.currentPage == 1;
  }

  public get isLastPage(): boolean {
    return this.currentPage == this.pageAmount;
  }

  public isCurrentPage(page: number): boolean {
    return page == this.currentPage;
  }

  public selectPreviousPage() {
    if (!this.isFirstPage) {
      this.currentPage--;
      this.currentPageChange.emit(this.currentPage);
      const firstValue = this.firstPages[0];
      if (this.currentPage + 1 == firstValue && firstValue > 1) {
        this.firstPages.splice(this.firstPages.length - 1, 1);
        this.firstPages.unshift(firstValue - 1);
      }
    }
  }

  public selectPage(page: number) {
    this.currentPage = page;
    this.currentPageChange.emit(this.currentPage);
  }

  public selectNextPage() {
    if (!this.isLastPage) {
      this.currentPage++;
      this.currentPageChange.emit(this.currentPage);
      const lastValue = this.firstPages[this.firstPages.length - 1];
      if (lastValue + 1 === this.currentPage && lastValue < this.pageAmount) {
        this.firstPages.splice(0, 1);
        this.firstPages.push(lastValue + 1);
      }
    }
  }
}
