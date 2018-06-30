import { NgModule } from '@angular/core';
import { PushControllerService } from '../overlay/push-controller.service';
import { SharedModule } from '../shared/shared.module';
import { AddArticleDialogComponent } from './add-article-dialog/add-article-dialog.component';
import { ChunkReaderArticleDetailComponent } from './chunk-reader-article-detail/chunk-reader-article-detail.component';
import { ChunkReaderArticlePreviewComponent } from './chunk-reader-article-preview/chunk-reader-article-preview.component';
import { ChunkReaderRoutingModule } from './chunk-reader-routing.module';
import { ChunkReaderComponent } from './chunk-reader.component';

@NgModule({
  imports: [
    SharedModule,
    ChunkReaderRoutingModule
  ],
  declarations: [ChunkReaderComponent, AddArticleDialogComponent,
    ChunkReaderArticlePreviewComponent, ChunkReaderArticleDetailComponent],
  providers: [PushControllerService],
  entryComponents: [AddArticleDialogComponent, ChunkReaderArticleDetailComponent]
})
export class ChunkReaderModule {
}
