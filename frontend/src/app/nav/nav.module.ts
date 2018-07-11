import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { SideNavPageComponent } from './side-nav-page/side-nav-page.component';
import { NavGroupComponent } from './nav-group/nav-group.component';
import { NavItemComponent } from './nav-item/nav-item.component';

@NgModule({
  imports: [SharedModule, RouterModule],
  exports: [SideNavPageComponent],
  declarations: [NavItemComponent, NavGroupComponent, SideNavPageComponent]
})
export class NavModule {
}
