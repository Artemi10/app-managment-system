export class PageCriteria {
  private _page: number;
  private readonly _pageSize: number;

  constructor(page: number = 1, pageSize: number = 1) {
    this._page = page;
    this._pageSize = pageSize;
  }

  pageAmount(elementAmount: number): number {
    if (elementAmount > 0 && elementAmount % this.pageSize == 0) {
      return Math.floor(elementAmount / this.pageSize);
    }
    else {
      return Math.ceil(elementAmount / this.pageSize);
    }
  }

  set page(value: number) {
    this._page = value;
  }

  get page(): number {
    return this._page;
  }

  get pageSize(): number {
    return this._pageSize;
  }
}
