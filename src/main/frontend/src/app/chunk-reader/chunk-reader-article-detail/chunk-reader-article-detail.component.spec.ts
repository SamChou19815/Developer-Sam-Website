import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChunkReaderArticleDetailComponent } from './chunk-reader-article-detail.component';

describe('ChunkReaderArticleDetailComponent', () => {
  let component: ChunkReaderArticleDetailComponent;
  let fixture: ComponentFixture<ChunkReaderArticleDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChunkReaderArticleDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChunkReaderArticleDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
