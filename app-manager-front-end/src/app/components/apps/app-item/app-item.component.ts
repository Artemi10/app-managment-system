import {Component, EventEmitter, Input, Output} from '@angular/core';
import { Router } from '@angular/router';
import {App} from 'src/app/model/app.model';
import {AppService} from 'src/app/service/app/app.service';

@Component({
  selector: 'app-app-item',
  templateUrl: './app-item.component.html',
  styleUrls: ['./app-item.component.css']
})
export class AppItemComponent {
  @Input()
  public app: App | undefined;
  @Output()
  public deleteApp: EventEmitter<App>;
  public isHovered: boolean;

  constructor(private appService: AppService,
              private router: Router) {
    this.isHovered = false;
    this.deleteApp = new EventEmitter<App>();
  }

  public showActions() {
    this.isHovered = true;
  }

  public hideActions() {
    this.isHovered = false;
  }

  public deleteAppItem() {
    if (this.app != undefined) {
      this.appService.deleteUserApp(this.app.id)
        .subscribe({
          next: this.handleDeleteAction.bind(this)
        });
    }
  }

  public openEventsTable() {
    if (this.app != undefined) {
      this.router.navigate([`/app/${this.app.id}/events`]);
    }
    else {
      this.router.navigate([`/`]);
    }
  }

  private handleDeleteAction() {
    this.deleteApp.emit(this.app)
  }

  public openUpdateForm() {
    if (this.app != undefined) {
      this.router.navigate([`/app/${this.app.id}/update`]);
    }
    else {
      this.router.navigate([`/`]);
    }
  }

  public openAppChart() {
    if (this.app != undefined) {
      this.router.navigate([`/app/${this.app.id}/stats`]);
    }
    else {
      this.router.navigate([`/`]);
    }
  }
}
