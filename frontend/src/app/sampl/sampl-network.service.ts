import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SamplCompileError, SamplResponse, SamplResult, SamplRuntimeError } from './sampl-response';

@Injectable({
  providedIn: 'root'
})
export class SamplNetworkService {

  constructor(private http: HttpClient) {
  }

  // noinspection JSMethodCanBeStatic
  /**
   * Perform a safe prefix check.
   *
   * @param s {string} the string to check.
   * @param prefix {string} the prefix to check.
   * @return {boolean} whether s contains prefix.
   */
  private safePrefixCheck(s: string, prefix: string): boolean {
    return s.length > prefix.length && s.substring(0, prefix.length) === prefix;
  }

  /**
   * Returns the result of interpretation.
   *
   * @param code code to interpret.
   * @return {Promise<SamplResponse>} the promise of response.
   */
  async getInterpretationResult(code: string): Promise<SamplResponse> {
    const rawResponse = await this.http.post('/apis/public/sampl/interpret', code, {
      responseType: 'text'
    }).toPromise();
    const resultPrefix = 'Result: ';
    const compileErrorPrefix = 'CompileTimeError: ';
    const runtimeErrorPrefix = 'RuntimeError: ';
    if (rawResponse === 'TIME_LIMIT_EXCEEDED' || rawResponse === 'UNKNOWN_ERROR') {
      return rawResponse;
    } else if (this.safePrefixCheck(rawResponse, resultPrefix)) {
      const s = rawResponse.substring(resultPrefix.length);
      return new SamplResult(s);
    } else if (this.safePrefixCheck(rawResponse, compileErrorPrefix)) {
      const s = rawResponse.substring(compileErrorPrefix.length);
      return new SamplCompileError(s);
    } else if (this.safePrefixCheck(rawResponse, runtimeErrorPrefix)) {
      const s = rawResponse.substring(runtimeErrorPrefix.length);
      return new SamplRuntimeError(s);
    } else {
      throw new Error('Bad Response!');
    }
  }

}
