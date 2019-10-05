package tokenizer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Tokenizer {
    private static String program;
    private static List<String> literals;
    private static List<String> tokens;
    private static Tokenizer theTokenizer;

    private Tokenizer(String filename, List<String> literalsList) {
        literals = literalsList;
        try {
            program = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Could not find the file! Please check the filename again.");
            System.exit(0);
        }
        tokenize();
    }

    private void tokenize() {
        String tokenizedProgram = program;

        // Removing all new line characters
        tokenizedProgram = tokenizedProgram.replace("\n","");

        for (String s : literals){
            tokenizedProgram = tokenizedProgram.replace(s,"_"+ s +"_");
        }


    }

    public static void createTokenizer(String filename, List<String> literals) {
        if (theTokenizer == null) {
            theTokenizer = new Tokenizer(filename, literals);
        }
    }

    public static Tokenizer getTokenizer(){
        return theTokenizer;
    }
}
