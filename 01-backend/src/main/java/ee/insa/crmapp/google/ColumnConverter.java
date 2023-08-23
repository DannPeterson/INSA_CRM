package ee.insa.crmapp.google;

public class ColumnConverter {
    public static int letterToIndex(String letter) {
        return letter.toUpperCase().charAt(0) - 'A';
    }

    public static String indexToLetter(int index) {
        return String.valueOf((char) (index + 'A'));
    }
}