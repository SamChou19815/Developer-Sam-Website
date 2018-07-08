import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { ItemComponent } from './item/item.component';
import { SchedulerRoutingModule } from './scheduler-routing.module';

import { SchedulerComponent } from './scheduler.component';
import { EditItemDialogComponent } from './edit-item-dialog/edit-item-dialog.component';

@NgModule({
  imports: [
    SharedModule,
    SchedulerRoutingModule
  ],
  exports: [SchedulerComponent],
  declarations: [SchedulerComponent, ItemComponent, EditItemDialogComponent],
  providers: [],
  entryComponents: [EditItemDialogComponent]
})
export class SchedulerModule {
}
