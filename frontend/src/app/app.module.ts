import { NgModule } from '@angular/core';
import { SharedModule } from './shared/shared.module';
import { BrowserModule, Title } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoadingOverlayComponent } from './overlay/loading-overlay.component';
import { LoadingOverlayService } from './overlay/loading-overlay.service';
import { AlertComponent } from './alert/alert.component';
import { PushedControllerOverlayComponent } from './overlay/pushed-controller-overlay.component';
import { environment } from '../environments/environment';
import { AngularFireModule } from 'angularfire2';
import { AngularFireAuthModule } from 'angularfire2/auth';
import { GoogleUserService } from './google-user/google-user.service';
import { ServiceWorkerModule } from '@angular/service-worker';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoadingOverlayComponent,
    PushedControllerOverlayComponent,
    AlertComponent
  ],
  imports: [
    BrowserModule,
    AngularFireModule.initializeApp(environment.firebase),
    AngularFireAuthModule,
    BrowserAnimationsModule,
    SharedModule,
    AppRoutingModule,
    ServiceWorkerModule.register('/ngsw-worker.js', { enabled: environment.production })
  ],
  providers: [Title, LoadingOverlayService, GoogleUserService],
  bootstrap: [AppComponent],
  entryComponents: [LoadingOverlayComponent, AlertComponent]
})
export class AppModule {}
