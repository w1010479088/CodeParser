package presenter;

import java.io.BufferedReader;
import java.io.File;
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
    //    private static final int INDEX_IMG = 3;
    private static final int INDEX_TIME_SEC = 3;
    private static final int INDEX_TIME_MIL = 4;
    private final String rootPath;
    private final OnParseListener listener;
    private final Pattern pattern;
    private final Pattern pattern2;

    public CodeParser(String rootPath, Pattern pattern, Pattern pattern2, OnParseListener listener) {
        this.rootPath = rootPath;
        this.pattern = pattern;
        this.pattern2 = pattern2;
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
        String name = file.getAbsolutePath();
        listener.onResult("账号：" + name);
        BufferedReader reader = null;
        try {
            List<CodeEntity> items = new ArrayList<>();
            reader = new BufferedReader(new FileReader(path));
            String line;
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
            CodeEntity codeEntity = parseLine(line);
            if (codeEntity != null) {
                lineResult.add(codeEntity);
            }
        }
        return lineResult;
    }

    private CodeEntity parseLine(String line) {
        {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String result = matcher.group(INDEX_CODE);
                String timeSecond = String.valueOf(Integer.parseInt(matcher.group(INDEX_TIME_SEC)));
                String timeRemain = matcher.group(INDEX_TIME_MIL);
                String time = String.format("11:29:%s", matcher.group(INDEX_AT_TIME_SEC));
                if (!TextUtil.isEmpty(timeRemain) && timeRemain.length() > MAX) {
                    timeRemain = timeRemain.substring(0, MAX);
                }
                return new CodeEntity("未知", result, String.format("%s.%s", timeSecond, timeRemain), time);
            }
        }

        {
            Matcher matcher = pattern2.matcher(line);
            if (matcher.find()) {
                String result = matcher.group(INDEX_CODE);
                String timeSecond = String.valueOf(Integer.parseInt(matcher.group(INDEX_TIME_SEC)));
                String time = String.format("11:29:%s", matcher.group(INDEX_AT_TIME_SEC));
                return new CodeEntity("未知", result, String.format("%s.%s", timeSecond, "0"), time);
            }
        }

        return null;
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
