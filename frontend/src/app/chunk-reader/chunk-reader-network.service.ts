import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthenticatedNetworkService } from '../shared/authenticated-network-service';
import { AnalyzedArticle, RawArticle } from './chunk-reader-data';

@Injectable({
  providedIn: 'root'
})
export class ChunkReaderNetworkService extends AuthenticatedNetworkService {

  constructor(http: HttpClient) {
    super(http);
  }

  /**
   * Returns the promise of a list of analyzed articles.
   *
   * @returns {Promise<AnalyzedArticle[]>} the promise of a list of analyzed articles.
   */
  async loadArticles(): Promise<AnalyzedArticle[]> {
    return this.getData<AnalyzedArticle[]>('/apis/user/chunk_reader/load');
  }

  /**
   * Returns the promise of adjusted summaries.
   *
   * @param {string} key key of the article of adjust summary.
   * @param {number} limit limit of summary.
   * @returns {Promise<string[]>} the promise of adjusted summaries.
   */
  async adjustSummary(key: string, limit: number): Promise<string[]> {
    const url = '/apis/user/chunk_reader/adjust_summary';
    return this.getData<string[]>(url, { 'key': key, 'limit': limit.toString(10) });
  }

  /**
   * Asynchronously analyzes an article and reports whether it's successful initially.
   *
   * @param {RawArticle} rawArticle the article to be analyzed.
   * @returns {Promise<boolean>} the promise of the success report.
   */
  async analyzeArticle(rawArticle: RawArticle): Promise<boolean> {
    const resp = await this.postDataForText('/apis/user/chunk_reader/analyze', rawArticle);
    return resp === 'true';
  }

  /**
   * Asynchronously sends an article with key.
   *
   * @param {string} key key of the article to be deleted.
   * @returns {Promise<void>} the promise when done.
   */
  async deleteArticle(key: string): Promise<void> {
    await this.deleteData('/apis/user/chunk_reader/delete', { 'key': key });
  }

}
