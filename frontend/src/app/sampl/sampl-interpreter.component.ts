import { Component, OnInit } from '@angular/core';
import { LoadingOverlayService } from '../shared/overlay/loading-overlay.service';
import { asyncRun } from '../shared/util';
import { SamplNetworkService } from './sampl-network.service';
import { SamplCompileError, SamplResponse, SamplResult, SamplRuntimeError } from './sampl-response';

@Component({
  selector: 'app-sampl-interpreter',
  templateUrl: './sampl-interpreter.component.html',
  styleUrls: ['./sampl-interpreter.component.scss']
})
export class SamplInterpreterComponent implements OnInit {

  /**
   * Code given by the user.
   */
  code = '';
  /**
   * The response from the server.
   */
  private response: SamplResponse | null = null;

  constructor(private loadingService: LoadingOverlayService,
              private networkService: SamplNetworkService) {
  }

  ngOnInit() {
  }

  /**
   * Submit the code.
   */
  submit(): void {
    asyncRun(async () => {
      const ref = this.loadingService.open();
      this.response = await this.networkService.getInterpretationResult(this.code);
      ref.close();
    });
  }

  /**
   * Returns whether we have a response.
   *
   * @return {boolean} whether we have a response.
   */
  get hasResponse(): boolean {
    return this.response != null;
  }

  /**
   * Returns the optional result.
   *
   * @return {SamplResult | null} the optional result.
   */
  get result(): SamplResult | null {
    return (this.response instanceof SamplResult) ? this.response : null;
  }

  /**
   * Returns the optional compile time error.
   *
   * @return {SamplCompileError | null} the optional compile time error.
   */
  get compileError(): SamplCompileError | null {
    return (this.response instanceof SamplCompileError) ? this.response : null;
  }

  /**
   * Returns the optional runtime error.
   *
   * @return {SamplRuntimeError | null} the optional runtime error.
   */
  get runtimeError(): SamplRuntimeError | null {
    return (this.response instanceof SamplRuntimeError) ? this.response : null;
  }

  /**
   * Returns whether the result is UNKNOWN_ERROR.
   *
   * @return {boolean} whether the result is UNKNOWN_ERROR.
   */
  get unknownError(): boolean {
    return this.response === 'UNKNOWN_ERROR';
  }

  /**
   * Returns whether the result is TIME_LIMIT_EXCEEDED.
   *
   * @return {boolean} whether the result is TIME_LIMIT_EXCEEDED.
   */
  get timeLimitExceeded(): boolean {
    return this.response === 'TIME_LIMIT_EXCEEDED';
  }

}
