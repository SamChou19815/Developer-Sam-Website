import { async, ComponentFixture, TestBed } from "@angular/core/testing";

import { PushedControllerOverlayComponent } from "./pushed-controller-overlay.component";

describe("PushedControllerOverlayComponent", () => {
  let component: PushedControllerOverlayComponent;
  let fixture: ComponentFixture<PushedControllerOverlayComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PushedControllerOverlayComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PushedControllerOverlayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
