import { TestBed } from '@angular/core/testing';

import { UnAuthUserGuard } from './un-auth-user.guard';

describe('UnAuthUserGuard', () => {
  let guard: UnAuthUserGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(UnAuthUserGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
