package com.developersam.web.model.chunkreader;

import com.google.appengine.api.ThreadManager;
import com.google.cloud.language.v1beta2.ClassificationCategory;
import com.google.cloud.language.v1beta2.Document;
import com.google.cloud.language.v1beta2.Document.Type;
import com.google.cloud.language.v1beta2.Entity;
import com.google.cloud.language.v1beta2.LanguageServiceClient;
import com.google.cloud.language.v1beta2.Sentence;
import com.google.cloud.language.v1beta2.Sentiment;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.cloud.language.v1beta2.EncodingType.UTF16;

/**
 * An Text Analyzer using Google Cloud NLP API directly.
 */
final class NLPAPIAnalyzer {
    
    /**
     * Sentiment of the entire document.
     */
    private volatile Sentiment sentiment;
    /**
     * List of entities extracted from the text.
     */
    private volatile List<Entity> entities;
    /**
     * List of sentences extracted from the text.
     */
    private volatile List<Sentence> sentences;
    /**
     * List of categories extracted from the text.
     */
    private List<ClassificationCategory> categories;
    
    /**
     * Construct a {@code NLPAPIAnalyzer} from the text needed to analyze.
     *
     * @param text text to be analyzed.
     * @throws Exception thrown when there is a problem reading the text.
     */
    private NLPAPIAnalyzer(String text) throws Exception {
        try (LanguageServiceClient client = LanguageServiceClient.create()) {
            Document doc = Document.newBuilder()
                    .setContent(text)
                    .setType(Type.PLAIN_TEXT).build();
            ExecutorService service = Executors.newFixedThreadPool(
                    3, ThreadManager.currentRequestThreadFactory());
            List<Callable<Void>> list = Arrays.asList(() -> {
                save(client.analyzeSentiment(doc).getDocumentSentiment());
                return null;
            }, () -> {
                saveEntities(client.analyzeEntitySentiment(doc, UTF16)
                        .getEntitiesList());
                return null;
            }, () -> {
                save(client.analyzeSyntax(doc, UTF16).getSentencesList());
                return null;
            });
            service.invokeAll(list);
            // TODO remove these statement when finished debugging.
            System.out.println(sentiment);
            System.out.println(entities);
            System.out.println(sentences);
            // Analyze Categories
            if (entities.size() > 20) {
                // Google's limitation
                categories = client.classifyText(doc).getCategoriesList();
            } else {
                categories = Collections.emptyList();
            }
        }
    }
    
    /**
     * Obtain an analyzer that has already analyzed the text.
     *
     * @param text text to be analyzed.
     * @return the analysis result, or {@code null} if the API request failed.
     */
    @Nullable
    static NLPAPIAnalyzer analyze(String text) {
        try {
            return new NLPAPIAnalyzer(text);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    /**
     * Save the sentiment of the text.
     *
     * @param sentiment sentiment analyzed by Google.
     */
    private void save(Sentiment sentiment) {
        this.sentiment = sentiment;
    }
    
    /**
     * Save the entities of the text.
     *
     * @param entities entities analyzed by Google.
     */
    private void saveEntities(List<Entity> entities) {
        this.entities = entities;
    }
    
    /**
     * Save the sentences of the text.
     *
     * @param sentences sentences analyzed by Google.
     */
    private void save(List<Sentence> sentences) {
        this.sentences = sentences;
    }
    
    /**
     * Obtain the sentiment of the document.
     *
     * @return the sentiment of the document.
     */
    public Sentiment getSentiment() {
        return sentiment;
    }
    
    /**
     * Obtain a list of entities from the document.
     *
     * @return a list of entities from the document.
     */
    public List<Entity> getEntities() {
        return entities;
    }
    
    /**
     * Obtain a list of sentences from the document.
     *
     * @return a list of sentences from the document.
     */
    public List<Sentence> getSentences() {
        return sentences;
    }
    
    /**
     * Obtain a list of categories from the document.
     *
     * @return a list of categories from the document.
     */
    public List<ClassificationCategory> getCategories() {
        return categories;
    }
    
}
