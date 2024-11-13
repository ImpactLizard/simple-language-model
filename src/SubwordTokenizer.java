
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class SubwordTokenizer {
    // this is the dictionary to hold all tokens that the llm will know
    Map<String, Integer> StrToTok = new HashMap<>();
    Map<Integer, String> TokToStr = new HashMap<>();
    // This is the list of tokens for the corpus
    List<Integer> tokens;

    public SubwordTokenizer(String input) {
        // Tokenize the input on a character level
        Tokenizer tokenizer = new Tokenizer(input);
        // add these tokens to our StrToTok map
        for (Map.Entry<Character, Integer> entry : tokenizer.getCharToTok().entrySet()) {
            StrToTok.put(entry.getKey().toString(), entry.getValue());
            TokToStr.put(entry.getValue(), entry.getKey().toString());
        }
        // now convert the input string into a list of tokens numbers
        tokens = tokenizer.tokenize();
        // now start looking for the most frequent pairs of tokens
    }

    public SubwordTokenizer(File file) throws IOException {
        String input = Files.readString(file.toPath());
        Tokenizer tokenizer = new Tokenizer(input);
        // add these tokens to our StrToTok map
        for (Map.Entry<Character, Integer> entry : tokenizer.getCharToTok().entrySet()) {
            StrToTok.put(entry.getKey().toString(), entry.getValue());
            TokToStr.put(entry.getValue(), entry.getKey().toString());
        }
        // now convert the input string into a list of tokens numbers
        tokens = tokenizer.tokenize();
        // now start looking for the most frequent pairs of tokens
    }

    /// Performs the Byte Pair Encoding algorithm on the list of tokens
    /// @param n - the number of times to perform the BPE
    public void BPE(int n) {
        for (int i = 0; i < n; i++) {
            if (tokens.size() == 1) {
                // do nothing if there is only one token in tokens
                return;
            }
            // toks - the list to contain the combined tokens
            List<Integer> toks = new ArrayList<>();
            // newTokenValue - the token value that will be assigned to the newly combined token
            int newTokenValue = TokToStr.size() + 1;
            // get the most frequent pair of tokens
            Pair<Integer, Integer> mostFrequentPair = getMostFrequentPair();
            // combine the MFP of tokens in tokens
            int j = 0; while (j < tokens.size()) {
                // if there is only on more token just add it and break
                if (j == (tokens.size() - 1)) {
                    toks.add(tokens.get(j));
                    break;
                }
                if (tokens.get(j).equals(mostFrequentPair.left()) && tokens.get(j + 1).equals(mostFrequentPair.right())) {
                    // the current pair of tokens is an instance of the MFP, so add the new token to toks
                    toks.add(newTokenValue);
                    // add this new token to the TokToStr map
                    TokToStr.put(newTokenValue, TokToStr.get(tokens.get(j)) + TokToStr.get(tokens.get(j+1)));
                    // move to the next pair of tokens
                    j += 2;
                } else {
                    // the current pair of tokens is not the MFP, add the first token to toks and move onto the next pair
                    toks.add(tokens.get(j));
                    // move to the next token
                    j += 1;
                }
            }
            tokens = toks;
        }
    }

    /// @return - returns the pair of tokens that appears the most number of times in tokens. if
     /// there is no pair that occurs more than once, it returns the lexicographically smallest pair
    private Pair<Integer, Integer> getMostFrequentPair() throws NoSuchElementException {
        // pairFrequency - maps a pair of tokens to the number of times that it appears in tokens
        Map<Pair<Integer, Integer>, Integer> pairFrequency = new HashMap<>();
        // mostFrequentPair (MPF) - the pair that will be returned
        Optional<Pair<Integer, Integer>> MFP = Optional.empty();
        // lexicographicallySmallestPair (LSP) - the pair that will be return if no MFP exists
        Pair<Integer, Integer> LSP = new Pair<>(tokens.get(0), tokens.get(1));
        // max - keeps track of the number of times the current MFP occurs
        int max = 1;
        // get the most frequent pair of tokens
        for (int i = 0; (i + 1) < tokens.size(); i++) {
            if (pairFrequency.containsKey(new Pair<>(tokens.get(i), tokens.get(i + 1)))) {
                // this pair of tokens has already been found, so increment its count
                pairFrequency.put(new Pair<>(tokens.get(i), tokens.get(i + 1)), pairFrequency.get(new Pair<>(tokens.get(i), tokens.get(i + 1))) + 1);
                // check if its count is larger than the count of the MFP
                if (pairFrequency.get(new Pair<>(tokens.get(i), tokens.get(i + 1))) > max) {
                    // if so, then set this count to be the new max
                    max = pairFrequency.get(new Pair<>(tokens.get(i), tokens.get(i + 1)));
                    // then set this pair to be the MFP
                    MFP = Optional.of(new Pair<>(tokens.get(i), tokens.get(i + 1)));
                }
            } else {
                // this pair of tokens is new, so add it to the map
                pairFrequency.put(new Pair<>(tokens.get(i), tokens.get(i + 1)), 1);
                // check if it is the new LSP
                String possibleLSP = TokToStr.get(tokens.get(i)) + TokToStr.get(tokens.get(i + 1));
                String currentLSP = TokToStr.get(LSP.left()) + TokToStr.get(LSP.right());
                if (possibleLSP.toLowerCase().compareTo(currentLSP.toLowerCase()) < 0) {
                    // the possible LSP is less than the current LSP, so make this the new LSP
                    LSP = new Pair<>(tokens.get(i), tokens.get(i + 1));
                }
            }
        }
        return MFP.orElse(LSP);
    }

    public void shrinkTokens(int shrinkTimes) {
        // this is the number of times to condense the tokens
        for (int i = 0; i < shrinkTimes; i++) {
            // this maps each pair of tokens to the number of
            // times that pair appears in the input string
            Map<Pair<Integer, Integer>, Integer> tokenCounts = new HashMap<>();
            // loop through every pair of tokens in the input string
            for (int j = 0; (j + 1) < tokens.size(); j++) {
                // get the current pair of tokens
                int token1 = tokens.get(j);
                int token2 = tokens.get(j + 1);
                Pair<Integer, Integer> newToken = new Pair<>(token1, token2);
                // check if these tokens are in the count map
                if (tokenCounts.containsKey(newToken)) {
                    // if so, add one to the count
                    tokenCounts.put(newToken, tokenCounts.get(newToken) + 1);
                } else {
                    // if not, add this newToken to the map
                    tokenCounts.put(newToken, 1);
                }
            }
            // get the pair that occurred the most
            Optional<Map.Entry<Pair<Integer, Integer>, Integer>> maxEntry = tokenCounts
                    .entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue());
            Pair<Integer, Integer> mostFrequentPair;
            if (maxEntry.isPresent()) {
                mostFrequentPair = maxEntry.get().getKey();
            } else {
                try {
                    mostFrequentPair = new Pair<>(tokens.getFirst(), tokens.get(1));
                } catch (IndexOutOfBoundsException e) {
                    throw new Error("Cannot shrink tokens further");
                }
            }
            // go through the list of tokens and combine all the occurrences
            List<Integer> newTokens = new ArrayList<>();
            // get the new token number for the condensed tokens
            int newTokenValue = StrToTok.size() + 1;
            // bool to do stuff
            int j = 0;
            while ((j + 1) < tokens.size()) {
                // get the current pair of tokens
                int token1 = tokens.get(j);
                int token2 = tokens.get(j + 1);
                // if the current pair of tokens matches the most frequent pair
                if (token1 == mostFrequentPair.left() && token2 == mostFrequentPair.right()) {
                    // join these elements together
                    // so set the element at index j to be the combination
                    // of the string at j and j + 1
                    TokToStr.put(newTokenValue, TokToStr.get(token1) + TokToStr.get(token2));
                    StrToTok.put(TokToStr.get(newTokenValue), newTokenValue);
                    newTokens.add(newTokenValue);
                    j += 2;
                } else {
                    newTokens.add(token1);
                    if ((j+1) == (tokens.size()-1)) {
                        newTokens.add(token2);
                    }
                    j += 1;
                }
            }
            tokens = newTokens;
        }
    }

    public List<String> getTokensAsStrings() {
        List<String> tokensAsStr = new ArrayList<>();
        for (Integer tok : tokens) {
            tokensAsStr.add(TokToStr.get(tok));
        }
        return tokensAsStr;
    }
}

