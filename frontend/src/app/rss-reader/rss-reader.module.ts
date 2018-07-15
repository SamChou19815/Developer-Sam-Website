import { NgModule } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { RssReaderRoutingModule } from './rss-reader-routing.module';
import { RssReaderComponent } from './rss-reader.component';
import { SubscriptionsComponent } from './subscriptions/subscriptions.component';
import { UserFeedItemComponent } from './user-feed-item/user-feed-item.component';

@NgModule({
  imports: [SharedModule, RssReaderRoutingModule],
  declarations: [RssReaderComponent, SubscriptionsComponent, UserFeedItemComponent]
})
export class RssReaderModule {
}
