package presenter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entity.CodeEntity;
import utils.TextUtil;
import utils.ThreadPool;

public class CodeParser {
    private static final int MAX = 2;
    private static final int INDEX_AT_TIME_SEC = 1;
    private static final int INDEX_CODE = 2;
    private static final int INDEX_IMG = 3;
    private static final int INDEX_TIME_SEC = 4;
    private static final int INDEX_TIME_MIL = 5;
    private final String PATTERN = "2020/12/19 11:29:28|    打码：8848;序列:4打码时间：00:00:03.9892996";
    private final Pattern pattern = Pattern.compile("11:29:(\\d+)\\|    打码：(\\d+);序列:(\\d+)打码时间：00:00:(\\d+).(\\d+)");
    private final List<CodeEntity> lineResult = new ArrayList<>();
    private String path;
    private OnParseListener listener;

    public CodeParser(String path, OnParseListener listener) {
        this.path = path;
        this.listener = listener;
        start();
    }

    private void start() {
        ThreadPool.execute(() -> {
            BufferedReader reader = null;
            try {
                List<CodeEntity> items = new ArrayList<>();
                reader = new BufferedReader(new FileReader(path));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    String key = "GB2312";
                    String encodedLine = new String(line.getBytes(key), key);
                    List<CodeEntity> lineItems = parse(encodedLine);
                    if (!lineItems.isEmpty()) {
                        items.addAll(lineItems);
                    }
                }
                listener.onResult(concat(items));
            } catch (Exception ex) {
                error(ex);
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception ex) {
                    error(ex);
                }
            }
        });
    }

    private List<CodeEntity> parse(String line) {
        lineResult.clear();
        if (!TextUtil.isEmpty(line)) {
//            listener.onResult(line);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                String result = matcher.group(INDEX_CODE);
                String index = matcher.group(INDEX_IMG);
                String timeSecond = matcher.group(INDEX_TIME_SEC);
                String timeRemain = matcher.group(INDEX_TIME_MIL);
                String time = String.format("11:29:%s", matcher.group(INDEX_AT_TIME_SEC));
                if (!TextUtil.isEmpty(timeRemain) && timeRemain.length() > MAX) {
                    timeRemain = timeRemain.substring(0, MAX);
                }
                lineResult.add(new CodeEntity(index, result, String.format("%s.%s", timeSecond, timeRemain), time));
            }
//            listener.onResult(lineResult.isEmpty() ? "该行未发现Code" : "该行有Code!");
        }
        return lineResult;
    }

    private String concat(List<CodeEntity> items) {
        if (items.isEmpty()) {
            return "暂未发现Code!";
        } else {
            StringBuilder index = new StringBuilder();
            StringBuilder code = new StringBuilder();
            StringBuilder timeLong = new StringBuilder();
            StringBuilder time = new StringBuilder();
            for (CodeEntity item : items) {
                index.append(item.index);
                index.append("\t");

                code.append(item.code);
                code.append("\t");

                timeLong.append(item.timeLong);
                timeLong.append("\t");

                time.append(item.time);
                time.append("\t");
            }
            return String.format("%s\n%s\n%s\n%s", index.toString(), code.toString(), timeLong.toString(), time.toString());
        }
    }

    private void error(Exception ex) {
        listener.onResult(ex.getMessage());
    }

    public interface OnParseListener {
        void onResult(String result);
    }
}
