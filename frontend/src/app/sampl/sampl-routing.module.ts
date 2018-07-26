import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SamplInterpreterComponent } from './sampl-interpreter.component';

const routes = [{ path: '', component: SamplInterpreterComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  declarations: []
})
export class SamplRoutingModule {
}
