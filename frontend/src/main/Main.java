import java.util.Arrays;
import java.util.List;

import ast.Program;
import parser.Parser;
import tokenizer.Tokenizer;
import visitor.EvaluateVisitor;
import typecheck.TypeCheckVisitor;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, here we go!");
        List<String> literals = Arrays.asList(" create ", " spreadsheet ", " sheet ", " for ", " add ", " track ", " projected expenses ",
                " monthly ", " trends ", " budget ", " expenses ", " starting savings ", " start ", " end ", " course ",
                " rows ", " columns ", "[", "]", "(", ")", ",", " sheet ", " account ", " balance ", " expense ", " is ");
        Tokenizer.createTokenizer("input", literals);

        Parser parser = new Parser();
        Program program = parser.parse();
        EvaluateVisitor ev = new EvaluateVisitor();
        program.accept(ev);
        TypeCheckVisitor typeCheck = new TypeCheckVisitor();

        typeCheck.visit(parser.parse());
    }
}
