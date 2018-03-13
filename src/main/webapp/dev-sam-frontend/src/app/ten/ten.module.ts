import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { HttpClient, HttpClientModule } from '@angular/common/http';

import { TenComponent } from './ten.component';
import { TenRoutingModule } from './ten-routing.module';
import { TenNetworkService } from './ten-network.service';
import { TenService } from './ten.service';
import { TenCellComponent } from './ten-cell/ten-cell.component';

@NgModule({
  imports: [
    SharedModule,
    CommonModule,
    SharedModule,
    TenRoutingModule
  ],
  exports: [TenComponent],
  declarations: [TenComponent, TenCellComponent],
  providers: [HttpClient, TenNetworkService, TenService]
})
export class TenModule {}
