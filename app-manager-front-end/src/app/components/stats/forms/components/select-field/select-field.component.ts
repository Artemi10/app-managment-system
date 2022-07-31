import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import {ControlContainer, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-select-field',
  templateUrl: './select-field.component.html',
  styleUrls: ['./select-field.component.css']
})
export class SelectFieldComponent implements AfterViewInit {
  @ViewChild("selectElement")
  public dateElement: ElementRef<HTMLSelectElement> | undefined;
  public selectFormGroup: FormGroup | null;

  constructor(private controlContainer: ControlContainer) {
    this.selectFormGroup = null;
  }

  ngAfterViewInit(): void {
    if (this.dateElement !== undefined) {
      // @ts-ignore
      const instances = M.FormSelect.init(this.dateElement.nativeElement, {});
    }
  }

  ngOnInit(): void {
    // @ts-ignore
    this.selectFormGroup = this.controlContainer.control;
  }
}
