package animalkeeping.ui.widgets;

/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author Andrei Orlov
 * https://github.com/aneagle/javafx-SpecialTextField
 * optimizations by
 * @author Jan Grewe
 */


import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Text field with a constant text length which same as the mask length. Available symbols are set via the mask.
 * Mask symbols is same as in AWT except [*] where excluded an empty character symbol.
 */
public class SpecialTextField extends TextField {
    private static HashMap<Character, Pattern> patternMap = new HashMap<Character, Pattern>(){
        {
            put('#', Pattern.compile("[0-9]"));
            put('?', Pattern.compile("A-z"));
            put('H', Pattern.compile("[0-9A-Fa-f]"));
            put('U', Pattern.compile("A-Z"));
            put('L', Pattern.compile("a-z"));
            put('A', Pattern.compile("[0-9A-z]"));
            put('*', Pattern.compile("."));
        }
    };
    private static final String MASK_NUMBER = "#";
    private static final String MASK_CHARACTER = "?";
    private static final String MASK_HEXADECIMAL = "H";
    private static final String MASK_UPPER_CHARACTER = "U";
    private static final String MASK_LOWER_CHARACTER = "L";
    private static final String MASK_CHAR_OR_NUM = "A";
    private static final String MASK_ANYTHING = "*";

    private static final char EMPTY_CHAR = '_';
    private Pattern maskPattern;
    private final String mask;
    private String text;
    private Set<Character> specialSymbols = new HashSet<>();

