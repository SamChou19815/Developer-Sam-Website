import { Injectable } from '@angular/core';
import { AngularFireAuth } from 'angularfire2/auth';
import { User } from "firebase";
import * as firebase from 'firebase/app';

@Injectable()
export class GoogleUserService {

  constructor(private angularFireAuth: AngularFireAuth) {
  }

  private userPromise(): Promise<User | null> {
    return new Promise((resolve, reject) =>
      this.angularFireAuth.auth.onAuthStateChanged(user => {
        resolve(user)
      }, error => {
        reject(error);
      })
    );
  }

  async afterSignedIn(): Promise<string> {
    const userOpt = await this.userPromise();
    if (userOpt == null) {
      try {
        await this.angularFireAuth.auth.signInWithRedirect(new firebase.auth.GoogleAuthProvider());
      } catch (e) {
      }
      return this.afterSignedIn();
    }
    const token = await userOpt.getIdToken(false);
    localStorage.setItem('token', token);
    return token;
  }

}
