import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ClosableControllerComponent } from './closable-controller.component';

describe('ClosableControllerComponent', () => {
  let component: ClosableControllerComponent;
  let fixture: ComponentFixture<ClosableControllerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ClosableControllerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClosableControllerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
