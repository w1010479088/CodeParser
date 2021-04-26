package entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.TextUtil;

public enum CodeParser implements ICodeParser {
    //        11:29:58.474   2021.04.24 11:29:58输入437，完成时间2秒，1619234996910-601236-2
    Mode1("11:29:22输入367，完成时间2.5秒", Pattern.compile("11:(\\d+):(\\d+)输入(\\d+)，完成时间(\\d+).?(\\d+)?秒"));

    private String demo;
    private Pattern pattern;

    @Override
    public Pattern pattern() {
        return pattern;
    }

    @Override
    public String stamp(Matcher matcher) {
        return String.format("11:%s:%s", matcher.group(1), matcher.group(2));
    }

    @Override
    public String code(Matcher matcher) {
        return matcher.group(3);
    }

    @Override
    public String time(Matcher matcher) {
        String timeSecond = matcher.group(4);
        String timeMillSecond = matcher.group(5);
        String timeFixed;
        if (TextUtil.isEmpty(timeMillSecond)) {
            timeFixed = timeSecond;
        } else {
            timeFixed = String.format("%s.%s", timeSecond, timeMillSecond);
        }
        try {
            if (Integer.parseInt(timeSecond) >= 5) {
                timeFixed = String.format("超时%s", timeFixed);
            }
        } catch (Exception ex) {
            //ignore
        }
        return timeFixed;
    }

    CodeParser(String demo, Pattern pattern) {
        this.demo = demo;
        this.pattern = pattern;
    }
}
