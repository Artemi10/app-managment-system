import {Component, EventEmitter, Output} from '@angular/core';
import {UserService} from "../../../../service/user/user.service";

@Component({
  selector: 'app-send-again-link',
  templateUrl: './send-again-link.component.html',
  styleUrls: ['./send-again-link.component.css']
})
export class SendAgainLinkComponent {
  @Output() public success: EventEmitter<void>;
  @Output() public error: EventEmitter<any>;

  constructor(private userService: UserService) {
    this.success = new EventEmitter<void>();
    this.error = new EventEmitter<any>();
  }

  public resetUserAgain() {
    this.userService.resetUserAgain()
      .subscribe({
        next : () => this.success.emit(),
        error : () => this.error.emit()
      });
  }

}
