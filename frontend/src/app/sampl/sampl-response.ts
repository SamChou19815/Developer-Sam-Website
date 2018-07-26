/**
 * The normal result.
 */
export class SamplResult {
  constructor(public readonly result: string) {
  }
}

/**
 * The compile time error.
 */
export class SamplCompileError {
  constructor(public readonly message: string) {
  }
}

/**
 * The runtime error.
 */
export class SamplRuntimeError {
  constructor(public readonly message: string) {
  }
}

/**
 * A set of all possible response.
 */
export type SamplResponse =
  SamplResult | SamplCompileError | SamplRuntimeError | 'UNKNOWN_ERROR' | 'TIME_LIMIT_EXCEEDED';
