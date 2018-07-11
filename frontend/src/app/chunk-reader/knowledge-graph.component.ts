import { Component, Input, OnInit } from '@angular/core';
import { dummyKnowledgeGraph, KnowledgeGraph } from './chunk-reader-data';

@Component({
  selector: 'app-chunk-reader-knowledge-graph',
  template: `
    <div class="section-text">Knowledge Graph</div>
    <div *ngIf="knowledgeGraph.PERSON">
      People Mentioned:
      <ul>
        <li *ngFor="let person of knowledgeGraph.PERSON">{{person.name}}</li>
      </ul>
    </div>
    <div *ngIf="knowledgeGraph.LOCATION">
      Locations Mentioned:
      <ul>
        <li *ngFor="let location of knowledgeGraph.LOCATION">{{location.name}}</li>
      </ul>
    </div>
    <div *ngIf="knowledgeGraph.ORGANIZATION">
      Organizations Mentioned:
      <ul>
        <li *ngFor="let org of knowledgeGraph.ORGANIZATION">{{org.name}}</li>
      </ul>
    </div>
    <div *ngIf="knowledgeGraph.EVENT">
      Events Mentioned:
      <ul>
        <li *ngFor="let event of knowledgeGraph.EVENT">{{event.name}}</li>
      </ul>
    </div>
    <div *ngIf="knowledgeGraph.WORK_OF_ART">
      Work of Art Mentioned:
      <ul>
        <li *ngFor="let workOfArt of knowledgeGraph.WORK_OF_ART">
          {{workOfArt.name}}
        </li>
      </ul>
    </div>
    <div *ngIf="knowledgeGraph.CONSUMER_GOOD">
      Consumer Goods Mentioned:
      <ul>
        <li *ngFor="let consumerGood of knowledgeGraph.CONSUMER_GOOD">
          {{consumerGood.name}}
        </li>
      </ul>
    </div>
    <div *ngIf="knowledgeGraph.UNKNOWN">
      Others:
      <ul>
        <li *ngFor="let other of knowledgeGraph.UNKNOWN">{{other.name}}</li>
      </ul>
    </div>
  `,
  styleUrls: []
})
export class KnowledgeGraphComponent implements OnInit {

  @Input() knowledgeGraph: KnowledgeGraph = dummyKnowledgeGraph;

  constructor() {
  }

  ngOnInit() {
  }

}
