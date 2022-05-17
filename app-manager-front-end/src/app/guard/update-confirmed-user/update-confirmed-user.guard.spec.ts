import { TestBed } from '@angular/core/testing';

import { UpdateConfirmedUserGuard } from './update-confirmed-user.guard';

describe('UpdateConfirmedUserGuard', () => {
  let guard: UpdateConfirmedUserGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(UpdateConfirmedUserGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
