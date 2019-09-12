package byow.Core;

public class InputString implements Inputs {
    private String input;
    private int index;

    public InputString(String s) {
        input = s.toUpperCase();
        index = 0;
    }

    public char getNextKey() {
        char c = input.charAt(index);
        index += 1;
        return c;
    }

    public boolean possibleNextInput() {
        return index < input.length();
    }
}
