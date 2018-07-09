import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { AutoSchedulingComponent } from './auto-scheduling/auto-scheduling.component';
import { EditorDialogComponent as EEditor } from './events/editor-dialog/editor-dialog.component';
import { EventComponent } from './events/event/event.component';
import { EventsComponent } from './events/events.component';
import { EditorDialogComponent as PEditor } from './projects/editor-dialog/editor-dialog.component';
import { ProjectComponent } from './projects/project/project.component';
import { ProjectsComponent } from './projects/projects.component';
import { SchedulerRoutingModule } from './scheduler-routing.module';

@NgModule({
  imports: [SharedModule, SchedulerRoutingModule],
  exports: [],
  declarations: [ProjectsComponent, ProjectComponent, EventsComponent, AutoSchedulingComponent,
    EventComponent, PEditor, EEditor],
  providers: [],
  entryComponents: [PEditor, EEditor]
})
export class SchedulerModule {
}
