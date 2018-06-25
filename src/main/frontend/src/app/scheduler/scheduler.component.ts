import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { GoogleUserService } from '../google-user/google-user.service';
import { SchedulerItem } from './scheduler-item';
import { SchedulerNetworkService } from './scheduler-network.service';
import { WriteSchedulerItemDialogComponent } from './write-scheduler-item-dialog/write-scheduler-item-dialog.component';

@Component({
  selector: 'app-scheduler',
  templateUrl: './scheduler.component.html',
  styleUrls: ['./scheduler.component.css']
})
export class SchedulerComponent implements OnInit {

  items: SchedulerItem[] = [];

  constructor(private networkService: SchedulerNetworkService,
              private googleUserService: GoogleUserService,
              private dialog: MatDialog) {
  }

  ngOnInit() {
    this.googleUserService.afterSignedIn(() => setTimeout(() => {
      this.networkService.loadItems(items => this.items =
        items.map(i => new SchedulerItem(i)))
    }, 50));
  }

  editItem(item?: SchedulerItem) {
    const toBeEdited = item == null ? new SchedulerItem() : new SchedulerItem(item);
    this.dialog.open(WriteSchedulerItemDialogComponent, { data: toBeEdited })
      .afterClosed()
      .subscribe(value => {
        if (value == null) {
          return;
        }
        const edited = value as SchedulerItem;
        const handler = key => {
          const itemWithOldRemoved = this.items.filter(i => i.key !== item.key);
          itemWithOldRemoved.push(new SchedulerItem(<SchedulerItem>{ ...edited, key: key }));
          this.items = itemWithOldRemoved.sort((a, b) => a.deadline - b.deadline);
        };
        this.networkService.editItem(edited, handler);
      });
  }

  deleteItem(item: SchedulerItem) {
    this.networkService.deleteItem(item.key, () =>
      this.items = this.items.filter(i => i.key !== item.key));
  }

  markAs(completed: boolean, item: SchedulerItem) {
    this.networkService.markAs(completed, item.key, () => item.isCompleted = completed);
  }

}
