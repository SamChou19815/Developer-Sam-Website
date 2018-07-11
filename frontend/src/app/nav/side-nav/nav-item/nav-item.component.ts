import { Component, Input, OnInit } from '@angular/core';
import { Icon } from '../../../shared/icon';
import { NavItem } from '../../nav-data';

@Component({
  selector: 'app-nav-item',
  templateUrl: './nav-item.component.html',
  styleUrls: ['./nav-item.component.css']
})
export class NavItemComponent implements OnInit {

  @Input() item: NavItem = { name: '', icon: Icon.ofDummy, link: '', isInternal: true };
  @Input() isChild = false;

  constructor() {
  }

  ngOnInit() {
  }

}
