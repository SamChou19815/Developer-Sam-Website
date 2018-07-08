import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { EditItemDialogComponent } from './projects/edit-item-dialog/edit-item-dialog.component';
import { ItemComponent } from './projects/item/item.component';
import { ProjectsComponent } from './projects/projects.component';
import { SchedulerRoutingModule } from './scheduler-routing.module';

@NgModule({
  imports: [
    SharedModule,
    SchedulerRoutingModule
  ],
  exports: [],
  declarations: [ProjectsComponent, ItemComponent, EditItemDialogComponent],
  providers: [],
  entryComponents: [EditItemDialogComponent]
})
export class SchedulerModule {
}
