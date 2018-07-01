import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { HomeSideNavComponent } from './home-side-nav/home-side-nav.component';
import { NavGroupComponent } from './home-side-nav/nav-group/nav-group.component';
import { NavItemComponent } from './home-side-nav/nav-item/nav-item.component';

@NgModule({
  imports: [SharedModule, RouterModule, CommonModule],
  exports: [HomeSideNavComponent],
  declarations: [HomeSideNavComponent, NavItemComponent, NavGroupComponent]
})
export class NavModule {
}
