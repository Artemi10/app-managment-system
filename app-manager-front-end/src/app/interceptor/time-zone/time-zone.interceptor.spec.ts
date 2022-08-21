import { TestBed } from '@angular/core/testing';

import { TimeZoneInterceptor } from './time-zone.interceptor';

describe('TimeZoneInterceptor', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [
      TimeZoneInterceptor
      ]
  }));

  it('should be created', () => {
    const interceptor: TimeZoneInterceptor = TestBed.inject(TimeZoneInterceptor);
    expect(interceptor).toBeTruthy();
  });
});
