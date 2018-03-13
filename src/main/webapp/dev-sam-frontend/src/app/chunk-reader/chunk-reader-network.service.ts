import { Injectable } from '@angular/core';
import { LoadingOverlayService } from '../overlay/loading-overlay.service';
import { HttpClient } from '@angular/common/http';
import { AnalyzedArticle, RawArticle } from './articles';

@Injectable()
export class ChunkReaderNetworkService {

  /**
   * Initialize the service via the injected http client and a loading service.
   *
   * @param {HttpClient} http the http client.
   * @param {LoadingOverlayService} loadingService the loading service.
   */
  constructor(private http: HttpClient, private loadingService: LoadingOverlayService) { }

  /**
   * Load a list of chunk reader articles preview and give back the list to client.
   *
   * @param {(items: AnalyzedArticle[]) => void} success to handle the list when succeeded.
   */
  loadArticlesPreview(success: (articles: AnalyzedArticle[]) => void): void {
    const token = localStorage.getItem('token');
    this.http.get<AnalyzedArticle[]>('/apis/chunkreader/load?token=' + token, {
      withCredentials: true
    }).subscribe(articles => success(articles));
  }

  /**
   * Load all the details for an article with a specified key and give back the article to client.
   *
   * @param {string} key the key of the article.
   * @param {(article: AnalyzedArticle) => void} success to handle the article when succeeded.
   */
  loadArticleDetail(key: string, success: (article: AnalyzedArticle) => void): void {
    const token = localStorage.getItem('token');
    const ref = this.loadingService.open();
    this.http.get<AnalyzedArticle>('/apis/chunkreader/articleDetail?key=' + key +
      '&token=' + token, {
      withCredentials: true
    }).subscribe(article => {
      ref.close();
      success(article);
    });
  }

  /**
   * Adjust number of summaries displayed for the article of the specified key and give back the list of new summaries.
   * @param {string} key the key of the article.
   *
   * @param {number} limit the new limit of the summary.
   * @param {(summaries: string[]) => void} success to handle the list of new summaries when succeeded.
   */
  adjustSummary(key: string, limit: number, success: (summaries: string[]) => void): void {
    const token = localStorage.getItem('token');
    const ref = this.loadingService.open();
    this.http.post<string[]>('/apis/chunkreader/adjustSummary?token=' + token, {
      keyString: key,
      limit: limit
    }, {
      withCredentials: true
    }).subscribe(summaries => {
      ref.close();
      success(summaries);
    });
  }

  /**
   * Send a request to analyze an article.
   *
   * @param {RawArticle} rawArticle the raw article to be analyzed.
   * @param {(items: AnalyzedArticle[]) => void} success to handle a new article list when request succeeded.
   * @param {() => void} failure to handle when the request failed.
   */
  analyzeArticle(rawArticle: RawArticle, success: (articles: AnalyzedArticle[]) => void, failure: () => void): void {
    const token = localStorage.getItem('token');
    const ref = this.loadingService.open();
    this.http.post('/apis/chunkreader/analyze?token=' + token, rawArticle, {
      responseType: 'text',
      withCredentials: true
    }).subscribe(resp => {
      if (resp === 'true') {
        this.loadArticlesPreview(articles => {
          ref.close();
          success(articles);
        });
      } else {
        ref.close();
        failure();
      }
    });
  }

}
