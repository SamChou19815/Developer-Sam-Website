package com.developersam.web.model.blog.util;

import com.developersam.web.model.blog.exceptions.BlogContentNotFetchedException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A helper class based on Jsoup to fetch and process blog article content.
 */
public final class BlogContentFetcher {
    
    /**
     * Obtain file content by specifying a file url
     *
     * @param url fire url
     * @return string form of the file content
     * @throws IOException io exception
     */
    private static String getFileContent(String url) throws IOException {
        FileInputStream fis = new FileInputStream(new File(url));
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(fis, "UTF-8"))) {
            return br.lines().collect(Collectors.joining("\n"));
        }
    }
    
    /**
     * Obtains summary and HTML content.
     * If the content is less than 200 words, summary is {@code null}.
     *
     * @param path path of root.
     * @param url file url.
     * @return [summary, content].
     * @throws BlogContentNotFetchedException error.
     */
    public static String[] fetch(String path, String url)
            throws BlogContentNotFetchedException {
        try {
            String completeUrl = path + "/resource/blog/posts/" + url + ".html";
            String content = getFileContent(completeUrl);
            Element body = Jsoup.parseBodyFragment(content).body();
            content = body.html();
            String summary = getSummary(body);
            return new String[]{summary, content};
        } catch (IOException e) {
            e.printStackTrace();
            throw new BlogContentNotFetchedException();
        }
    }
    
    /**
     * Obtain text content from HTML node.
     *
     * @param e text node or element node.
     * @return string form of the text found.
     */
    private static String getTextFromNode(Node e) {
        if (e instanceof Element) {
            return ((Element) e).text();
        } else if (e instanceof TextNode) {
            return ((TextNode) e).getWholeText();
        } else {
            return "";
        }
    }
    
    /**
     * Get word count from a HTML node.
     *
     * @param e html node.
     * @return word count of the node.
     */
    private static int getHTMLElementWordCount(Node e) {
        return getTextFromNode(e).split(" ").length;
    }
    
    /**
     * Find the index of nth occurrence of space in a string, return -1 if
     * not found.
     *
     * @param str string to find space.
     * @param n nth occurrence.
     * @return index.
     */
    private static int nthOccurrenceOfSpace(String str, int n) {
        int pos = str.indexOf(" ");
        while (--n > 0 && pos != -1)
            pos = str.indexOf(" ", pos + 1);
        return pos;
    }
    
    /**
     * This method removes additional words from an HTML.
     * It is a recursive method that helps keep the HTML format while removing
     * things.
     *
     * @param e element.
     * @param maxWords max words.
     */
    private static void removeAdditionalWords(Node e, int maxWords) {
        int wordLimitLeft = maxWords;
        List<Node> children = e.childNodes();
        // we have to use Node to include text node
        int originalSize = children.size();
        if (originalSize == 0) {
            // base case: cannot deal with children any more, needs to actually
            // remove words.
            String plainText = getTextFromNode(e);
            int index = nthOccurrenceOfSpace(plainText, maxWords);
            if (index != -1) {
                String resultantText = plainText.substring(0, index) + " ...";
                // only text node has no children
                ((TextNode) e).text(resultantText);
            }
            return;
        }
        int removeChildrenStartingAtIndex = children.size();
        // initialize to length, assuming no removal of children
        // A loop to check starting from which child node,
        // word limit has been exceeded.
        for (int i = 0; i < originalSize; i++) {
            Node childE = children.get(i);
            int childWordCount = getHTMLElementWordCount(childE);
            // remove additional elements that will clearly exceed word limit
            if (wordLimitLeft == childWordCount) {
                removeChildrenStartingAtIndex = i + 1;
                // just enough, stop here. No need to waste resources on the
                // recursive method.
                break;
            } else if (wordLimitLeft < childWordCount) {
                removeAdditionalWords(childE, wordLimitLeft);
                removeChildrenStartingAtIndex = i + 1;
                break;
            } else {
                wordLimitLeft -= childWordCount;
            }
        }
        // Remove nodes that comes after the node that just exceeds the word
        // limit.
        for (int i = removeChildrenStartingAtIndex; i < originalSize; i++) {
            children.get(removeChildrenStartingAtIndex).remove();
            /*
            Due to the implementation of Jsoup lib, it only removes the element
            from doc tree.
            But that's already what we want.
             */
        }
    }
    
    /**
     * The summary of the article is defined to be the first 200 words or the
     * entire article within 200 words.
     * This method calls the recursive private method removeAdditionalWords if
     * word limit has been exceeded.
     *
     * @param body body node in html, refers to the parsed blog article/
     * @return the summary.
     */
    private static String getSummary(Element body) {
        if (getHTMLElementWordCount(body) <= 200) {
            return null;
        }
        removeAdditionalWords(body, 200);
        return body.html();
    }
}