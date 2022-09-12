import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventNavigationComponent } from './event-navigation.component';

describe('EventNavigationComponent', () => {
  let component: EventNavigationComponent;
  let fixture: ComponentFixture<EventNavigationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EventNavigationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventNavigationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
