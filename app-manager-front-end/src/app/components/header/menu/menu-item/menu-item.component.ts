import { Component, Input, OnInit } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-menu-item',
  templateUrl: './menu-item.component.html',
  styleUrls: ['./menu-item.component.css']
})
export class MenuItemComponent implements OnInit {
  @Input()
  public itemName: string;
  @Input()
  public href: string;

  public get isActive(): boolean {
    return this.router.url == this.href;
  }

  constructor(private router: Router) {
    this.itemName = 'item';
    this.href = '';
  }

  ngOnInit(): void {
  }

}
