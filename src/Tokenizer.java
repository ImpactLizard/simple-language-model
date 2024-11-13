import javax.xml.stream.events.Characters;
import java.util.*;

public class Tokenizer {
    private final Map<Character, Integer> CharToTok = new HashMap<>();
    private final Map<Integer, Character> TokToChar = new HashMap<>();
    private final List<Integer> tokens = new ArrayList<>();
    private final List<Character> characters = new ArrayList<>();
    String corpus;

    Tokenizer(String corpus) {
        this.corpus = corpus;
        buildVocab();
    }

    private void buildVocab() {
        // manually add the whitespace character at the beginning
        CharToTok.put(' ', 0);
        TokToChar.put(0, ' ');
        // loop through the rest of the characters in the corpus
        int token = 1;
        for (int i = 1; i <= corpus.length(); i++) {
            // only add non duplicates
            if (!CharToTok.containsKey(corpus.charAt(i-1))) {
                CharToTok.put(corpus.charAt(i-1), token);
                TokToChar.put(token, corpus.charAt(i-1));
                token++;
            }
        }
    }

    public List<Integer> tokenize() {
        for (int i = 0; i < corpus.length(); i++) {
            tokens.add(CharToTok.get(corpus.charAt(i)));
        }
        return tokens;
    }

    public List<Character> deTokenize() {
        for (Integer token : tokens) {
            characters.add(TokToChar.get(token));
        }
        return characters;
    }

    public List<Integer> getTokens() {
        return tokens;
    }

    public Map<Character, Integer> getCharToTok() {
        return CharToTok;
    }

    public Map<Integer, Character> getTokToChar() {
        return TokToChar;
    }
}
