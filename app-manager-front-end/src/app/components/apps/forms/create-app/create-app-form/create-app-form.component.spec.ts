import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateAppFormComponent } from './create-app-form.component';

describe('CreateAppFormComponent', () => {
  let component: CreateAppFormComponent;
  let fixture: ComponentFixture<CreateAppFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateAppFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateAppFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
