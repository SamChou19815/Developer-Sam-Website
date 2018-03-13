import { Component, OnInit } from '@angular/core';
import { AnalyzedArticle, RawArticle } from './articles';
import { ChunkReaderNetworkService } from './chunk-reader-network.service';
import { MatDialog } from '@angular/material';
import { AddArticleDialogComponent } from './add-article-dialog/add-article-dialog.component';
import { AlertComponent } from '../alert/alert.component';
import { PushControllerService } from '../overlay/push-controller.service';
import { ChunkReaderArticleDetailComponent } from './chunk-reader-article-detail/chunk-reader-article-detail.component';
import { GoogleUserService } from '../google-user/google-user.service';

@Component({
  selector: 'app-chunk-reader',
  templateUrl: './chunk-reader.component.html',
  styleUrls: ['./chunk-reader.component.css']
})
export class ChunkReaderComponent implements OnInit {

  articlesPreview: AnalyzedArticle[];

  constructor(private chunkReaderNetworkService: ChunkReaderNetworkService,
              private googleUserService: GoogleUserService,
              private pushControllerService: PushControllerService,
              private dialog: MatDialog) { }

  ngOnInit() {
    this.googleUserService.doTaskAfterSignedIn(() =>
      this.chunkReaderNetworkService.loadArticlesPreview(articles => this.articlesPreview = articles));
  }

  /**
   * Open a dialog for adding an article.
   */
  openAddArticleDialog(): void {
    this.dialog.open(AddArticleDialogComponent).afterClosed().subscribe(value => {
      if (value === null || value === undefined) {
        return;
      }
      const rawArticle: RawArticle = value as RawArticle;
      this.chunkReaderNetworkService.analyzeArticle(rawArticle, articles => this.articlesPreview = articles,
        () => this.dialog.open(AlertComponent, {
          data: `Sorry, your article cannot be analyzed for some reasons.
          The failure has been logged in the system and we will try to figure out why.`
        }));
    });
  }

  /**
   * Display the article details for a specified article.
   * @param {AnalyzedArticle} analyzedArticle the article to get more details.
   */
  displayArticleDetails(analyzedArticle: AnalyzedArticle) {
    this.chunkReaderNetworkService.loadArticleDetail(analyzedArticle.keyString, article => {
      this.pushControllerService.open(ChunkReaderArticleDetailComponent, article);
    });
  }

}
