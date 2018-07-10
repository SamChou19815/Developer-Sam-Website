import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AnalyzedArticle } from '../chunk-reader-data';

@Component({
  selector: 'app-chunk-reader-article-preview',
  templateUrl: './article-preview.component.html',
  styleUrls: ['./article-preview.component.css']
})
export class ArticlePreviewComponent implements OnInit {

  @Input() articlePreview: AnalyzedArticle = AnalyzedArticle.defaultValue;
  @Output() viewDetailClicked = new EventEmitter<undefined>();
  @Output() deleteClicked = new EventEmitter<undefined>();

  constructor() {
  }

  ngOnInit() {
  }

  get time(): string {
    return new Date(this.articlePreview.time).toLocaleDateString();
  }

}
