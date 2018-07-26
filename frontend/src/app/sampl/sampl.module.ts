import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { SamplInterpreterComponent } from './sampl-interpreter.component';
import { SamplRoutingModule } from './sampl-routing.module';

@NgModule({
  imports: [SharedModule, SamplRoutingModule],
  declarations: [SamplInterpreterComponent]
})
export class SamplModule {
}
