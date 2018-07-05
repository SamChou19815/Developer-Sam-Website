import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { GoogleUserService } from '../google-user/google-user.service';
import { LoadingOverlayService } from '../overlay/loading-overlay.service';
import { EditItemDialogComponent } from './edit-scheduler-item-dialog/edit-item-dialog.component';
import { SchedulerItem } from './scheduler-data';
import { SchedulerNetworkService } from './scheduler-network.service';

@Component({
  selector: 'app-scheduler',
  templateUrl: './scheduler.component.html',
  styleUrls: ['./scheduler.component.css']
})
export class SchedulerComponent implements OnInit {

  items: SchedulerItem[] = [];

  constructor(private googleUserService: GoogleUserService,
              private networkService: SchedulerNetworkService,
              private loadingService: LoadingOverlayService,
              private dialog: MatDialog) {
  }

  async ngOnInit() {
    setTimeout(async () => {
      const ref = this.loadingService.open();
      this.networkService.firebaseAuthToken = await this.googleUserService.afterSignedIn();
      const data = await this.networkService.loadData();
      this.items = data.items.map(i => new SchedulerItem(i));
      ref.close();
    }, 50);
  }

  async editItem(item?: SchedulerItem) {
    const toBeEdited = item == null ? new SchedulerItem() : new SchedulerItem(item);
    const value: any = await this.dialog
      .open(EditItemDialogComponent, { data: toBeEdited })
      .afterClosed()
      .toPromise();
    if (value == null) {
      return;
    }
    const edited = value as SchedulerItem;
    const ref = this.loadingService.open();
    const key = await this.networkService.editItem(edited);
    ref.close();
    const itemWithOldRemoved = item == null
      ? this.items : this.items.filter(i => i.key !== item.key);
    itemWithOldRemoved.push(new SchedulerItem(<SchedulerItem>{ ...edited, key: key }));
    this.items = itemWithOldRemoved.sort((a, b) => a.deadline - b.deadline);
  }

  async deleteItem(item: SchedulerItem) {
    if (item.key == null) {
      return;
    }
    const ref = this.loadingService.open();
    await this.networkService.deleteItem(item.key);
    ref.close();
    this.items = this.items.filter(i => i.key !== item.key);
  }

  async markAs(completed: boolean, item: SchedulerItem) {
    if (item.key == null) {
      return;
    }
    const ref = this.loadingService.open();
    await this.networkService.markAs(completed, item.key);
    ref.close();
    item.isCompleted = completed;
  }

}
