import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { SchedulerItemComponent } from './scheduler-item/scheduler-item.component';
import { SchedulerRoutingModule } from './scheduler-routing.module';

import { SchedulerComponent } from './scheduler.component';
import { WriteSchedulerItemDialogComponent } from './write-scheduler-item-dialog/write-scheduler-item-dialog.component';

@NgModule({
  imports: [
    SharedModule,
    SchedulerRoutingModule
  ],
  exports: [SchedulerComponent],
  declarations: [SchedulerComponent, SchedulerItemComponent, WriteSchedulerItemDialogComponent],
  providers: [],
  entryComponents: [WriteSchedulerItemDialogComponent]
})
export class SchedulerModule {
}
