import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateAppFormComponent } from './update-app-form.component';

describe('UpdateAppFormComponent', () => {
  let component: UpdateAppFormComponent;
  let fixture: ComponentFixture<UpdateAppFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpdateAppFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateAppFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
