import { TestBed, inject } from '@angular/core/testing';

import { ChunkReaderNetworkService } from './chunk-reader-network.service';

describe('ChunkReaderNetworkService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ChunkReaderNetworkService]
    });
  });

  it('should be created', inject([ChunkReaderNetworkService], (service: ChunkReaderNetworkService) => {
    expect(service).toBeTruthy();
  }));
});
