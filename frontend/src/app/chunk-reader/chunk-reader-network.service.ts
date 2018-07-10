import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthenticatedNetworkService } from '../shared/authenticated-network-service';
import { AnalyzedArticle, FullAnalyzedArticle, RawArticle } from './chunk-reader-data';

@Injectable({
  providedIn: 'root'
})
export class ChunkReaderNetworkService extends AuthenticatedNetworkService {

  constructor(http: HttpClient) {
    super(http);
  }

  async loadArticlesPreview(): Promise<AnalyzedArticle[]> {
    return this.getData<AnalyzedArticle[]>('/apis/user/chunkreader/load');
  }

  async loadArticleDetail(key: string): Promise<FullAnalyzedArticle> {
    const url = `/apis/user/chunkreader/article_detail?key=${key}`;
    return this.getData<FullAnalyzedArticle>(url);
  }

  async adjustSummary(key: string, limit: number): Promise<string[]> {
    const url = `/apis/user/chunkreader/adjust_summary?key=${key}&limit=${limit}`;
    return this.getData<string[]>(url);
  }

  async analyzeArticle(rawArticle: RawArticle): Promise<boolean> {
    const resp = await this.postDataForText('/apis/user/chunkreader/analyze', rawArticle);
    return resp === 'true';
  }

  async deleteArticle(key: string): Promise<void> {
    await this.deleteWithParams('/apis/user/chunkreader/delete', { 'key': key });
  }

}
