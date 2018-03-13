import { NgModule } from '@angular/core';
import { SchedulerComponent } from './scheduler.component';
import { RouterModule } from '@angular/router';

const routes = [
  { path: '', component: SchedulerComponent }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [RouterModule],
  declarations: []
})
export class SchedulerRoutingModule {}
