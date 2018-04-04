import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ViewChild
} from '@angular/core';
import { MatDrawer } from '@angular/material';
import { Title } from '@angular/platform-browser';

/**
 * A collection of currently supported nav element names.
 */
export enum NavElementName {
  Home = 'Developer Sam', Scheduler = 'Scheduler',
  ChunkReader = 'Chunk Reader (Beta V2)', TEN = 'TEN'
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements AfterViewInit {

  /**
   * Just to use the enum to template.
   * @type {NavElementName}
   */
  NavElementName = NavElementName;
  /**
   * The drawer embed in the component.
   */
  @ViewChild('drawer') private drawer: MatDrawer;
  /**
   * Title of the component.
   * @type {string}
   */
  title = '';
  /**
   * Whether home is selected.
   * @type {boolean}
   */
  homeSelected = false;
  /**
   * Whether scheduler is selected.
   * @type {boolean}
   */
  schedulerSelected = false;
  /**
   * Whether Chunk Reader is selected.
   * @type {boolean}
   */
  chunkReaderSelected = false;
  /**
   * Whether TEN is selected.
   * @type {boolean}
   */
  tenSelected = false;

  /**
   * Initialize itself with the current url.
   *
   * @param {Title} titleService the title service to set web title.
   * @param {ChangeDetectorRef} changeDetector the change detector injected.
   */
  constructor(private titleService: Title,
              private changeDetector: ChangeDetectorRef) {
  }

  /**
   * Select an element to highlight to indicate that it has been selected in
   * the nav bar.
   * @param {string} elementName the name of the element, which can be home,
   * projects, scheduler and TEN.
   */
  select(elementName: NavElementName) {
    let home = false, scheduler = false, ten = false, chunkReader = false;
    switch (elementName) {
      case NavElementName.Home:
        home = true;
        break;
      case NavElementName.Scheduler:
        scheduler = true;
        break;
      case NavElementName.TEN:
        ten = true;
        break;
      case this.NavElementName.ChunkReader:
        chunkReader = true;
        break;
    }
    this.homeSelected = home;
    this.schedulerSelected = scheduler;
    this.chunkReaderSelected = chunkReader;
    this.tenSelected = ten;
    this.titleService.setTitle(elementName as string);
    this.title = elementName as string;
    // noinspection JSIgnoredPromiseFromCall
    this.drawer.close();
  }

  ngAfterViewInit(): void {
    let name: NavElementName;
    switch (window.location.pathname) {
      case '/':
      case '':
        name = NavElementName.Home;
        break;
      case '/scheduler':
        name = NavElementName.Scheduler;
        break;
      case '/chunkreader':
        name = NavElementName.ChunkReader;
        break;
      case '/ten':
        name = NavElementName.TEN;
        break;
      default:
        throw new Error('Unknown Type!');
    }
    this.select(name);
    this.changeDetector.detectChanges();
  }

}
