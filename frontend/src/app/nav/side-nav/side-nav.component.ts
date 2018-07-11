import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NavData, NavGroup, NavItem } from '../nav-data';

@Component({
  selector: 'app-nav-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.css']
})
export class SideNavComponent implements OnInit {

  @Input() navDataList: NavData[] = [];
  @Output() navClicked = new EventEmitter<undefined>();

  /**
   * [isItem] checks whether the data is an item.
   * @param {NavData} data the given data.
   * @returns {boolean} whether the data is an item.
   */
  isItem = (data: NavData) => data.hasOwnProperty('link');

  /**
   * [isGroup] checks whether the data is a group.
   * @param {NavData} data the given data.
   * @returns {boolean} whether the data is a group.
   */
  isGroup = (data: NavData) => data.hasOwnProperty('children');

  /**
   * Returns the data as a nav item.
   * @param {NavData} data the given data.
   * @returns {NavItem} the data as a nav item.
   */
  getItem = (data: NavData) => data as NavItem;

  /**
   * Returns the data as a nav group.
   * @param {NavData} data the given data.
   * @returns {NavGroup} the data as a nav group.
   */
  getGroup = (data: NavData) => data as NavGroup;

  constructor() {
  }

  ngOnInit() {
  }

}
