import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularFontAwesomeModule } from 'angular-font-awesome';
import { BlockingOverlayComponent } from '../overlay/blocking-overlay.component';
import { ClosableControllerComponent } from './closable-controller/closable-controller.component';
import { IconComponent } from './icon.component';
import { MaterialModule } from './material.module';

@NgModule({
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MaterialModule,
    AngularFontAwesomeModule, HttpClientModule],
  exports: [CommonModule, FormsModule, ReactiveFormsModule, MaterialModule,
    AngularFontAwesomeModule, HttpClientModule,
    BlockingOverlayComponent, ClosableControllerComponent, IconComponent],
  declarations: [BlockingOverlayComponent, ClosableControllerComponent, IconComponent],
  providers: [HttpClient]
})
export class SharedModule {
}
