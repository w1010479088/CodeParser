import presenter.CodeParser;
import utils.BasePage;

public class Tester {
    public static void main(String[] args) {
        new BasePage() {
            @Override
            protected void action(String path) {
                new CodeParser(path, new CodeParser.OnParseListener() {
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
