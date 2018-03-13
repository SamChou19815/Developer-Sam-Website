import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AnalyzedArticle } from '../articles';

@Component({
  selector: 'app-chunk-reader-article-preview',
  templateUrl: './chunk-reader-article-preview.component.html',
  styleUrls: ['./chunk-reader-article-preview.component.css']
})
export class ChunkReaderArticlePreviewComponent implements OnInit {

  @Input() articlePreview: AnalyzedArticle;
  @Output() viewDetailClicked = new EventEmitter<void>();

  constructor() { }

  ngOnInit() { }

  /**
   * Edit itself.
   */
  viewDetail(): void {
    this.viewDetailClicked.emit(null);
  }

}
