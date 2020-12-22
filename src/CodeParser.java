import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeParser {
    private static final int MAX = 2;
    private final Pattern pattern = Pattern.compile("打码：(\\d+);序列:(\\d+)打码时间：00:00:(\\d+).(\\d+)");
    private final StringBuilder builder = new StringBuilder();
    private final String RESULT = "8848\t1264\t1860\t3465\t1387\t1854\t6189\t1535\n" +
            "3.99\t2.57\t3.44\t3.30\t2.03\t1.91\t1.91\t2.16";
    private String path;
    private OnParseListener listener;

    public CodeParser(String path, OnParseListener listener) {
        this.path = path;
        this.listener = listener;
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
                    log(parsedResult);
                }
            }
        } catch (Exception ex) {
            log(ex.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception ex) {
                log(ex.getMessage());
            } finally {
                listener.onResult(builder.toString());
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

    private void log(String content) {
        builder.append(content);
        builder.append("\n");
    }

    public interface OnParseListener {
        void onResult(String result);
    }
}
