import { TestBed } from '@angular/core/testing';

import { UpdateNotConfirmedUserGuard } from './update-not-confirmed-user.guard';

describe('UpdateNotConfirmedUserGuard', () => {
  let guard: UpdateNotConfirmedUserGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(UpdateNotConfirmedUserGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
