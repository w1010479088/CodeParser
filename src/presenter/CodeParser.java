package presenter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
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
    private final String rootPath;
    private final OnParseListener listener;

    public CodeParser(String rootPath, OnParseListener listener) {
        this.rootPath = rootPath;
        this.listener = listener;
        start();
    }

    private void start() {
        ThreadPool.execute(() -> {
            listener.onStart();
            List<String> logPaths = parseValidLogs(rootPath);
            if (logPaths.isEmpty()) {
                listener.onResult("未发现有效的logs文件夹");
            } else {
                for (String path : logPaths) {
                    parseAccount(path);
                }
            }
            listener.onFinish();
        });
    }

    private List<String> parseValidLogs(String path) {
        List<String> paths = new ArrayList<>();
        File file = new File(path);
        if (file.isFile()) {
            if (isLogFile(file)) {
                paths.add(path);
            }
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subItem : files) {
                    List<String> subPaths = parseValidLogs(subItem.getPath());
                    paths.addAll(subPaths);
                }
            }

        }
        return paths;
    }

    private void parseAccount(String path) {
        File file = new File(path);
        String name = file.getName();
        listener.onResult("账号：" + name);
        BufferedReader reader = null;
        try {
            List<CodeEntity> items = new ArrayList<>();
            reader = new BufferedReader(new FileReader(path, StandardCharsets.UTF_8));
            String line = null;
            while ((line = reader.readLine()) != null) {
                List<CodeEntity> lineItems = parse(line);
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
            } finally {
                listener.onResult("\n");
            }
        }
    }

    private List<CodeEntity> parse(String line) {
        List<CodeEntity> lineResult = new ArrayList<>();
        if (!TextUtil.isEmpty(line)) {
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

    private boolean isLogFile(File file) {
        return file.getName().endsWith(".log");
    }

    private void error(Exception ex) {
        listener.onResult(ex.getMessage());
    }

    public interface OnParseListener {
        void onStart();

        void onResult(String result);

        void onFinish();
    }
}
