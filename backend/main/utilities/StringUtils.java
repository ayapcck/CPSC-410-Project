package utilities;

public class StringUtils {

    public static String capitalizeSentence(String sentence) {
        return sentence.substring(0,1).toUpperCase()
                + sentence.substring(1);
    }

}
