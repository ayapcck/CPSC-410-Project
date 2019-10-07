import tokenizer.Tokenizer;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        List<String> literals = Arrays.asList("create", "date", "expenses", "account_balance", "budget", "track", "weight",
                "count");
        Tokenizer.makeTokenizer("/Users/meghasinghania/Documents/School/CPSC410/CPSC-410-Project/frontend/src/main/input.tdot", literals);
    }
}
