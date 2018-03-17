import { NgModule } from '@angular/core';
import { DiscoverRoutingModule } from './discover-routing.module';
import { DiscoverComponent } from './discover.component';
import { PublicUserComponent } from './public-user/public-user.component';
import { DiscoverNetworkService } from './discover-network.service';
import { UpdatePublicUserDialogComponent } from './update-public-user-dialog/update-public-user-dialog.component';
import { SharedModule } from "../shared/shared.module";

@NgModule({
  imports: [
    SharedModule,
    DiscoverRoutingModule
  ],
  declarations: [DiscoverComponent, PublicUserComponent, UpdatePublicUserDialogComponent],
  providers: [DiscoverNetworkService],
  entryComponents: [UpdatePublicUserDialogComponent]
})
export class DiscoverModule { }
