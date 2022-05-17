import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExtraInfFieldComponent } from './extra-inf-field.component';

describe('ExtraInfFieldComponent', () => {
  let component: ExtraInfFieldComponent;
  let fixture: ComponentFixture<ExtraInfFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExtraInfFieldComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExtraInfFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
