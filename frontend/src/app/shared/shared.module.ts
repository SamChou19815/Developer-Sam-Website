import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularFontAwesomeModule } from 'angular-font-awesome';
import { AlertComponent } from './alert/alert.component';
import { ClosableControllerComponent } from './closable-controller/closable-controller.component';
import { IconComponent } from './icon.component';
import { MaterialModule } from './material.module';
import { BlockingOverlayComponent } from './overlay/blocking-overlay.component';
import { PushedControllerOverlayComponent } from './overlay/pushed-controller-overlay.component';
import { UserCardComponent } from './user-card/user-card.component';

@NgModule({
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MaterialModule,
    AngularFontAwesomeModule, HttpClientModule],
  exports: [CommonModule, FormsModule, ReactiveFormsModule, MaterialModule,
    AngularFontAwesomeModule, HttpClientModule,
    BlockingOverlayComponent, ClosableControllerComponent,
    PushedControllerOverlayComponent, AlertComponent,
    IconComponent, UserCardComponent],
  declarations: [BlockingOverlayComponent, ClosableControllerComponent,
    PushedControllerOverlayComponent, AlertComponent,
    IconComponent, UserCardComponent],
  providers: [HttpClient],
  entryComponents: [AlertComponent]
})
export class SharedModule {
}
