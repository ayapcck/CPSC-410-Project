import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        List<String> literals = Arrays.asList(" create ", " sheet ", " for ", " add ", " track ", " projected expenses ",
                " monthly ", " trends ", " budget ", " expenses ", " starting savings ", " start ", " end ", " course ",
                " rows ", " columns ", " is ", "[", "]", "(", ")", ",", " sheet ", " account ", " balance ");
        tokenizer.Tokenizer.makeTokenizer("/Users/AndyTsang/Dropbox/CPSC 410/" +
                "CPSC-410-Project/frontend/src/main/input.tdot",literals);
    }

}
