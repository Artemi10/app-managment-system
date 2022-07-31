import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateEventFormComponent } from './update-event-form.component';

describe('UpdateEventFormComponent', () => {
  let component: UpdateEventFormComponent;
  let fixture: ComponentFixture<UpdateEventFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpdateEventFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateEventFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
