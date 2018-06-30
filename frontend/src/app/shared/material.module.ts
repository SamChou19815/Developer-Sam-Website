import { NgModule } from '@angular/core';
import {
  MatBadgeModule,
  MatButtonModule,
  MatCardModule,
  MatCheckboxModule,
  MatChipsModule,
  MatDatepickerModule,
  MatDialogModule,
  MatDividerModule,
  MatFormFieldModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatMenuModule,
  MatNativeDateModule,
  MatProgressBarModule,
  MatProgressSpinnerModule,
  MatRadioModule,
  MatSelectModule,
  MatSidenavModule,
  MatSliderModule,
  MatTableModule,
  MatTabsModule,
  MatToolbarModule,
  MatTooltipModule
} from '@angular/material';

@NgModule({
  imports: [
    MatSidenavModule, MatToolbarModule, MatProgressSpinnerModule, MatTabsModule,
    MatIconModule, MatListModule, MatMenuModule, MatCardModule, MatBadgeModule, MatButtonModule,
    MatCheckboxModule, MatDividerModule, MatFormFieldModule, MatInputModule, MatRadioModule,
    MatSelectModule, MatNativeDateModule, MatDatepickerModule, MatSliderModule, MatTableModule,
    MatProgressBarModule, MatDialogModule, MatChipsModule, MatTooltipModule
  ],
  exports: [
    MatSidenavModule, MatToolbarModule, MatProgressSpinnerModule, MatTabsModule,
    MatIconModule, MatListModule, MatMenuModule, MatCardModule, MatBadgeModule, MatButtonModule,
    MatCheckboxModule, MatDividerModule, MatFormFieldModule, MatInputModule, MatRadioModule,
    MatSelectModule, MatNativeDateModule, MatDatepickerModule, MatSliderModule, MatTableModule,
    MatProgressBarModule, MatDialogModule, MatChipsModule, MatTooltipModule
  ],
  declarations: []
})
export class MaterialModule {
}
