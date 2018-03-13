import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TenCellComponent } from './ten-cell.component';

describe('TenCellComponent', () => {
  let component: TenCellComponent;
  let fixture: ComponentFixture<TenCellComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TenCellComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TenCellComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
