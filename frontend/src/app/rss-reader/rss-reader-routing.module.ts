import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { RssReaderComponent } from './rss-reader.component';
import { SubscriptionsComponent } from './subscriptions/subscriptions.component';

const routes = [
  { path: '', component: RssReaderComponent },
  { path: 'subscriptions', component: SubscriptionsComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  declarations: []
})
export class RssReaderRoutingModule {
}
