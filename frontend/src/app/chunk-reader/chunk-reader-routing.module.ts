import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ChunkReaderComponent } from './chunk-reader.component';

const routes = [{ path: '', component: ChunkReaderComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  declarations: []
})
export class ChunkReaderRoutingModule {
}
