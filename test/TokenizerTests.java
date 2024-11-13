import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class TokenizerTests {
    @Test
    public void test1() throws IOException {
        SubwordTokenizer s = new SubwordTokenizer("Mississippilessly");
        System.out.println(s.getTokensAsStrings());
        for (int i = 0; i < 13; i++) {
            s.BPE(1);
            System.out.println(s.getTokensAsStrings());
        }
        System.out.println(s.getTokensAsStrings());
        SubwordTokenizer s2 = new SubwordTokenizer("The dog is a dog that does doggy like things like a dog");
        s2.BPE(3);
        System.out.println(s2.getTokensAsStrings());
        SubwordTokenizer s3 = new SubwordTokenizer(new File("corpus.txt"));
        s3.BPE(1500);
        System.out.println(s3.getTokensAsStrings());
    }
}
