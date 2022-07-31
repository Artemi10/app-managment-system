import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventNameFieldComponent } from './event-name-field.component';

describe('EventNameFieldComponent', () => {
  let component: EventNameFieldComponent;
  let fixture: ComponentFixture<EventNameFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EventNameFieldComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventNameFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
