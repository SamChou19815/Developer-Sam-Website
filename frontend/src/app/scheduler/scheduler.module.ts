import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { AutoSchedulingComponent } from './auto-scheduling/auto-scheduling.component';
import { EventsComponent } from './events/events.component';
import { EditorDialogComponent } from './projects/editor-dialog/editor-dialog.component';
import { ProjectComponent } from './projects/project/project.component';
import { ProjectsComponent } from './projects/projects.component';
import { SchedulerRoutingModule } from './scheduler-routing.module';

@NgModule({
  imports: [
    SharedModule,
    SchedulerRoutingModule
  ],
  exports: [],
  declarations: [ProjectsComponent, ProjectComponent, EditorDialogComponent,
    EventsComponent, AutoSchedulingComponent],
  providers: [],
  entryComponents: [EditorDialogComponent]
})
export class SchedulerModule {
}
