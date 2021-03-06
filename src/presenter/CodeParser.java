package presenter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import entity.CodeEntity;
import entity.CodeType;
import utils.TextUtil;
import utils.ThreadPool;

public class CodeParser {
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
        String name = file.getAbsolutePath();
        listener.onResult("账号：" + name);
        InputStreamReader isr = null;
        BufferedReader reader = null;
        try {
            List<CodeEntity> items = new ArrayList<>();
            isr = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
            reader = new BufferedReader(isr);
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
                if (isr != null) {
                    isr.close();
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
            for (CodeType type : CodeType.values()) {
                Matcher matcher = type.pattern().matcher(line);
                if (matcher.find()) {
                    return new CodeEntity(type.code(matcher), type.time(matcher), type.stamp(matcher));
                }
            }
        }
        return null;
    }

    private String concat(List<CodeEntity> items) {
        if (items.isEmpty()) {
            return "暂未发现Code!";
        } else {
            StringBuilder code = new StringBuilder();
            StringBuilder timeLong = new StringBuilder();
            StringBuilder time = new StringBuilder();
            for (CodeEntity item : items) {
                code.append(item.code);
                code.append("\t");
                timeLong.append(item.time);
                timeLong.append("\t");

                time.append(item.stamp);
                time.append("\t");
            }
            return String.format("%s\n%s\n%s", code.toString(), timeLong.toString(), time.toString());
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
