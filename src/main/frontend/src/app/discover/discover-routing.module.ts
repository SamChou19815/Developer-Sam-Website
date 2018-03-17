import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { DiscoverComponent } from "./discover.component";

const routes = [
  { path: '', component: DiscoverComponent }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [RouterModule],
  declarations: []
})
export class DiscoverRoutingModule {

}
