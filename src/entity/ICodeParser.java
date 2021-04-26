package entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface ICodeParser {
    Pattern pattern();

    String stamp(Matcher matcher);

    String code(Matcher matcher);

    String time(Matcher matcher);
}
