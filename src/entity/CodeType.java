package entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum CodeType implements ICoder {
    //        11:29:58.474   2021.04.24 11:29:58输入437，完成时间2秒，1619234996910-601236-2
    Mode1("11:29:22输入367，完成时间2.5秒", Pattern.compile("(11:\\d+:\\d+)输入(\\d+)，完成时间(\\d+.?\\d+?)秒")),
    Mode2("11:29:22输入367，完成时间2.5秒", Pattern.compile("(11:\\d+:\\d+)输入(\\d+)，完成时间(\\d+)秒"));

    private String demo;
    private Pattern pattern;

    @Override
    public Pattern pattern() {
        return pattern;
    }

    @Override
    public String stamp(Matcher matcher) {
        return matcher.group(1);
    }

    @Override
    public String code(Matcher matcher) {
        return matcher.group(2);
    }

    @Override
    public String time(Matcher matcher) {
        String time = matcher.group(3);
        if (Float.parseFloat(time) >= 4.99) {
            return String.format("超时%s", time);
        } else {
            return time;
        }
    }

    CodeType(String demo, Pattern pattern) {
        this.demo = demo;
        this.pattern = pattern;
    }
}
