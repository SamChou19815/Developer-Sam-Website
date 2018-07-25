import { Component, Input, OnInit } from '@angular/core';
import { ProjectCardData } from './project-card-data';

@Component({
  selector: 'app-home-project-card',
  templateUrl: './project-card.component.html',
  styleUrls: ['./project-card.component.css']
})
export class ProjectCardComponent implements OnInit {

  /**
   * The data to display.
   * @type {ProjectCardData}
   */
  @Input() data: ProjectCardData = <ProjectCardData>{ name: '', logo: '' };

  constructor() {
  }

  ngOnInit() {
  }

}
