import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';

import { SchedulerComponent } from './scheduler.component';
import { SchedulerRoutingModule } from './scheduler-routing.module';
import { SchedulerNetworkService } from './scheduler-network.service';
import { SchedulerItemComponent } from './scheduler-item/scheduler-item.component';
import { WriteSchedulerItemDialogComponent } from './write-scheduler-item-dialog/write-scheduler-item-dialog.component';

@NgModule({
  imports: [
    SharedModule,
    SchedulerRoutingModule
  ],
  exports: [SchedulerComponent],
  declarations: [SchedulerComponent, SchedulerItemComponent, WriteSchedulerItemDialogComponent],
  providers: [SchedulerNetworkService],
  entryComponents: [WriteSchedulerItemDialogComponent]
})
export class SchedulerModule {}
