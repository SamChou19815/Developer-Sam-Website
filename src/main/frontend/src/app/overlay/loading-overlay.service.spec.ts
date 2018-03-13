import { TestBed, inject } from "@angular/core/testing";

import { LoadingOverlayService } from "./loading-overlay.service";

describe("LoadingOverlayService", () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LoadingOverlayService]
    });
  });

  it("should be created", inject([LoadingOverlayService], (service: LoadingOverlayService) => {
    expect(service).toBeTruthy();
  }));
});
