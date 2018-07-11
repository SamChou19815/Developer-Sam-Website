/**
 * Raw article is an article for analysis.
 */
export interface RawArticle {
  /**
   * Title of the article.
   */
  title: string;
  /**
   * Content of the article.
   */
  content: string;
}

/**
 * Analyzed article is the article with the full detail of analysis.
 */
export interface AnalyzedArticle {
  /**
   * Key of the article.
   */
  readonly key: string;
  /**
   * Time of analysis.
   */
  readonly time: number;
  /**
   * Original title of the article.
   */
  readonly title: string;
  /**
   * Number of tokens in the article.
   */
  readonly tokenCount: number;
  /**
   * Content of the article.
   */
  readonly content: string;
  /**
   * A list of keywords of the article.
   */
  readonly keywords: string[];
  /**
   * Knowledge map of the article.
   */
  readonly knowledgeMap: KnowledgeGraph;
  /**
   * Summaries of the article.
   */
  summaries: string[];
}

/**
 * A single knowledge point with a name and a link.
 */
export interface KnowledgePoint {
  /**
   * Name of the knowledge point.
   */
  readonly name: string;
  /**
   * Link of the knowledge point.
   */
  readonly url?: string;
}

/**
 * Knowledge graph is the collection of different knowledge points.
 */
export interface KnowledgeGraph {
  /**
   * About people.
   */
  readonly PERSON: KnowledgePoint[];
  /**
   * About location.
   */
  readonly LOCATION: KnowledgePoint[];
  /**
   * About organizations
   */
  readonly ORGANIZATION: KnowledgePoint[];
  /**
   * About events.
   */
  readonly EVENT: KnowledgePoint[];
  /**
   * About works of art.
   */
  readonly WORK_OF_ART: KnowledgePoint[];
  /**
   * About consumer goods.
   */
  readonly CONSUMER_GOOD: KnowledgePoint[];
  /**
   * About unknown stuff.
   */
  readonly UNKNOWN: KnowledgePoint[];
}

/**
 * A dummy [KnowledgeGraph].
 * @type {KnowledgeGraph}
 */
export const dummyKnowledgeGraph: KnowledgeGraph = <KnowledgeGraph>{
  PERSON: [], LOCATION: [], ORGANIZATION: [], EVENT: [],
  WORK_OF_ART: [], CONSUMER_GOOD: [], UNKNOWN: []
};
