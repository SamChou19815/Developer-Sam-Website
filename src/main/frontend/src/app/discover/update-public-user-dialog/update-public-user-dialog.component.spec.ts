import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdatePublicUserDialogComponent } from './update-public-user-dialog.component';

describe('UpdatePublicUserDialogComponent', () => {
  let component: UpdatePublicUserDialogComponent;
  let fixture: ComponentFixture<UpdatePublicUserDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UpdatePublicUserDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdatePublicUserDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
