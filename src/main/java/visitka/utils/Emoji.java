package visitka.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.Getter;

public enum Emoji {
    MINUS(":heavy_minus_sign:"),
    BANANA(":banana:");

    private final String value;

    public String emoji() {
        return EmojiParser.parseToUnicode(value);
    }

    Emoji(String value){
        this.value = value;
    }
}
