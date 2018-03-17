import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  {
    path: 'scheduler',
    loadChildren: 'app/scheduler/scheduler.module#SchedulerModule'
  },
  {
    path: 'chunkreader',
    loadChildren: 'app/chunk-reader/chunk-reader.module#ChunkReaderModule'
  },
  {
    path: 'ten',
    loadChildren: 'app/ten/ten.module#TenModule'
  },
  {
    path: 'discover',
    loadChildren: 'app/discover/discover.module#DiscoverModule'
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule],
  declarations: []
})
export class AppRoutingModule {
}
