import { TestBed, inject } from '@angular/core/testing';

import { DiscoverNetworkService } from './discover-network.service';

describe('DiscoverNetworkService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DiscoverNetworkService]
    });
  });

  it('should be created', inject([DiscoverNetworkService], (service: DiscoverNetworkService) => {
    expect(service).toBeTruthy();
  }));
});
