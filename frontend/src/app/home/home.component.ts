import { Component, OnInit } from '@angular/core';
import { Icon } from '../shared/icon';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  readonly githubIcon: Icon = Icon.ofFontAwesome('github');

  constructor() { }

  ngOnInit() {
  }

}
