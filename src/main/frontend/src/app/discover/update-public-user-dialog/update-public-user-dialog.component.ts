import { Component, OnInit } from '@angular/core';
import { PublicUserData } from "../public-users";

@Component({
  selector: 'app-update-public-user-dialog',
  templateUrl: './update-public-user-dialog.component.html',
  styleUrls: ['./update-public-user-dialog.component.css']
})
export class UpdatePublicUserDialogComponent implements OnInit {

  publicUserData: PublicUserData = {
    githubAccount: null
  };

  constructor() { }

  ngOnInit() {}

}
