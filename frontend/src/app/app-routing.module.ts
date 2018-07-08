import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'friends', loadChildren: 'app/friends/friends.module#FriendsModule' },
  { path: 'scheduler', loadChildren: 'app/scheduler/scheduler.module#SchedulerModule' },
  { path: 'chunkreader', loadChildren: 'app/chunk-reader/chunk-reader.module#ChunkReaderModule' },
  { path: 'playground/ten', loadChildren: 'app/ten/ten.module#TenModule' }
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
