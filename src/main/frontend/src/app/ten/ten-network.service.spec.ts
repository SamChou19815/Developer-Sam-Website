import { TestBed, inject } from '@angular/core/testing';

import { TenNetworkService } from './ten-network.service';

describe('TenNetworkService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TenNetworkService]
    });
  });

  it('should be created', inject([TenNetworkService], (service: TenNetworkService) => {
    expect(service).toBeTruthy();
  }));
});
