import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FriendsComponent } from './friends.component';

const routes = [
  { path: '', component: FriendsComponent }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [RouterModule],
  declarations: []
})
export class FriendsRoutingModule {
}
