import { Component, OnInit } from '@angular/core';
import { ProjectCardData } from './project-card/project-card-data';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  /**
   * A list of projects to display.
   * @type {ProjectCardData[]}
   */
  readonly projects: ProjectCardData[] = [
    <ProjectCardData>{
      name: 'Scheduler', logo: '/assets/app-icons/scheduler.png', routerLink: '/scheduler',
      intro: 'A manager and auto-scheduler for your projects and events.'
    },
    <ProjectCardData>{
      name: 'RSS Reader', logo: '/assets/app-icons/rss-reader.png', routerLink: '/rss-reader',
      intro: 'A reader for subscribed RSS feeds. Source of my potential ML data.'
    },
    <ProjectCardData>{
      name: 'SAMPL', logo: '/assets/app-icons/sampl.png', routerLink: '/playground/sampl',
      externalResource: { prompt: 'GitHub Repo', link: 'https://github.com/SamChou19815/sampl' },
      intro: 'A statically-typed functional programming language with basic type inference.'
    },
    <ProjectCardData>{
      name: 'Chunk Reader', logo: '/assets/app-icons/chunk-reader.png',
      routerLink: '/playground/chunk-reader',
      intro: 'A hackathon-winning service to extract key information and summary from text.'
    },
    <ProjectCardData>{
      name: 'TEN', logo: '/assets/app-icons/ten.png', routerLink: '/playground/ten',
      externalResource: { prompt: 'GitHub Repo', link: 'https://github.com/SamChou19815/ten' },
      intro: 'An interesting game with simple rules. Powered by an MCTS AI.'
    },
    <ProjectCardData>{
      name: 'More Projects', logo: '/assets/app-icons/more-projects.png',
      externalResource: { prompt: 'See Them All', link: 'https://github.com/SamChou19815' },
      intro: 'Other open source projects that are not hosted on this website.'
    }
  ];

  constructor() {
  }

  ngOnInit() {
  }

}
