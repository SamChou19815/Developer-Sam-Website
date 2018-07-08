/**
 * [GoogleUser] represents a [GoogleUser] in the system.
 */
export interface GoogleUser {
  /**
   * Used to uniquely identifiers a user.
   */
  readonly key: string;
  /**
   * Name of the user.
   */
  readonly name: string;
  /**
   * Email of the user.
   */
  readonly email: string;
  /**
   * Picture url of the user.
   */
  readonly picture: string;
}
