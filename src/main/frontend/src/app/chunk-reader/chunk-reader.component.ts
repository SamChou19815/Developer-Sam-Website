import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { GoogleUserService } from '../google-user/google-user.service';
import { PushControllerService } from '../overlay/push-controller.service';
import { AddArticleDialogComponent } from './add-article-dialog/add-article-dialog.component';
import { AnalyzedArticle, RawArticle } from './articles';
import { ChunkReaderArticleDetailComponent } from './chunk-reader-article-detail/chunk-reader-article-detail.component';
import { ChunkReaderNetworkService } from './chunk-reader-network.service';

@Component({
  selector: 'app-chunk-reader',
  templateUrl: './chunk-reader.component.html',
  styleUrls: ['./chunk-reader.component.css']
})
export class ChunkReaderComponent implements OnInit {

  articlesPreview: AnalyzedArticle[] = [];

  constructor(private chunkReaderNetworkService: ChunkReaderNetworkService,
              private googleUserService: GoogleUserService,
              private pushControllerService: PushControllerService,
              private dialog: MatDialog) {
  }

  ngOnInit() {
    this.googleUserService.afterSignedIn(() => setTimeout(() => {
      this.chunkReaderNetworkService.loadArticlesPreview(a => this.articlesPreview = a);
    }, 50));
  }

  openAddArticleDialog(): void {
    this.dialog.open(AddArticleDialogComponent).afterClosed().subscribe(value => {
      if (value === null || value === undefined) {
        return;
      }
      this.chunkReaderNetworkService.analyzeArticle(value as RawArticle);
    });
  }

  displayArticleDetails(analyzedArticle: AnalyzedArticle) {
    this.chunkReaderNetworkService.loadArticleDetail(analyzedArticle.key, article => {
      this.pushControllerService.open(ChunkReaderArticleDetailComponent, article);
    });
  }

}
