import { Component, OnInit } from '@angular/core';
import { RawArticle } from '../chunk-reader-data';

@Component({
  selector: 'app-chunk-reader-add-article-dialog',
  templateUrl: './add-article-dialog.component.html',
  styleUrls: ['./add-article-dialog.component.css']
})
export class AddArticleDialogComponent implements OnInit {

  /**
   * Raw article to edit.
   */
  readonly rawArticle: RawArticle;

  constructor() {
    this.rawArticle = { title: '', content: '' };
  }

  ngOnInit() { }

}
