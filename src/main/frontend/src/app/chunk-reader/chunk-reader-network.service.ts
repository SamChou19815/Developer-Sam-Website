import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatDialog } from "@angular/material";
import { AlertComponent } from "../alert/alert.component";
import { LoadingOverlayService } from '../overlay/loading-overlay.service';
import { AnalyzedArticle, FullAnalyzedArticle, RawArticle } from './articles';

@Injectable()
export class ChunkReaderNetworkService {

  constructor(private http: HttpClient, private loadingService: LoadingOverlayService,
              private dialog: MatDialog) {
  }

  loadArticlesPreview(success: (articles: AnalyzedArticle[]) => void): void {
    const token = localStorage.getItem('token');
    if (token == null) {
      throw new Error();
    }
    const ref = this.loadingService.open();
    this.http.get<AnalyzedArticle[]>('/apis/user/chunkreader/load', {
      withCredentials: true, headers: { 'Firebase-Auth-Token': token }
    }).subscribe(articles => {
      ref.close();
      success(articles);
    });
  }

  loadArticleDetail(key: string, success: (article: FullAnalyzedArticle) => void): void {
    const token = localStorage.getItem('token');
    if (token == null) {
      throw new Error();
    }
    const ref = this.loadingService.open();
    const url = `/apis/user/chunkreader/article_detail?key=${key}`;
    this.http.get<FullAnalyzedArticle>(url, {
      withCredentials: true, headers: { 'Firebase-Auth-Token': token }
    }).subscribe(a => {
      ref.close();
      success(a);
    });
  }

  adjustSummary(key: string, limit: number, success: (summaries: string[]) => void): void {
    const token = localStorage.getItem('token');
    if (token == null) {
      throw new Error();
    }
    const url = `/apis/user/chunkreader/adjust_summary?key=${key}&limit=${limit}`;
    const ref = this.loadingService.open();
    this.http.post<string[]>(url, {}, {
      withCredentials: true, headers: { 'Firebase-Auth-Token': token }
    }).subscribe(s => {
      ref.close();
      success(s);
    });
  }

  analyzeArticle(rawArticle: RawArticle): void {
    const token = localStorage.getItem('token');
    if (token == null) {
      throw new Error();
    }
    const ref = this.loadingService.open();
    this.http.post('/apis/user/chunkreader/analyze', rawArticle, {
      responseType: 'text', withCredentials: true, headers: { 'Firebase-Auth-Token': token }
    }).subscribe(resp => {
      ref.close();
      const message = resp === 'true'
        ? `Your article is being analyzed right now. Refresh the page later to see its analysis.`
        : `Sorry, your article cannot be analyzed for some unknown reasons.
          The failure has been logged in the system and we will try to figure out why.`;
      this.dialog.open(AlertComponent, { data: message });
    });
  }

}
