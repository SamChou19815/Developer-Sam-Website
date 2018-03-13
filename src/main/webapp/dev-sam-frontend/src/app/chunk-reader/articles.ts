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
  readonly keyString: string;
  readonly date: string;
  readonly title: string;
  readonly tokenCount: number;
  readonly content?: string;
  readonly textType?: string;
  readonly keywords?: string[];
  readonly knowledgeMap?: KnowledgeGraph;
  summaries?: string[];
  readonly categories?: string[];
}

export interface KnowledgeGraph {
  PERSON: KnowledgePoint[];
  LOCATION: KnowledgePoint[];
  ORGANIZATION: KnowledgePoint[];
  EVENT: KnowledgePoint[];
  WORK_OF_ART: KnowledgePoint[];
  CONSUMER_GOOD: KnowledgePoint[];
  UNKNOWN: KnowledgePoint[];
}

/**
 * Defines the shape of an knowledge point.
 */
export interface KnowledgePoint {
  readonly name: string;
  readonly url?: string;
}
