import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { AlertComponent } from '../shared/alert/alert.component';
import { GoogleUserService } from '../shared/google-user.service';
import { LoadingOverlayService } from '../shared/overlay/loading-overlay.service';
import { PushControllerService } from '../shared/overlay/push-controller.service';
import { shortDelay } from '../shared/util';
import { AddArticleDialogComponent } from './add-article-dialog/add-article-dialog.component';
import { ArticleDetailComponent } from './article-detail/article-detail.component';
import { AnalyzedArticle, RawArticle } from './chunk-reader-data';
import { ChunkReaderNetworkService } from './chunk-reader-network.service';

@Component({
  selector: 'app-chunk-reader',
  templateUrl: 'chunk-reader.component.html',
  styleUrls: ['chunk-reader.component.css']
})
export class ChunkReaderComponent implements OnInit {

  /**
   * A list of article for preview.
   * @type {AnalyzedArticle[]}
   */
  articlesPreview: AnalyzedArticle[] = [];

  constructor(private googleUserService: GoogleUserService,
              private networkService: ChunkReaderNetworkService,
              private pushControllerService: PushControllerService,
              private loadingService: LoadingOverlayService,
              private dialog: MatDialog) {
  }

  ngOnInit() {
    shortDelay(async () => {
      const ref = this.loadingService.open();
      this.networkService.firebaseAuthToken = await this.googleUserService.afterSignedIn();
      this.articlesPreview = await this.networkService.loadArticlesPreview();
      ref.close();
    });
  }

  /**
   * Open an add article dialog and registers the callback.
   */
  addArticle(): void {
    (async () => {
      const value = await this.dialog.open(AddArticleDialogComponent).afterClosed().toPromise();
      if (value == null) {
        return;
      }
      const successful = await this.networkService.analyzeArticle(value as RawArticle);
      const message = successful
        ? `Your article is being analyzed right now. Refresh the page later to see its result.`
        : `Sorry, your article cannot be analyzed for some unknown reasons.
      The failure has been logged in the system and we will try to figure out why.`;
      this.dialog.open(AlertComponent, { data: message });
    })();
  }

  /**
   * Display the article detail.
   *
   * @param {AnalyzedArticle} analyzedArticle the article to display detail.
   */
  displayArticleDetails(analyzedArticle: AnalyzedArticle): void {
    (async () => {
      const article = await this.networkService.loadArticleDetail(analyzedArticle.key);
      this.pushControllerService.open(ArticleDetailComponent, article);
    })();
  }

  /**
   * Delete the given article.
   *
   * @param {AnalyzedArticle} article article to delete.
   * @param {number} index index of the article.
   */
  deleteArticle(article: AnalyzedArticle, index: number): void {
    if (!confirm('Do you really want to delete this article?')) {
      return;
    }
    (async () => {
      const ref = this.loadingService.open();
      await this.networkService.deleteArticle(article.key);
      this.articlesPreview.splice(index, 1);
      ref.close();
    })();
  }

}
