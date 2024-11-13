public class Main {
    public static void main(String[] args) {
        String corpus = "John is a man that does very manly things.";
        Tokenizer tokenizer = new Tokenizer(corpus);
        System.out.println(tokenizer.tokenize());
        System.out.println(tokenizer.deTokenize());
    }
}