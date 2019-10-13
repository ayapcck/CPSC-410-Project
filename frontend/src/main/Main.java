import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import parser.Parser;
import tokenizer.Tokenizer;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        List<String> literals = Arrays.asList(" create ", " sheet ", " for ", " add ", " track ", " projected expenses ",
                " monthly ", " trends ", " budget ", " expenses ", " starting savings ", " start ", " end ", " course ",
                " rows ", " columns ", "[", "]", "(", ")", ",", " sheet ", " account ", " balance ", " expense ", " is ");
        Tokenizer.makeTokenizer("/Users/meghasinghania/Documents/School/CPSC410/" +
                "CPSC-410-Project/frontend/src/main/input.txt",literals);

        Parser parser = new Parser();

        parser.parse();
    }
}
