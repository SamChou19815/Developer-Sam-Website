/**
 * Defines the shape of a raw, not analyzed article.
 */
export interface RawArticle {
  title: string;
  content: string;
}

/**
 * Defines the shape of an analyzed article.
 */
export interface AnalyzedArticle {
  readonly key: string;
  readonly time: number;
  readonly title: string;
  readonly tokenCount: number;
  readonly content?: string;
  readonly keywords?: string[];
  readonly knowledgeMap?: KnowledgeGraph;
  summaries?: string[];
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

/**
 * Defines the shape of an knowledge point.
 */
export interface KnowledgePoint {
  readonly name: string;
  readonly url?: string;
}
