import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WriteSchedulerItemDialogComponent } from './write-scheduler-item-dialog.component';

describe('WriteSchedulerItemDialogComponent', () => {
  let component: WriteSchedulerItemDialogComponent;
  let fixture: ComponentFixture<WriteSchedulerItemDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WriteSchedulerItemDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WriteSchedulerItemDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
