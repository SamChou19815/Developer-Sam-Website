import { TestBed, inject } from '@angular/core/testing';

import { TenService } from './ten.service';

describe('TenService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TenService]
    });
  });

  it('should be created', inject([TenService], (service: TenService) => {
    expect(service).toBeTruthy();
  }));
});
