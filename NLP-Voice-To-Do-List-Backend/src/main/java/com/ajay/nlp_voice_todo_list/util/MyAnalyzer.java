package com.ajay.nlp_voice_todo_list.util;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

public class MyAnalyzer extends Analyzer {

    private final CharArraySet stopWords;
    private final Set<String> protectedTerms;

    public MyAnalyzer(Set<String> stopWordsList, Set<String> protectedTermsList) {
        this.stopWords = new CharArraySet(stopWordsList, true);
        this.protectedTerms = new HashSet<>(protectedTermsList);
    }


    @Override
    protected TokenStreamComponents createComponents(String s) {
        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();            // nike shoes for men - splits based on whitespace
        TokenStream tokenStream = new StopFilter(tokenizer, stopWords);       //nike shoes men - removes our custom stopWords

        tokenStream = new TokenFilter(tokenStream) {
            private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);

            @Override
            public boolean incrementToken() throws IOException {
                if (!input.incrementToken()) return false;

                String term = termAttr.toString();
                if (protectedTerms.contains(term)) {
                    termAttr.setEmpty().append(term);
                    return true;
                }
                return true;
            }
        };

        tokenStream = new PorterStemFilter(tokenStream);                      //nike shoe men - performs porter stemming
        return new TokenStreamComponents(tokenizer, tokenStream);
    }

    public String stem (String text){
        if(text == null || text.isEmpty())
            return null;

        Pair<String,String> placeHolders = StemmerHelper.getPlaceHolders(text, protectedTerms);
        text = placeHolders.getFirst();

        try(TokenStream tokenStream = tokenStream(null,new StringReader(text))){
            StringBuilder result = new StringBuilder();
            CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);  //receiving stemmed result as Char Term Attribute

            tokenStream.reset();
            while(tokenStream.incrementToken()){     //appending to result string in loop until it has next value
                result.append(charTermAttribute.toString()).append(" ");
            }
            tokenStream.end();

            String stemmedText = result.toString().trim();
            return stemmedText.replace("_PLACEHOLDER_", placeHolders.getSecond());

        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
