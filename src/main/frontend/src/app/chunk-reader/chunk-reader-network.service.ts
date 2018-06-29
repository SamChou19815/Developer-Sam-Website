import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoadingOverlayService } from '../overlay/loading-overlay.service';
import { AnalyzedArticle, FullAnalyzedArticle, RawArticle } from './articles';

@Injectable()
export class ChunkReaderNetworkService {

  constructor(private http: HttpClient, private loadingService: LoadingOverlayService) {
  }

  async loadArticlesPreview(): Promise<AnalyzedArticle[]> {
    const token = localStorage.getItem('token');
    if (token == null) {
      throw new Error();
    }
    return this.http.get<AnalyzedArticle[]>('/apis/user/chunkreader/load', {
      withCredentials: true, headers: { 'Firebase-Auth-Token': token }
    }).toPromise();
  }

  async loadArticleDetail(key: string): Promise<FullAnalyzedArticle> {
    const token = localStorage.getItem('token');
    if (token == null) {
      throw new Error();
    }
    const url = `/apis/user/chunkreader/article_detail?key=${key}`;
    return this.http.get<FullAnalyzedArticle>(url, {
      withCredentials: true, headers: { 'Firebase-Auth-Token': token }
    }).toPromise();
  }

  async adjustSummary(key: string, limit: number): Promise<string[]> {
    const token = localStorage.getItem('token');
    if (token == null) {
      throw new Error();
    }
    const url = `/apis/user/chunkreader/adjust_summary?key=${key}&limit=${limit}`;
    return this.http.post<string[]>(url, {}, {
      withCredentials: true, headers: { 'Firebase-Auth-Token': token }
    }).toPromise()
  }

  async analyzeArticle(rawArticle: RawArticle): Promise<boolean> {
    const token = localStorage.getItem('token');
    if (token == null) {
      throw new Error();
    }
    const resp = await this.http.post('/apis/user/chunkreader/analyze', rawArticle, {
      responseType: 'text', withCredentials: true, headers: { 'Firebase-Auth-Token': token }
    }).toPromise();
    return resp === 'true';
  }

}
