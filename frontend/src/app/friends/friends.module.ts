import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { FriendsRoutingModule } from './friends-routing.module';
import { FriendsComponent } from './friends.component';

@NgModule({
  imports: [SharedModule, FriendsRoutingModule],
  declarations: [FriendsComponent]
})
export class FriendsModule {
}
