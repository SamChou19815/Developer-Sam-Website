import { NgModule } from '@angular/core';
import { MaterialModule } from './material.module';
import { BlockingOverlayComponent } from '../overlay/blocking-overlay.component';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ClosableControllerComponent } from './closable-controller/closable-controller.component';

@NgModule({
  imports: [CommonModule, FormsModule, MaterialModule, HttpClientModule],
  exports: [CommonModule, FormsModule, MaterialModule, HttpClientModule,
    BlockingOverlayComponent, ClosableControllerComponent],
  declarations: [BlockingOverlayComponent, ClosableControllerComponent],
  providers: [HttpClient]
})
export class SharedModule {}
