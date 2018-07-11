import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { TenCellComponent } from './ten-cell/ten-cell.component';
import { TenRoutingModule } from './ten-routing.module';
import { TenComponent } from './ten.component';

@NgModule({
  imports: [SharedModule, TenRoutingModule],
  exports: [TenComponent],
  declarations: [TenComponent, TenCellComponent],
  providers: []
})
export class TenModule {
}
