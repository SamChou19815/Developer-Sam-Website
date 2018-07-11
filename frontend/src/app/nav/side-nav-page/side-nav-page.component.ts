import {
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
import { NavData, navDataList } from '../nav-data';

@Component({
  selector: 'app-nav-side-nav-page',
  templateUrl: './side-nav-page.component.html',
  styleUrls: ['./side-nav-page.component.css']
})
export class SideNavPageComponent implements OnInit {

  navDataList = navDataList;
  title = navDataList[0].name;
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

  /**
   * Mode for the drawer.
   *
   * @returns {string}
   */
  get mode(): string {
    return this.windowWidth >= 800 ? 'side' : 'over';
  }

  /**
   * Whether the side nav should be open initially.
   *
   * @returns {boolean}
   */
  get sideNavInitiallyOpened(): boolean {
    return this.windowWidth >= 800;
  }

  /**
   * Handle clicking nav.
   *
   * @returns {Promise<void>} dummy return value.
   */
  async clickNav(): Promise<void> {
    if (window.innerWidth < 800 && this.drawer != null) {
      await this.drawer.close();
    }
  }

  /**
   * Handle resizing.
   */
  @HostListener('window:resize', ['$event'])
  onResize(): void {
    this.windowWidth = window.innerWidth;
  }

}
