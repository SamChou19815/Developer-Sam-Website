import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { AutoSchedulingComponent } from './auto-scheduling/auto-scheduling.component';
import { EditorDialogComponent as EEditor } from './events/editor-dialog/editor-dialog.component';
import { EventsComponent } from './events/events.component';
import { EditorDialogComponent as PEditor } from './projects/editor-dialog/editor-dialog.component';
import { ProjectComponent } from './projects/project/project.component';
import { ProjectsComponent } from './projects/projects.component';
import { SchedulerRoutingModule } from './scheduler-routing.module';
import { OneTimeEventComponent } from './events/one-time-event/one-time-event.component';
import { WeeklyEventComponent } from './events/weekly-event/weekly-event.component';
import { TaggedIntervalComponent } from './auto-scheduling/tagged-interval/tagged-interval.component';

@NgModule({
  imports: [SharedModule, SchedulerRoutingModule],
  exports: [],
  declarations: [ProjectsComponent, ProjectComponent, EventsComponent, AutoSchedulingComponent,
    PEditor, EEditor, OneTimeEventComponent, WeeklyEventComponent, TaggedIntervalComponent],
  providers: [],
  entryComponents: [PEditor, EEditor]
})
export class SchedulerModule {
}
