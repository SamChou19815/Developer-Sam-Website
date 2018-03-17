export interface PublicUserData {
  githubAccount?: string;
}

/**
 * Public User interface defines the information that a public user should
 * contain.
 */
export interface PublicUser extends PublicUserData {
  nickname: string;
}
