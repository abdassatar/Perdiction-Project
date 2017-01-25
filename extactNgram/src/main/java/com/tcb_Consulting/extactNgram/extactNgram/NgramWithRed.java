package com.tcb_Consulting.extactNgram.extactNgram;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Version;

public class NgramWithRed implements ExtractNgram {
	Analyzer analyzer;

	String text = "";
	Boolean stopWords = true;
	Boolean overlap = false;
	int length = 3;

	LinkedList<String> nGrams;
	LinkedList<String> uniqueNGrams;
	HashMap<String, Integer> nGramFreqs;

	public void extractNgram(String text, int length, Boolean stopWords, Boolean overlap) throws FileNotFoundException, IOException {

	    this.text = text;
	    this.length = length;
	    this.stopWords = stopWords;
	    this.overlap = overlap;

	    nGrams = new LinkedList<String>();
	    uniqueNGrams = new LinkedList<String>();
	    nGramFreqs = new HashMap<String, Integer>();

	    /* If the minLength and maxLength are both 1, then we want unigrams
	     * Make use of a StopAnalyzer when stopwords should be removed
	     * Make use of a SimpleAnalyzer when stop words should be included
	     */
	    if (length == 1){
	        if (this.stopWords) {
	            analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
	        }
	        else {
	            analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
	        }
	    }
	    else { //Bigger than unigrams so use ShingleAnalyzerWrapper. Once again, different analyzers depending on stop word removal
	        if (this.stopWords) {
	            analyzer = new ShingleAnalyzerWrapper(new StopAnalyzer(Version.LUCENE_24), length, length, " ", false, false); //This is a hack to use Lucene 2.4 since in 2.4 position increments weren't preserved by default. Using a later version puts underscores (_) in the place of removed stop words.
	        }
	        else {
	            analyzer = new ShingleAnalyzerWrapper(new SimpleAnalyzer(Version.LUCENE_CURRENT), length, length, " ", false, false);
	        }
	    }

	    //Code to process and extract the ngrams
	    TokenStream tokenStream = analyzer.tokenStream("text", new StringReader(this.text));
	    OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
	    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

	    int tokenCount = 0;
	    while (tokenStream.incrementToken()) {
	                    
	        int startOffset = offsetAttribute.startOffset();
	        int endOffset = offsetAttribute.endOffset();
	        String termToken = charTermAttribute.toString(); //The actual token term
	        nGrams.add(termToken); //Add all ngrams to the ngram LinkedList
	        
	        //If n-grams are not allowed to overlap, then increment to point of no overlap
	        if (!overlap){
	            for (int i = 0; i < length-1; i++){
	                tokenStream.incrementToken();
	            }
	        }
	        
	    }

	    //Store unique nGrams and frequencies in hash tables
	    for (String nGram : nGrams) {
	        if (nGramFreqs.containsKey(nGram)) {
	            nGramFreqs.put(nGram, nGramFreqs.get(nGram)+1);
	        }
	        else {
	            nGramFreqs.put(nGram, 1);
	            uniqueNGrams.add(nGram);
	        }
	    }
	    
	    System.out.println("Extarction n-gram with redondance");

	    LinkedList<String> ngrams = getNGrams();
        for (String s : ngrams){
            System.out.println("Ngram '" + s + "' occurs " + getNGramFrequency(s));
        }
	    
	    
	}
	public int getNGramFrequency(String ngram) {
	    return nGramFreqs.get(ngram);
	}
	public LinkedList<String> getNGrams() {
	    return nGrams;
	}
}

