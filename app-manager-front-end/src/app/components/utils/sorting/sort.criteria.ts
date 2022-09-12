import {OrderType} from "../../../model/app.model";

export class SortCriteria {
  private _sortField: string;
  private _orderType: OrderType;
  private readonly _sortElements: DropdownElement[];

  constructor(
    sortField: string = "id",
    orderType: OrderType = OrderType.ASC,
    sortElements: DropdownElement[] = [new DropdownElement()]
  ) {
    this._sortField = sortField;
    this._orderType = orderType;
    this._sortElements = sortElements;
  }

  changeOrderType() {
    this._orderType = this._orderType == OrderType.DESC ? OrderType.ASC : OrderType.DESC;
  }

  selectSortField(field: string) {
    this._sortField = field;
    this.sortElements
      .forEach((value) => value.selected = value.key == field);
  }

  get isDescending(): boolean {
    return this.orderType == OrderType.DESC;
  }

  get sortField(): string {
    return this._sortField;
  }

  get orderType(): OrderType {
    return this._orderType;
  }

  get sortElements(): DropdownElement[] {
    return this._sortElements;
  }
}

export class DropdownElement {
  private readonly _key: string;
  private readonly _name: string;
  private _selected: boolean;

  constructor(key: string = "id", name: string = "Id", selected: boolean = true) {
    this._key = key;
    this._name = name;
    this._selected = selected;
  }

  get key(): string {
    return this._key;
  }

  get name(): string {
    return this._name;
  }

  get selected(): boolean {
    return this._selected;
  }

  set selected(value: boolean) {
    this._selected = value;
  }
}
