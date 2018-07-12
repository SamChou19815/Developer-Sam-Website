import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { AddArticleDialogComponent } from './add-article-dialog/add-article-dialog.component';
import { ChunkReaderRoutingModule } from './chunk-reader-routing.module';
import { ChunkReaderComponent } from './chunk-reader.component';
import { KnowledgeGraphComponent } from './knowledge-graph.component';

@NgModule({
  imports: [SharedModule, ChunkReaderRoutingModule],
  declarations: [ChunkReaderComponent, AddArticleDialogComponent, KnowledgeGraphComponent],
  providers: [],
  entryComponents: [AddArticleDialogComponent]
})
export class ChunkReaderModule {
}
