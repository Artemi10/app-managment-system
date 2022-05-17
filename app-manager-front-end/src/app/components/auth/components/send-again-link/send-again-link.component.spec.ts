import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SendAgainLinkComponent } from './send-again-link.component';

describe('SendAgainLinkComponent', () => {
  let component: SendAgainLinkComponent;
  let fixture: ComponentFixture<SendAgainLinkComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SendAgainLinkComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SendAgainLinkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
