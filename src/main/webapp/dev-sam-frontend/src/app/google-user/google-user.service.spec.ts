import { TestBed, inject } from '@angular/core/testing';

import { GoogleUserService } from './google-user.service';

describe('GoogleUserService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GoogleUserService]
    });
  });

  it('should be created', inject([GoogleUserService], (service: GoogleUserService) => {
    expect(service).toBeTruthy();
  }));
});
