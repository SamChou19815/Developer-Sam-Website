import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BlockingOverlayComponent } from '../overlay/blocking-overlay.component';
import { ClosableControllerComponent } from './closable-controller/closable-controller.component';
import { MaterialModule } from './material.module';

@NgModule({
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MaterialModule, HttpClientModule],
  exports: [CommonModule, FormsModule, ReactiveFormsModule, MaterialModule, HttpClientModule,
    BlockingOverlayComponent, ClosableControllerComponent],
  declarations: [BlockingOverlayComponent, ClosableControllerComponent],
  providers: [HttpClient]
})
export class SharedModule {
}
