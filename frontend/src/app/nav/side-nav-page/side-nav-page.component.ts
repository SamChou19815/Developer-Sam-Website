import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  HostListener,
  OnInit,
  ViewChild
} from '@angular/core';
import { MatDrawer } from '@angular/material';
import { Title } from '@angular/platform-browser';
import { NavigationStart, Router, RouterEvent } from '@angular/router';
import { ignore } from '../../shared/util';
import { NavDataList } from '../nav-data';
import { NavDataService } from '../nav-data.service';

@Component({
  selector: 'app-nav-side-nav-page',
  templateUrl: './side-nav-page.component.html',
  styleUrls: ['./side-nav-page.component.css']
})
export class SideNavPageComponent implements OnInit, AfterViewInit {

  /**
   * Title displayed at the top.
   * @type {string}
   */
  title: string;
  /**
   * Whether the page is home page.
   * @type {boolean}
   */
  isHome = true;
  /**
   * Current width of the window.
   */
  private windowWidth: number;
  /**
   * The reference to the drawer.
   */
  @ViewChild('drawer') private drawer: MatDrawer | undefined;

  constructor(private titleService: Title,
              private navDataService: NavDataService,
              private changeDetector: ChangeDetectorRef,
              private router: Router) {
    this.title = this.navDataList.list[0].name;
    this.windowWidth = window.innerWidth;
  }

  ngOnInit() {
    this.router.events.subscribe(e => {
      if (e instanceof RouterEvent && e instanceof NavigationStart) {
        let currentUrl = e.url;
        const hashTagIndex = currentUrl.indexOf('#');
        if (hashTagIndex !== -1) {
          currentUrl = currentUrl.substring(0, hashTagIndex);
        }
        let title: string;
        if (currentUrl === '/') {
          title = 'Developer Sam';
          this.isHome = true;
        } else {
          title = this.navDataList.getNameByUrl(currentUrl);
          this.isHome = false;
        }
        this.titleService.setTitle(title);
        this.title = title;
      }
    });
  }

  ngAfterViewInit() {
    this.changeDetector.detectChanges();
  }

  /**
   * Returns the nav data list for display.
   *
   * @returns {NavDataList} the nav data list for display.
   */
  get navDataList(): NavDataList {
    return this.navDataService.navDataList;
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
   * Handle clicking nav.
   */
  clickNav(): void {
    if (this.drawer != null) {
      this.drawer.close().then(ignore);
    }
  }

  /**
   * Handle resizing.
   */
  @HostListener('window:resize', ['$event'])
  onResize(): void {
    this.windowWidth = window.innerWidth;
    this.changeDetector.detectChanges();
  }

  /**
   * Back to the home page.
   */
  backToHome(): void {
    this.router.navigateByUrl('/').then(ignore);
  }

}