    /**
     * Constructor.
     *
     * @param mask - mask expression:
     *             # - any valid number [0-9];
     *             ? - any character [A-z];
     *             H - any hexadecimal character [0-9A-Fa-f];
     *             U - any character. All lowercase character are mapped to upper case;
     *             L - any character. All uppercase character are mapped to lower case;
     *             A - any character or number [0-9A-z];
     *             * - any symbol except a symbol of empty character, i.e. [_]. [^_]
     */
    public SpecialTextField(String mask) {
        super();
        this.mask = mask;
        this.text = textFromMask(specialSymbols);
        this.maskPattern = patternFromMask();
        StringBuilder expression = new StringBuilder("[");
        for (Character c : specialSymbols) {
            expression.append("^").append(c);
        }
        expression.append("^").append(EMPTY_CHAR).append("]");

        this.caretPositionProperty().addListener((observable1, oldValue, newValue) -> {
            int caretPosition = (int) newValue;
            if (caretPosition >= mask.length()) {
                Platform.runLater(() -> positionCaret(mask.length()));
                return;
            }
            if (caretPosition < 0) {
                Platform.runLater(() -> positionCaret(0));
            }
        });

        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && (getText() == null || getText().equals(""))) {
                this.setText(text);
                Platform.runLater(() -> positionCaret(0));
            }
            if (!newValue && getText().equals(textFromMask(null))) {
                this.setText(textFromMask(null));
            }
        });

        this.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null && !newValue.equals("") &&
                    (text == null || !text.equals(newValue))) {
                if (isValid(newValue)) {
                    setText(newValue);
                    this.setStyle("-fx-text-inner-color: black;");
                    return;
                }
                final int caretPosition = getCaretPosition();
                if (caretPosition >= oldValue.length() || isSpecial(oldValue.charAt(caretPosition))) {
                    text = oldValue;
                    setText(text);

                    return;
                }
                char challenger = newValue.charAt(caretPosition);
                Character currentMask = mask.charAt(caretPosition);

                if (currentMask == 'U')
                    challenger = String.valueOf(challenger).toUpperCase().charAt(0);
                if (currentMask == 'L')
                    challenger = String.valueOf(challenger).toLowerCase().charAt(0);

                Pattern currentPattern;
                if (!patternMap.containsKey(currentMask)) {
                    throw new IllegalArgumentException();
                } else {
                    currentPattern = patternMap.get(currentMask);
                }

                Matcher matcher = currentPattern.matcher(String.valueOf(challenger));
                if (matcher.matches()) {
                    text = (replaceInsteadInsertion(newValue, challenger, caretPosition));
                    if (caretPosition + 1 < text.length() && isSpecial(text.charAt(caretPosition + 1))) {
                        Platform.runLater(() -> positionCaret(caretPosition + 2));
                    } else Platform.runLater(() -> positionCaret(caretPosition + 1));
                } else {
                    text = oldValue;
                }
                this.setText(text);
            } else if (newValue != null && newValue.length() == mask.length()) {
                text = newValue;
                this.setText(text);
            }
            if (!isValid(text)) {
                this.setStyle("-fx-text-inner-color: red;");
            } else {
                this.setStyle("-fx-text-inner-color: black;");
            }
        });

        this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getEventType() == KeyEvent.KEY_PRESSED) {
                final int caretPosition = getCaretPosition();
                switch (event.getCode()) {
                    case BACK_SPACE:
                        if (caretPosition != 0) {
                            for (int i = 1; caretPosition - i >= 0; i++) {
                                if (!isSpecial(text.charAt(caretPosition - i))) {
                                    text = replaceCharAtPosition(text, EMPTY_CHAR, caretPosition - i);
                                    this.setText(text);
                                    final int j = i;
                                    Platform.runLater(() -> positionCaret(caretPosition - j));
                                    break;
                                }
                            }
                        }
                        event.consume();
                        break;
                    case DELETE:
                        for (int i = 0; caretPosition + i < text.length(); i++) {
                            if (!isSpecial(text.charAt(caretPosition + i))) {
                                text = replaceCharAtPosition(text, EMPTY_CHAR, caretPosition + i);
                                this.setText(text);
                                final int j = i + 1;
                                Platform.runLater(() -> positionCaret(caretPosition + j));
                                break;
                            }
                        }
                        event.consume();
                        break;
                }
            }
        });
    }

    @Override
    public void paste() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            final String text = clipboard.getString();
            if (text != null && isValid(text)) {
                setText(text);
            }
        }
    }

    private static String replaceInsteadInsertion(String str, char ch, int pos) {
        char[] buffer = new char[str.toCharArray().length];
        for (int i = 0, j = 0; i < buffer.length; i++, j++) {
            if (i != pos) {
                buffer[j] = str.toCharArray()[i];
            } else {
                buffer[j] = ch;
                ++i;
            }
        }
        String result = new String(buffer);
        result = result.substring(0, result.length() - 1);
        return result;
    }

    private static String replaceCharAtPosition(String str, char ch, int pos) {
        char[] buffer = new char[str.toCharArray().length];
        for (int i = 0; i < buffer.length; i++) {
            if (i != pos) buffer[i] = str.toCharArray()[i];
            else buffer[i] = ch;
        }
        str = new String(buffer);
        return str;
    }

    @Override
    public void replaceText(int start, int end, String text) {
        if (start == end && !text.equals("")) super.replaceText(start, end, text);
    }

    /**
     * Method used for check an empty characters.
     *
     * @return true - no empty characters
     */
    public boolean isFilled() {
        for (char ch : text.toCharArray()) {
            if (ch == EMPTY_CHAR) return false;
        }
        return true;
    }

    private String textFromMask(Set<Character> specialSymbols) {
        String tempText = mask.replace(MASK_ANYTHING, String.valueOf(EMPTY_CHAR));
        tempText = tempText.replace(MASK_CHARACTER, String.valueOf(EMPTY_CHAR));
        tempText = tempText.replace(MASK_NUMBER, String.valueOf(EMPTY_CHAR));
        tempText = tempText.replace(MASK_HEXADECIMAL, String.valueOf(EMPTY_CHAR));
        tempText = tempText.replace(MASK_UPPER_CHARACTER, String.valueOf(EMPTY_CHAR));
        tempText = tempText.replace(MASK_LOWER_CHARACTER, String.valueOf(EMPTY_CHAR));
        tempText = tempText.replace(MASK_CHAR_OR_NUM, String.valueOf(EMPTY_CHAR));
        if (specialSymbols != null) {
            for (Character ch : tempText.toCharArray()) {
                if (ch != EMPTY_CHAR) specialSymbols.add(ch);
            }
        }
        return tempText;
    }

    private boolean isSpecial(char character) {
        return specialSymbols.contains(character);
    }

    private Pattern patternFromMask() {
        String pattern = "";
        for (Character c : mask.toCharArray()) {
            pattern += patternMap.containsKey(c) ? patternMap.get(c).toString() : c;
        }
        return Pattern.compile(pattern);
    }

    private Boolean isValid(String string) {
        Matcher matcher = maskPattern.matcher(string);
        return matcher.matches();
    }
}