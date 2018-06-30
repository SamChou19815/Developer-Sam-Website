import { NgModule } from '@angular/core';
import { AddArticleDialogComponent } from './add-article-dialog/add-article-dialog.component';
import {
  ChunkReaderArticlePreviewComponent
} from './chunk-reader-article-preview/chunk-reader-article-preview.component';
import { ChunkReaderNetworkService } from './chunk-reader-network.service';
import { SharedModule } from '../shared/shared.module';
import { ChunkReaderRoutingModule } from './chunk-reader-routing.module';
import { ChunkReaderComponent } from './chunk-reader.component';
import { ChunkReaderArticleDetailComponent } from './chunk-reader-article-detail/chunk-reader-article-detail.component';
import { PushControllerService } from '../overlay/push-controller.service';

@NgModule({
  imports: [
    SharedModule,
    ChunkReaderRoutingModule
  ],
  declarations: [ChunkReaderComponent, AddArticleDialogComponent, ChunkReaderArticlePreviewComponent,
    ChunkReaderArticleDetailComponent],
  providers: [ChunkReaderNetworkService, PushControllerService],
  entryComponents: [AddArticleDialogComponent, ChunkReaderArticleDetailComponent]
})
export class ChunkReaderModule {}
