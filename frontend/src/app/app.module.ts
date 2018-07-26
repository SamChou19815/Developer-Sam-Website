import { NgModule } from '@angular/core';
import { BrowserModule, Title } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ServiceWorkerModule } from '@angular/service-worker';
import { AngularFireModule } from 'angularfire2';
import { AngularFireAuthModule } from 'angularfire2/auth';
import { environment } from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AboutMeRowComponent } from './home/about-me-row/about-me-row.component';
import { AboutMeSectionComponent } from './home/about-me-section/about-me-section.component';
import { HomeComponent } from './home/home.component';
import { ProjectCardComponent } from './home/project-card/project-card.component';
import { NavModule } from './nav/nav.module';
import { SharedModule } from './shared/shared.module';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    ProjectCardComponent,
    AboutMeRowComponent,
    AboutMeSectionComponent
  ],
  imports: [
    BrowserModule, NavModule, BrowserAnimationsModule,
    AngularFireModule.initializeApp(environment.firebase), AngularFireAuthModule,
    ServiceWorkerModule.register('/ngsw-worker.js', { enabled: environment.production }),
    AppRoutingModule,
    SharedModule
  ],
  providers: [Title],
  bootstrap: [AppComponent]
})
export class AppModule {
}
