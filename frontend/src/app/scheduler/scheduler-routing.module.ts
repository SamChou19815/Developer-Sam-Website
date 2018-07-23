import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AutoSchedulingComponent } from './auto-scheduling/auto-scheduling.component';
import { EventsComponent } from './events/events.component';
import { ProjectsComponent } from './projects/projects.component';

const routes: Routes = [
  { path: '', redirectTo: 'projects' },
  { path: 'projects', component: ProjectsComponent },
  { path: 'events', component: EventsComponent },
  { path: 'auto', component: AutoSchedulingComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  declarations: []
})
export class SchedulerRoutingModule {
}
