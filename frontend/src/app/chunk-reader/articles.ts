export interface RawArticle {
  title: string;
  content: string;
}

export interface AnalyzedArticle {
  readonly key: string;
  readonly time: number;
  readonly title: string;
  readonly tokenCount: number;
}

export namespace AnalyzedArticle {
  export const defaultValue = <AnalyzedArticle>{ key: '', time: 0, title: '', tokenCount: 0 };
}

export interface FullAnalyzedArticle extends AnalyzedArticle{
  readonly content: string;
  readonly keywords: string[];
  readonly knowledgeMap: KnowledgeGraph;
  summaries: string[];
}

export interface KnowledgeGraph {
  readonly PERSON: KnowledgePoint[];
  readonly LOCATION: KnowledgePoint[];
  readonly ORGANIZATION: KnowledgePoint[];
  readonly EVENT: KnowledgePoint[];
  readonly WORK_OF_ART: KnowledgePoint[];
  readonly CONSUMER_GOOD: KnowledgePoint[];
  readonly UNKNOWN: KnowledgePoint[];
}

export interface KnowledgePoint {
  readonly name: string;
  readonly url?: string;
}
