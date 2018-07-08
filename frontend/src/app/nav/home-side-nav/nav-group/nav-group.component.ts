import { Component, Input, OnInit } from '@angular/core';
import { Icon } from '../../../shared/icon';
import { NavGroup } from '../../nav-data';

@Component({
  selector: 'app-nav-group',
  templateUrl: './nav-group.component.html',
  styleUrls: ['./nav-group.component.css']
})
export class NavGroupComponent implements OnInit {

  shown = true;
  @Input() group: NavGroup = { name: '', icon: Icon.ofDummy, children: [] };

  constructor() {
  }

  ngOnInit() {
  }

}
