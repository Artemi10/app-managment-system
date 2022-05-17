import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css']
})
export class PaginationComponent implements OnChanges {
  @Input() public pageAmount: number;
  @Input() public currentPage: number;
  @Output() public currentPageChange: EventEmitter<number>;
  public pages: number[];

  constructor() {
    this.pageAmount = 1;
    this.currentPage = 1;
    this.currentPageChange = new EventEmitter<number>();
    this.pages = [1];
  }

  ngOnChanges(changes: SimpleChanges) {
    this.pages = Array(this.pageAmount)
      .fill(0)
      .map((x, i)=> i + 1);
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
    }
  }
}
