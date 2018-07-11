import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { SideNavComponent } from './side-nav/side-nav.component';
import { NavGroupComponent } from './side-nav/nav-group/nav-group.component';
import { NavItemComponent } from './side-nav/nav-item/nav-item.component';
import { SideNavPageComponent } from './side-nav-page/side-nav-page.component';

@NgModule({
  imports: [SharedModule, RouterModule],
  exports: [SideNavPageComponent],
  declarations: [SideNavComponent, NavItemComponent, NavGroupComponent, SideNavPageComponent]
})
export class NavModule {
}
