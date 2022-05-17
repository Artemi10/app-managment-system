import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ForgetFieldComponent } from './forget-field.component';

describe('ForgetFieldComponent', () => {
  let component: ForgetFieldComponent;
  let fixture: ComponentFixture<ForgetFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ForgetFieldComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ForgetFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
