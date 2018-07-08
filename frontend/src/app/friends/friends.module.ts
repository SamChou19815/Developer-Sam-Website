import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { FriendCardComponent } from './friend-card/friend-card.component';
import { FriendsRoutingModule } from './friends-routing.module';
import { FriendsComponent } from './friends.component';
import { FriendRequestComponent } from './friend-request/friend-request.component';

@NgModule({
  imports: [SharedModule, FriendsRoutingModule],
  declarations: [FriendsComponent, FriendCardComponent, FriendRequestComponent]
})
export class FriendsModule {
}
