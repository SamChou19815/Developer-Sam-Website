import { Component, Inject, OnInit } from '@angular/core';
import {
  PUSHED_CONTROLLER_DATA,
  PushedControllerOverlayRef
} from '../../overlay/push-controller.service';
import { PushedControllerOverlayComponent } from '../../overlay/pushed-controller-overlay.component';
import { AnalyzedArticle } from '../articles';
import { ChunkReaderNetworkService } from '../chunk-reader-network.service';

@Component({
  selector: 'app-chunk-reader-article-detail',
  templateUrl: './chunk-reader-article-detail.component.html',
  styleUrls: ['../../overlay/pushed-controller-overlay.component.scss', './chunk-reader-article-detail.component.css']
})
export class ChunkReaderArticleDetailComponent extends PushedControllerOverlayComponent
  implements OnInit {

  readonly articleDetail: AnalyzedArticle;

  constructor(ref: PushedControllerOverlayRef, @Inject(PUSHED_CONTROLLER_DATA) data: any,
              private chunkReaderNetworkService: ChunkReaderNetworkService) {
    super(ref);
    this.articleDetail = data as AnalyzedArticle;
  }

  ngOnInit() {
  }

  private adjustSummary(limit: number): void {
    this.chunkReaderNetworkService.adjustSummary(this.articleDetail.key, limit,
      summaries => this.articleDetail.summaries = summaries);
  }

  less(): void {
    const newLimit = this.articleDetail.summaries.length - 1;
    this.adjustSummary(newLimit);
  }

  more(): void {
    const newLimit = this.articleDetail.summaries.length + 1;
    this.adjustSummary(newLimit);
  }

}
