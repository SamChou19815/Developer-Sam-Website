import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { MatDrawer } from '@angular/material';
import { Title } from '@angular/platform-browser';
import { NavigationStart, Router, RouterEvent } from '@angular/router';
import { allData, NavData } from './nav/nav-data';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  dataList = allData;
  title = 'Developer Sam';
  @ViewChild('drawer') private drawer: MatDrawer | undefined;

  constructor(private titleService: Title,
              private changeDetector: ChangeDetectorRef,
              private router: Router) {
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

  // noinspection JSMethodCanBeStatic
  get mode(): string {
    return window.innerWidth >= 800 ? 'side' : 'over';
  }

  // noinspection JSMethodCanBeStatic
  get sideNavInitiallyOpened(): boolean {
    return window.innerWidth >= 800;
  }

}
