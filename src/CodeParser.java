import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeParser {
    private final String TEST0 = "打码：8848;序列:4打码时间：00:00:03.9892996";
    private final String TEST = "打码：8848;序列:4打码时间：00:00:03.9892996\n打码：1854;序列:9打码时间：00:00:01.9104948\n打码：1535;序列:11打码时间：00:00:02.1681648";
    private final Pattern pattern = Pattern.compile("打码：(\\d+);序列:(\\d+)打码时间：00:00:03.9892996");
    private String path;

    public CodeParser(String path) {
        this.path = path;
        String parsedLine = parse(TEST0);
        LogUtil.log(parsedLine);
    }

    private String parse(String line) {
        if (!TextUtils.isEmpty(line)) {
            Matcher matcher = pattern.matcher(line);
            boolean find = matcher.find();
            if (find) {
                String result = matcher.group(1);
                String index = matcher.group(2);
                return result + "|" + index;
            }
        }
        return "";
    }
}
