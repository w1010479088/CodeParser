import presenter.CodeParser;
import utils.BasePage;

import java.util.regex.Pattern;

public class Tester {
    private static final String PATTERN = "2020/12/19 11:29:28|    打码：8848;序列:4打码时间：00:00:03.9892996";
    private static final Pattern pattern = Pattern.compile("11:29:(\\d+)\\|    打码：(\\d+);序列:(\\d+)打码时间：00:00:(\\d+).(\\d+)");

    public static void main(String[] args) {
        new BasePage() {
            @Override
            protected void action(String path) {
                new CodeParser(path, pattern, new CodeParser.OnParseListener() {
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
}
