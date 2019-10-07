package tokenizer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Tokenizer {
    private static String program;
    private static List<String> literals;
    private ArrayList<String> tokens = new ArrayList<>();

    private int currentToken;
    private static Tokenizer theTokenizer;

    private static String NO_MORE_TOKENS = "No More Tokens Available";

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
//                .replace("\t", "");
        System.out.println(program);

        for (String s : literals){
            tokenizedProgram = tokenizedProgram.replace("\\s" + s + "\\s","\""+ s +"\"");
        }
        System.out.println(tokenizedProgram);
        String[] documents = tokenizedProgram.split("(?=create)");
        for(String doc: documents){
            String[] header = doc.split(" ", 3);
            Collections.addAll(tokens, header[0], header[1]);
            System.out.println(header);
            String[] tail = header[2].split(": ");
            tokenizeChild(tail);
        }
        System.out.println(Arrays.asList(tokens));
    }

    public static void createTokenizer(String filename, List<String> literals) {
        if (theTokenizer == null) {
            theTokenizer = new Tokenizer(filename, literals);
        }
    }

    public static Tokenizer getTokenizer(){
        return theTokenizer;
    }

    public static void makeTokenizer(String filename, List<String> literals){
        if (theTokenizer == null){
            theTokenizer = new Tokenizer(filename,literals);
        }
    }

    private void tokenizeChild(String[] childNodes){
        for (String child: childNodes){
//            String[] t1 = child.split("\\s*\"\\s*|\\s*},\\s*|\\s*,\\s*|\\s*}}\\s*|\\s*\"\\s*|\\s*\\{\\s*|}|"+
//                    "(?=to)");
            String[] t1 = child.split("\\s(?=(?:[^'\"`]*(['\"`])[^'\"`]*\\1)*[^'\"`]*$)|,|(?=\\{\")|" +
                    "(?=})");
            for (String str : t1){
                if (literals.contains(str) || str.matches("\"[^\"]*\"[^,]")){
                    tokens.add(str);
                    tokens.add(":");
                } else {
                    tokens.add(str);

                }
            }
        }
        tokens.removeAll(Arrays.asList("", null));
    }


    public boolean hasMoreTokens(){
        return currentToken < tokens.size();
    }

    public String viewNextToken(){
        String token;
        if (currentToken < tokens.size()){
            token = tokens.get(currentToken);
        }
        else
            return NO_MORE_TOKENS;
        return token;
    }

    public String nextToken(){
        String token;
        if (hasMoreTokens()){
            token = tokens.get(currentToken);
            currentToken++;
        }
        else{
            return NO_MORE_TOKENS;
        }
        return token;
    }


    public boolean checkTokenValue(String regexValue){
        String s = viewNextToken();
        System.out.println("comparing: "+ s + "  to  "+ regexValue);
        return (s.matches(regexValue));
    }

    public String getAndCheckTokenValue(String regexValue){
        String s = nextToken();
        if (!s.matches(regexValue)) System.exit(0);
        System.out.println("matched: "+s+"  to  "+ regexValue);
        return s;
    }

    public ArrayList<String> getAllTokens() {
        return tokens;
    }

    public int countTokens(){
        return tokens.size() - currentToken;
    }
}
