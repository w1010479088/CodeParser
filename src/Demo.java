import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Demo {
    public static void main(String[] args) throws Exception {
//        testTime();
        code();
    }

    private static void code() {
        String code = "11:29:58.474   2021.04.24 11:29:58输入437，完成时间2秒，1619234996910-601236-2";
        match(code, "11:(\\d+):(\\d+)输入(\\d+)，完成时间(\\d+).?(\\d+)?秒");
    }

    private static void testTime() {
        match("102.368686986859686", "[\\d]*[\\.][\\d]{2}");
    }

    private static void match(String content, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(content);
        // 查找相应的字符串
        while (m.find()) {
            String tmp = m.group();
            System.out.println(tmp);
        }
    }
}
