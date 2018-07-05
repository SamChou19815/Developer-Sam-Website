import { NgModule } from '@angular/core';
import { PushControllerService } from '../overlay/push-controller.service';
import { SharedModule } from '../shared/shared.module';
import { AddArticleDialogComponent } from './add-article-dialog/add-article-dialog.component';
import { ArticleDetailComponent } from './article-detail/article-detail.component';
import { ArticlePreviewComponent } from './article-preview/article-preview.component';
import { ChunkReaderRoutingModule } from './chunk-reader-routing.module';
import { ChunkReaderComponent } from './chunk-reader.component';

@NgModule({
  imports: [
    SharedModule,
    ChunkReaderRoutingModule
  ],
  declarations: [ChunkReaderComponent, AddArticleDialogComponent,
    ArticlePreviewComponent, ArticleDetailComponent],
  providers: [PushControllerService],
  entryComponents: [AddArticleDialogComponent, ArticleDetailComponent]
})
export class ChunkReaderModule {
}
