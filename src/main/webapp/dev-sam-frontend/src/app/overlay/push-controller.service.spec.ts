import { TestBed, inject } from "@angular/core/testing";

import { PushControllerService } from "./push-controller.service";

describe("PushControllerService", () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PushControllerService]
    });
  });

  it("should be created", inject([PushControllerService], (service: PushControllerService) => {
    expect(service).toBeTruthy();
  }));
});
