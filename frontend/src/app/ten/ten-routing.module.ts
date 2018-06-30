import { NgModule } from '@angular/core';
import { TenComponent } from './ten.component';
import { RouterModule } from '@angular/router';

const routes = [
  { path: '', component: TenComponent }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  declarations: []
})
export class TenRoutingModule {}
