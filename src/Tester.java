import presenter.CodeParser;
import utils.BasePage;

public class Tester {
    public static void main(String[] args) {
        new BasePage() {
            @Override
            protected void action(String path) {
                new CodeParser(path, this::log);
            }

            @Override
            protected String title() {
                return "Code解析";
            }
        }.showUI();
    }
}
