import {Component, EventEmitter, Output} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {Stat} from "../../../../model/stat.model";

@Component({
  selector: 'app-stats-form',
  templateUrl: './stats-form.component.html',
  styleUrls: ['./stats-form.component.css']
})
export class StatsFormComponent {
  @Output()
  public submitForm: EventEmitter<Stat>;
  public statsForm: FormGroup;
  public fromDate: string;
  public toDate: string;

  constructor(private formBuilder: FormBuilder) {
    this.fromDate = '';
    this.toDate = '';
    this.submitForm = new EventEmitter<Stat>();
    this.statsForm = formBuilder.group({
      typeField: this.formBuilder.group({
        type:  new FormControl('MONTH')
      })
    });
  }

  private get requestBody(): Stat {
    const formValue = this.statsForm.value;
    return {
      from: this.fromDate,
      to: this.toDate,
      type: formValue.typeField.type
    }
  }

  public submitFormListener() {
    this.submitForm.emit(this.requestBody);
  }

  public fromDateChange(date: string) {
    this.fromDate = date;
  }

  public toDateChange(date: string) {
    this.toDate = date;
  }
}
