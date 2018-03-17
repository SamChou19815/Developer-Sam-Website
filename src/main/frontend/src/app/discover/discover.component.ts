import { Component, OnInit } from '@angular/core';
import { MatDialog } from "@angular/material";
import { GoogleUserService } from "../google-user/google-user.service";
import { PublicUser, PublicUserData } from "./public-users";
import { DiscoverNetworkService } from "./discover-network.service";
import { SchedulerItemData } from "../scheduler/scheduler-item";
import { WriteSchedulerItemDialogComponent } from "../scheduler/write-scheduler-item-dialog/write-scheduler-item-dialog.component";
import { UpdatePublicUserDialogComponent } from "./update-public-user-dialog/update-public-user-dialog.component";

@Component({
  selector: 'app-discover',
  templateUrl: './discover.component.html',
  styleUrls: ['./discover.component.css']
})
export class DiscoverComponent implements OnInit {

  publicUsers: PublicUser[];

  constructor(private discoverNetworkService: DiscoverNetworkService,
              private googleUserService: GoogleUserService,
              private dialog: MatDialog) { }

  ngOnInit() {
    this.googleUserService.doTaskAfterSignedIn(() =>
      this.discoverNetworkService.loadPublicUsers(users => this.publicUsers = users));
  }

  /**
   * Open the update public user dialog.
   */
  openUpdatePublicUserDialog(): void {
    this.dialog.open(UpdatePublicUserDialogComponent).afterClosed()
      .subscribe(value => {
        if (value === null || value === undefined) {
          return;
        }
        const publicUserData = value as PublicUserData;
        this.discoverNetworkService.writePublicUserData(publicUserData,
          users => this.publicUsers = users);
      });
  }

}
