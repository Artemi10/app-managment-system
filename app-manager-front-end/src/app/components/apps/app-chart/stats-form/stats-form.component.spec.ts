import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatsFormComponent } from './stats-form.component';

describe('StatsFormComponent', () => {
  let component: StatsFormComponent;
  let fixture: ComponentFixture<StatsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StatsFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StatsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
