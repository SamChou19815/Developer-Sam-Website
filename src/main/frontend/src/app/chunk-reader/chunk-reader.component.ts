import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { AlertComponent } from "../alert/alert.component";
import { GoogleUserService } from '../google-user/google-user.service';
import { LoadingOverlayService } from "../overlay/loading-overlay.service";
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

  constructor(private googleUserService: GoogleUserService,
              private chunkReaderNetworkService: ChunkReaderNetworkService,
              private pushControllerService: PushControllerService,
              private loadingService: LoadingOverlayService,
              private dialog: MatDialog) {
  }

  ngOnInit() {
    setTimeout(async () => {
      const ref = this.loadingService.open();
      await this.googleUserService.afterSignedIn();
      this.articlesPreview = await this.chunkReaderNetworkService.loadArticlesPreview();
      ref.close();
    }, 50);
  }

  async openAddArticleDialog(): void {
    const value: any = await this.dialog.open(AddArticleDialogComponent).afterClosed().toPromise();
    if (value == null) {
      return;
    }
    const successful = await this.chunkReaderNetworkService.analyzeArticle(value as RawArticle);
    const message = successful
      ? `Your article is being analyzed right now. Refresh the page later to see its analysis.`
      : `Sorry, your article cannot be analyzed for some unknown reasons.
      The failure has been logged in the system and we will try to figure out why.`;
    this.dialog.open(AlertComponent, { data: message });
  }

  async displayArticleDetails(analyzedArticle: AnalyzedArticle) {
    const article = await this.chunkReaderNetworkService.loadArticleDetail(analyzedArticle.key);
    this.pushControllerService.open(ChunkReaderArticleDetailComponent, article);
  }

}
