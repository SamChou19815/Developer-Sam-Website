import { Component, OnInit } from '@angular/core';
import { SchedulerNetworkService } from './scheduler-network.service';
import { SchedulerItem, SchedulerItemData } from './scheduler-item';
import { MatDialog } from '@angular/material';
import { WriteSchedulerItemDialogComponent } from './write-scheduler-item-dialog/write-scheduler-item-dialog.component';
import { GoogleUserService } from '../google-user/google-user.service';

@Component({
  selector: 'app-scheduler',
  templateUrl: './scheduler.component.html',
  styleUrls: ['./scheduler.component.css']
})
export class SchedulerComponent implements OnInit {

  schedulerItems: SchedulerItem[];

  constructor(private schedulerNetworkService: SchedulerNetworkService,
              private googleUserService: GoogleUserService,
              private dialog: MatDialog) { }

  ngOnInit() {
    this.googleUserService.doTaskAfterSignedIn(() =>
      this.schedulerNetworkService.loadSchedulerItems(items => this.schedulerItems = items));
  }

  /**
   * Open the add scheduler item dialog.
   */
  openAddItemDialog(): void {
    const data: SchedulerItemData = {
      keyString: null,
      description: null,
      deadline: null
    };
    this.openWriteItemDialog(data);
  }

  /**
   * Open an write scheduler item dialog.
   *
   * @param {SchedulerItemData} schedulerItemData the item to be edited.
   */
  openWriteItemDialog(schedulerItemData: SchedulerItemData) {
    if (schedulerItemData.keyString !== null) {
      schedulerItemData = { ...schedulerItemData } as SchedulerItemData;
    }
    this.dialog.open(WriteSchedulerItemDialogComponent, { data: schedulerItemData }).afterClosed()
      .subscribe(value => {
        if (value === null || value === undefined) {
          return;
        }
        schedulerItemData = value as SchedulerItemData;
        this.schedulerNetworkService.writeSchedulerItem(schedulerItemData,
          items => this.schedulerItems = items);
      });
  }

  /**
   * Delete an scheduler item.
   *
   * @param {SchedulerItem} schedulerItem the item to be deleted.
   */
  deleteItem(schedulerItem: SchedulerItem) {
    this.schedulerNetworkService.deleteSchedulerItem(schedulerItem.keyString,
      items => this.schedulerItems = items);
  }

  /**
   * Mark as completed or uncompleted.
   *
   * @param {boolean} completed true => completed, false => uncompleted.
   * @param {SchedulerItem} schedulerItem the item to be marked.
   */
  markAs(completed: boolean, schedulerItem: SchedulerItem) {
    this.schedulerNetworkService.markSchedulerItem(completed, schedulerItem.keyString,
      items => this.schedulerItems = items);
  }

}
