import {FormGroup } from "@angular/forms";

export const isInputInvalid = (form: FormGroup, inputName: string) =>
  form.controls[inputName].invalid && form.controls[inputName].touched;
