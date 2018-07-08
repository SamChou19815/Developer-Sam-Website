import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { AutoSchedulingComponent } from './auto-scheduling/auto-scheduling.component';
import * as Events from './events/editor-dialog/editor-dialog.component';
import { EventComponent } from './events/event/event.component';
import { EventsComponent } from './events/events.component';
import * as Projects from './projects/editor-dialog/editor-dialog.component';
import { ProjectComponent } from './projects/project/project.component';
import { ProjectsComponent } from './projects/projects.component';
import { SchedulerRoutingModule } from './scheduler-routing.module';

@NgModule({
  imports: [
    SharedModule,
    SchedulerRoutingModule
  ],
  exports: [],
  declarations: [ProjectsComponent, ProjectComponent, EventsComponent, AutoSchedulingComponent,
    EventComponent, Projects.EditorDialogComponent, Events.EditorDialogComponent],
  providers: [],
  entryComponents: [Projects.EditorDialogComponent, Events.EditorDialogComponent]
})
export class SchedulerModule {
}
