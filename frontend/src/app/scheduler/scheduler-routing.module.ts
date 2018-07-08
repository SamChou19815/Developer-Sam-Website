import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AutoSchedulingComponent } from './auto-scheduling/auto-scheduling.component';
import { EventsComponent } from './events/events.component';
import { ProjectsComponent } from './projects/projects.component';

const routes = [
  { path: '', component: ProjectsComponent },
  { path: 'projects', component: ProjectsComponent },
  { path: 'events', component: EventsComponent },
  { path: 'auto', component: AutoSchedulingComponent },
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [RouterModule],
  declarations: []
})
export class SchedulerRoutingModule {
}
