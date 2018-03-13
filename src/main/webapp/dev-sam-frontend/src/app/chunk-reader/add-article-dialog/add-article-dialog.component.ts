import { Component, OnInit } from '@angular/core';
import { RawArticle } from '../articles';

@Component({
  selector: 'app-add-article-dialog',
  templateUrl: './add-article-dialog.component.html',
  styleUrls: ['./add-article-dialog.component.css']
})
export class AddArticleDialogComponent implements OnInit {

  readonly rawArticle: RawArticle;

  constructor() {
    this.rawArticle = { title: '', content: '' };
  }

  ngOnInit() { }

}
