import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  HostListener,
  Input,
  OnInit,
  ViewChild
} from '@angular/core';
import { MatDrawer } from '@angular/material';
import { Title } from '@angular/platform-browser';
import { NavigationStart, Router, RouterEvent } from '@angular/router';
import { NavData, navDataList, NavGroup, NavItem } from '../nav-data';

@Component({
  selector: 'app-nav-side-nav-page',
  templateUrl: './side-nav-page.component.html',
  styleUrls: ['./side-nav-page.component.css']
})
export class SideNavPageComponent implements OnInit, AfterViewInit {

  /**
   * Exported navigation data list.
   * @type {NavData[]}
   */
  readonly navDataList = navDataList;
  /**
   * Title displayed at the top.
   * @type {string}
   */
  title = navDataList[0].name;
  /**
   * Current width of the window.
   */
  private windowWidth: number;

  /**
   * The title for the side nav.
   * @type {string}
   */
  @Input() sideNavTitle = '';

  /**
   * The reference to the drawer.
   */
  @ViewChild('drawer') private drawer: MatDrawer | undefined;

  /**
   * [isItem] checks whether the data is an item.
   *
   * @param {NavData} data the given data.
   * @returns {boolean} whether the data is an item.
   */
  readonly isItem = (data: NavData) => data.hasOwnProperty('link');

  /**
   * [isGroup] checks whether the data is a group.
   *
   * @param {NavData} data the given data.
   * @returns {boolean} whether the data is a group.
   */
  readonly isGroup = (data: NavData) => data.hasOwnProperty('children');

  /**
   * Returns the data as a nav item.
   *
   * @param {NavData} data the given data.
   * @returns {NavItem} the data as a nav item.
   */
  readonly getItem = (data: NavData) => data as NavItem;

  /**
   * Returns the data as a nav group.
   *
   * @param {NavData} data the given data.
   * @returns {NavGroup} the data as a nav group.
   */
  readonly getGroup = (data: NavData) => data as NavGroup;

  constructor(private titleService: Title,
              private changeDetector: ChangeDetectorRef,
              private router: Router) {
    this.windowWidth = window.innerWidth;
  }

  ngOnInit() {
    this.router.events.subscribe((e) => {
      if (e instanceof RouterEvent && e instanceof NavigationStart) {
        const currentUrl = e.url;
        const title = NavData.getNameByUrl(currentUrl);
        this.titleService.setTitle(title);
        this.title = title;
      }
    });
  }

  ngAfterViewInit() {
    this.changeDetector.detectChanges();
  }

  /**
   * Returns whether the screen is wide enough.
   *
   * @returns {boolean} whether the screen is wide enough.
   */
  private get isScreenWide(): boolean {
    return this.windowWidth >= 600;
  }

  /**
   * Mode for the drawer.
   *
   * @returns {string}
   */
  get mode(): string {
    return this.isScreenWide ? 'side' : 'over';
  }

  /**
   * Whether the side nav should be open initially.
   *
   * @returns {boolean}
   */
  get sideNavInitiallyOpened(): boolean {
    return this.isScreenWide;
  }

  /**
   * Handle clicking nav.
   */
  clickNav(): void {
    (async () => {
      if (!this.isScreenWide && this.drawer != null) {
        await this.drawer.close();
      }
    })();
  }

  /**
   * Handle resizing.
   */
  @HostListener('window:resize', ['$event'])
  onResize(): void {
    this.windowWidth = window.innerWidth;
    this.changeDetector.detectChanges();
  }

}
