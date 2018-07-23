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
      name: 'Developer Sam', icon: Icon.ofMaterial('home'), link: '/'
    },
    <NavGroup>{
      name: 'Scheduler', icon: Icon.ofMaterial('event_note'),
      children: [
        {
          name: 'Projects', icon: Icon.ofMaterial('event_available'),
          link: '/scheduler/projects'
        },
        {
          name: 'Events', icon: Icon.ofMaterial('event'), link: '/scheduler/events'
        },
        {
          name: 'Friends', icon: Icon.ofMaterial('group'), link: '/friends'
        },
        {
          name: 'Auto Scheduling', icon: Icon.ofMaterial('dashboard'), link: '/scheduler/auto'
        }
      ]
    },
    <NavGroup>{
      name: 'RSS Reader', icon: Icon.ofMaterial('chrome_reader_mode'),
      children: [
        {
          name: 'Articles', icon: Icon.ofMaterial('library_books'),
          link: '/rss_reader/articles'
        },
        {
          name: 'Subscriptions', icon: Icon.ofMaterial('rss_feed'),
          link: '/rss_reader/subscriptions'
        },
      ]
    },
    <NavItem>{
      name: 'Chunk Reader', icon: Icon.ofMaterial('speaker_notes'), link: '/chunk_reader'
    },
    <NavGroup>{
      name: 'Playground', icon: Icon.ofMaterial('apps'),
      children: [
        { name: 'TEN', icon: Icon.ofMaterial('grid_on'), link: '/playground/ten' }
      ]
    }
  ]);

  constructor() {
  }

}
