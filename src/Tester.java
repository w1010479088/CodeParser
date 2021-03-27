import presenter.CodeParser;
import utils.BasePage;

import java.util.regex.Pattern;

public class Tester {
    public static void main(String[] args) {
        new BasePage() {
            @Override
            protected void action(String path) {
                new CodeParser(path, CodeType.NEW.pattern(), new CodeParser.OnParseListener() {
                    @Override
                    public void onStart() {
                        log("解析开始!");
                    }

                    @Override
                    public void onResult(String result) {
                        log(result);
                    }

                    @Override
                    public void onFinish() {
                        log("解析结束!");
                    }
                });

            }

            @Override
            protected String title() {
                return "Code解析";
            }
        }.showUI();
    }

    public enum CodeType {
        OLD("2020/12/19 11:29:28|    打码：8848;序列:4打码时间：00:00:03.9892996", Pattern.compile("11:29:(\\d+)\\|    打码：(\\d+);序列:(\\d+)打码时间：00:00:(\\d+).(\\d+)")),
        NEW("11:29:22输入367，完成时间2.5秒", Pattern.compile("11:29:(\\d+)输入(\\d+)，完成时间(\\d+).(\\d+)秒"));

        private String demo;
        private Pattern pattern;

        public Pattern pattern() {
            return pattern;
        }

        CodeType(String demo, Pattern pattern) {
            this.demo = demo;
            this.pattern = pattern;
        }
    }
}
