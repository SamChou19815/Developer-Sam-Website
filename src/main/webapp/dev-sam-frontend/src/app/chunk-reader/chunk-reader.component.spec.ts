import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChunkReaderComponent } from './chunk-reader.component';

describe('ChunkReaderComponent', () => {
  let component: ChunkReaderComponent;
  let fixture: ComponentFixture<ChunkReaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChunkReaderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChunkReaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
