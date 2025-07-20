package visitka.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.Getter;

public enum Emoji {
    MINUS(":heavy_minus_sign:"),
    SPEAKING_HEAD_IN_SILHOUETTE(":speaking_head_in_silhouette:"),
    SMIRK(":smirk:"),
    MAN_DANCING(":man_dancing:"),
    LINKED_PAPERCLIPS(":linked_paperclips:"),
    GAME_DIE(":game_die:"),
    FACE_WITH_SYMBOLS_ON_MOUTH(":face_with_symbols_on_mouth:"),
    CLOWN_FACE(":clown_face:"),
    SOB(":sob:"),
    UNAMUSED(":unamused:"),
    BANANA(":banana:");

    private final String value;

    public String emoji() {
        return EmojiParser.parseToUnicode(value);
    }

    Emoji(String value){
        this.value = value;
    }
}
