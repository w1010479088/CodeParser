import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeParser {
    private static final int MAX = 2;
    private final Pattern pattern = Pattern.compile("打码：(\\d+);序列:(\\d+)打码时间：00:00:(\\d+).(\\d+)");
    private String path;

    public CodeParser(String path) {
        this.path = path;
        start();
    }

    private void start() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String parsedResult = parse(line);
                if (!TextUtils.isEmpty(parsedResult)) {
                    LogUtil.log(parsedResult);
                }
            }
        } catch (Exception ex) {
            LogUtil.log(ex.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception ex) {
                LogUtil.log(ex.getMessage());
            }
        }
    }

    private String parse(String line) {
        if (!TextUtils.isEmpty(line)) {
            Matcher matcher = pattern.matcher(line);
            StringBuilder builder = new StringBuilder();
            while (matcher.find()) {
                String result = matcher.group(1);
                String index = matcher.group(2);
                String timeSecond = matcher.group(3);
                String timeRemain = matcher.group(4);
                if (!TextUtils.isEmpty(timeRemain) && timeRemain.length() > MAX) {
                    timeRemain = timeRemain.substring(0, MAX);
                }
                builder.append(String.format("序号:%s, 打码:%s, 时间:%s.%s", index, result, timeSecond, timeRemain));
            }
            return builder.toString();
        }
        return "";
    }
}
