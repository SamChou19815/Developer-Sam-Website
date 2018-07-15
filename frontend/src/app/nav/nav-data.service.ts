import { Injectable } from '@angular/core';
import { Icon } from '../shared/icon';
import { NavData, NavDataList, NavGroup, NavItem } from './nav-data';

@Injectable({
  providedIn: 'root'
})
export class NavDataService {

  /**
   * [navDataList] is a collection of all nav related data.
   * @type {NavDataList}
   */
  readonly navDataList: NavDataList = new NavDataList(<NavData[]>[
    <NavItem>{
      name: 'Developer Sam', icon: Icon.ofMaterial('home'),
      link: '/', isInternal: true
    },
    <NavItem>{
      name: 'Friends', icon: Icon.ofMaterial('group'), link: '/friends', isInternal: true
    },
    <NavGroup>{
      name: 'Scheduler', icon: Icon.ofMaterial('event_note'),
      children: [
        {
          name: 'Projects', icon: Icon.ofMaterial('event_available'),
          link: '/scheduler/projects', isInternal: true
        },
        {
          name: 'Events', icon: Icon.ofMaterial('event'),
          link: '/scheduler/events', isInternal: true
        },
        {
          name: 'Auto Scheduling', icon: Icon.ofMaterial('dashboard'),
          link: '/scheduler/auto', isInternal: true
        }
      ]
    },
    <NavGroup>{
      name: 'Rss Reader', icon: Icon.ofMaterial('chrome_reader_mode'),
      children: [
        {
          name: 'Articles', icon: Icon.ofMaterial('library_books'),
          link: '/rss_reader/articles', isInternal: true
        },
        {
          name: 'Subscriptions', icon: Icon.ofMaterial('rss_feed'),
          link: '/rss_reader/subscriptions', isInternal: true
        },
      ]
    },
    <NavItem>{
      name: 'Chunk Reader', icon: Icon.ofMaterial('speaker_notes'),
      link: '/chunkreader', isInternal: true
    },
    <NavGroup>{
      name: 'Playground', icon: Icon.ofMaterial('apps'),
      children: [
        {
          name: 'TEN', icon: Icon.ofMaterial('grid_on'),
          link: '/playground/ten', isInternal: true
        }
      ]
    },
    <NavItem>{
      name: 'Blog', icon: Icon.ofMaterial('language'),
      link: 'https://blog.developersam.com/', isInternal: false
    },
    <NavItem>{
      name: 'Open Source', icon: Icon.ofFontAwesome('github'),
      link: 'https://github.com/SamChou19815', isInternal: false
    }
  ]);

  constructor() {
  }

}
