import { Injectable } from '@angular/core';
import { AngularFireAuth } from 'angularfire2/auth';
import * as firebase from 'firebase/app';

@Injectable()
export class GoogleUserService {

  constructor(private angularFireAuth: AngularFireAuth) {
  }

  afterSignedIn(doTask: () => void): void {
    this.angularFireAuth.authState.subscribe(userOptional => {
      if (userOptional === null) {
        // noinspection JSIgnoredPromiseFromCall
        this.angularFireAuth.auth.signInWithRedirect(new firebase.auth.GoogleAuthProvider());
        return;
      }
      userOptional.getIdToken(true).then(token => {
        localStorage.setItem('token', token);
        doTask();
      });
    });
  }

}
