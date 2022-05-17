import {Component, EventEmitter, Input, Output, SimpleChanges} from '@angular/core';

@Component({
  selector: 'app-counter',
  templateUrl: './counter.component.html',
  styleUrls: ['./counter.component.css']
})
export class CounterComponent {
  public seconds: number;
  public interval: number | undefined;
  @Output() public timerStopEvent: EventEmitter<void>;
  @Input() public isButtonDisabled: boolean;

  constructor() {
    this.isButtonDisabled = true;
    this.timerStopEvent = new EventEmitter<void>();
    this.seconds = 59;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.isButtonDisabled){
      this.startTimer();
    }
    else{
      this.stopTimer();
    }
  }

  private startTimer() {
    this.interval = setInterval(() => {
      if (this.seconds === 0){
        this.stopTimer();
      }
      else{
        this.seconds--;
      }
    }, 1000);
  }

  private stopTimer() {
    clearInterval(this.interval);
    this.timerStopEvent.emit();
  }
}
