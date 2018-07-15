import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { NotFoundComponent } from './shared/not-found/not-found.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'friends', loadChildren: 'app/friends/friends.module#FriendsModule' },
  { path: 'scheduler', loadChildren: 'app/scheduler/scheduler.module#SchedulerModule' },
  { path: 'chunk_reader', loadChildren: 'app/chunk-reader/chunk-reader.module#ChunkReaderModule' },
  { path: 'rss_reader', loadChildren: 'app/rss-reader/rss-reader.module#RssReaderModule' },
  { path: 'playground/ten', loadChildren: 'app/ten/ten.module#TenModule' },
  { path: '**', component: NotFoundComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      preloadingStrategy: PreloadAllModules
    })
  ],
  exports: [RouterModule],
  declarations: []
})
export class AppRoutingModule {
}
