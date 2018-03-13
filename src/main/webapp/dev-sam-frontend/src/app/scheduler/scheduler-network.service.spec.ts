import { TestBed, inject } from '@angular/core/testing';

import { SchedulerNetworkService } from './scheduler-network.service';

describe('SchedulerNetworkService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SchedulerNetworkService]
    });
  });

  it('should be created', inject([SchedulerNetworkService], (service: SchedulerNetworkService) => {
    expect(service).toBeTruthy();
  }));
});
