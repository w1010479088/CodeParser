import presenter.CodeParser;
import utils.BasePage;

public class Tester {
    public static void main(String[] args) {
        new BasePage() {
            @Override
            protected void action(String path) {
                log("解析开始!");
                new CodeParser(path, this::log);
                log("解析结束!");
            }

            @Override
            protected String title() {
                return "Code解析";
            }
        }.showUI();
    }
}
