import { Icon } from '../shared/icon';

/**
 * [BaseNavItem] defines the common attribute shared by all nav related items.
 */
interface BaseNavData {
  /**
   * Name of the nav item displayed to the user.
   */
  readonly name: string;
  /**
   * Material Icon of the nav item (material-icon) displayed to the user.
   */
  readonly icon: Icon;
}

/**
 * [NavItem] defines the properties of a navigation item.
 */
export interface NavItem extends BaseNavData {
  /**
   * The actual link of the item, which can be internal or external.
   */
  readonly link: string;
  /**
   * Whether this link is pointing to an internal resource.
   */
  readonly isInternal: boolean;
}

/**
 * [NavGroup] defines the properties of a navigation group, which contains a list of [NavItem]
 * as its children.
 */
export interface NavGroup extends BaseNavData {
  /**
   * A list of child nav items. The first one is the default nav-item in this group.
   */
  readonly children: NavItem[];
}

/**
 * [NavData] is either a [NavItem] or a [NavGroup].
 */
export type NavData = NavItem | NavGroup;

/**
 * [navDataList] is a collection of all nav related data.
 * @type {NavData[]}
 */
export const navDataList: NavData[] = [
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
];

export namespace NavData {

  /**
   * Computes and returns the map from urls to names.
   *
   * @returns {Map<string, string>} the map from urls to names.
   */
  function computeNames(): Map<string, string> {
    const map = new Map<string, string>();
    for (const data of navDataList) {
      if (data.hasOwnProperty('link')) {
        const item = <NavItem>data;
        map.set(item.link, item.name);
      } else if (data.hasOwnProperty('children')) {
        const group = <NavGroup>data;
        for (const child of group.children) {
          map.set(child.link, `${group.name} - ${child.name}`);
        }
      } else {
        throw new Error();
      }
    }
    return map;
  }

  /**
   * The map that maps urls to names.
   * @type {Map<string, string>}
   */
  const nameMap: Map<string, string> = computeNames();

  /**
   * Returns the name of the nav data with respect to the url.
   * @param {string} url url to search.
   * @returns {string} the name of the nav data with respect to the url.
   */
  export function getNameByUrl(url: string): string {
    const nameOpt = nameMap.get(url);
    return nameOpt ? nameOpt : 'Developer Sam';
  }

}
