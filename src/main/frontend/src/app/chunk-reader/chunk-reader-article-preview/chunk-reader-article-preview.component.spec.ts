import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChunkReaderArticlePreviewComponent } from './chunk-reader-article-preview.component';

describe('ChunkReaderArticlePreviewComponent', () => {
  let component: ChunkReaderArticlePreviewComponent;
  let fixture: ComponentFixture<ChunkReaderArticlePreviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChunkReaderArticlePreviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChunkReaderArticlePreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
