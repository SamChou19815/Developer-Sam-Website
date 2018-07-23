import { Injectable } from '@angular/core';
import { Icon } from '../shared/icon';
import { NavData, NavDataList, NavGroup, NavItem } from './nav-data';

@Injectable({
  providedIn: 'root'
})
export class NavDataService {

  /**
   * The item for home page.
   * @type {NavItem}
   */
  private readonly homeNavItem: NavItem = <NavItem>{
    name: 'Developer Sam', icon: Icon.ofMaterial('home'), link: '/'
  };
  /**
   * The group for scheduler.
   * @type {NavGroup}
   */
  private readonly schedulerNavGroup: NavGroup = <NavGroup>{
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
  };
  /**
   * Group for RSS Reader.
   * @type {NavGroup}
   */
  private readonly rssReaderGroup: NavGroup = <NavGroup>{
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
  };
  /**
   * The item for chunk reader.
   * @type {NavItem}
   */
  private readonly chunkReaderItem: NavItem = <NavItem>{
    name: 'Chunk Reader', icon: Icon.ofMaterial('speaker_notes'), link: '/chunk_reader'
  };
  /**
   * The group for playground
   * @type {NavGroup}
   */
  private readonly playgroundGroup: NavGroup = <NavGroup>{
    name: 'Playground', icon: Icon.ofMaterial('apps'),
    children: [
      { name: 'TEN', icon: Icon.ofMaterial('grid_on'), link: '/playground/ten' }
    ]
  };
  /**
   * The nav data list for home.
   * @type {NavDataList}
   */
  private readonly navDataListForHome: NavDataList = new NavDataList(<NavData[]>[
    this.schedulerNavGroup, this.rssReaderGroup, this.chunkReaderItem, this.playgroundGroup
  ]);
  /**
   * The nav data list for apps.
   * @type {NavDataList}
   */
  private readonly navDataListForApps: NavDataList = new NavDataList(<NavData[]>[
    this.homeNavItem, this.schedulerNavGroup, this.rssReaderGroup,
    this.chunkReaderItem, this.playgroundGroup
  ]);

  constructor() {
  }

  /**
   * Returns the nav data depending on the location of the user.
   *
   * @param {boolean} isHome whether the user is in home.
   * @returns {NavDataList} the nav data depending on the location of the user.
   */
  getNavData(isHome: boolean): NavDataList {
    return isHome ? this.navDataListForHome : this.navDataListForApps;
  }

}
