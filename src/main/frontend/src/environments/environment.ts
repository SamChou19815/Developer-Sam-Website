// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  backend: '',
  firebase: {
    apiKey: 'AIzaSyDBWoo_i6JNj0qgbXPKxxdco83FkPlKybo',
    authDomain: 'dev-sam.firebaseapp.com',
    databaseURL: 'https://dev-sam.firebaseio.com',
    projectId: 'dev-sam',
    storageBucket: 'dev-sam.appspot.com',
    messagingSenderId: '705604021257'
  }
};
