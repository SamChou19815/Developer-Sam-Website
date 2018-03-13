import { NgModule } from '@angular/core';
import { ChunkReaderComponent } from './chunk-reader.component';
import { RouterModule } from '@angular/router';

const routes = [
  { path: '', component: ChunkReaderComponent }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [RouterModule],
  declarations: []
})
export class ChunkReaderRoutingModule { }
