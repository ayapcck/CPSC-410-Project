import parser.Parser;
import tokenizer.Tokenizer;
import typecheck.TypeCheckVisitor;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        List<String> literals = Arrays.asList(" create ", " sheet ", " for ", " add ", " track ", " projected expenses ",
                " monthly ", " trends ", " budget ", " expenses ", " starting savings ", " start ", " end ", " course ",
                " rows ", " columns ", "[", "]", "(", ")", ",", " sheet ", " account ", " balance ", " expense ", " is ");
        Tokenizer.makeTokenizer("./frontend/src/main/input.tdot",literals);

        Parser parser = new Parser();
        TypeCheckVisitor typecheck = new TypeCheckVisitor();

        typecheck.visit(parser.parse());
    }
}
