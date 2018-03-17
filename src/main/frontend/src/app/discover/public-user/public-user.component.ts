import { Component, Input, OnInit } from '@angular/core';
import { PublicUser } from "../public-users";

@Component({
  selector: 'app-public-user',
  templateUrl: './public-user.component.html',
  styleUrls: ['./public-user.component.css']
})
export class PublicUserComponent implements OnInit {

  @Input() user: PublicUser;

  constructor() { }

  ngOnInit() { }

  /**
   * Compute and returns the GitHub URL of the user.
   *
   * @returns {string} the GitHub URL of the user.
   */
  get githubURL(): string {
    return "https://github.com/" + this.user.githubAccount;
  }

}
