package cn.bugstack.guide.idea.plugin.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiTest {

    static String str = "package cn.bugstack.test.demo.interfaces.test;\n" +
            "\n" +
            "import com.alibaba.fastjson.JSON;\n" +
            "import com.jd.test.demo.infrastructure.dao.UserDao;\n" +
            "//import com.jd.test.demo.infrastructure.po.*;\n" +
            "import org.junit.Test;\n" +
            "import org.junit.runner.RunWith;\n" +
            "import org.springframework.boot.test.context.SpringBootTest;\n" +
            "import org.springframework.test.context.junit4.SpringRunner;\n" +
            "\n" +
            "import javax.annotation.Resource;\n" +
            "\n" +
            "@RunWith(SpringRunner.class)\n" +
            "@SpringBootTest\n" +
            "public class ApiTest {\n" +
            "\n" +
            "    @Resource\n" +
            "    private UserDao userDao;\n" +
            "\n" +
            "//    @Test\n" +
            "//    public void test_findById(){\n" +
            "//        User user = userDao.findById(1L);\n" +
            "//        System.out.println(JSON.toJSON(user));\n" +
            "//    }\n" +
            "//\n" +
            "//    @Test\n" +
            "//    public void test_update(){\n" +
            "//        User user = new User();\n" +
            "//        user.setId(1L);\n" +
            "//        user.setName(\"谢飞机\");\n" +
            "//        user.setAge(18);\n" +
            "//        user.setAddress(\"北京.大兴区.亦庄经济开发区\");\n" +
            "//\n" +
            "//        userDao.update(user);\n" +
            "//    }\n" +
            "\n" +
            "    public void t_vo2dto(cn.bugstack.test.demo.interfaces.test.bbb.User user){\n" +
            "        UserDTO userDTO = new UserDTO();\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "    public void t_vo2dto(User user){\n" +
            "        UserDTO userDTO = new UserDTO();\n" +
            "\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "}\n" +
            "\n";

    static String str2 = "public static  final String x = 00;";
    static String str3 = "public final  static String x = 00;";
    static String str4 = "public  static String x = 00;";

    public static void main(String[] args) {
        Pattern p = Pattern.compile("static.*?final|final.*?static");
        Matcher m = p.matcher(str4);

        System.out.println(m.find());

        while(m.find()){
            System.out.println(m.group(0));
        }


    }

}
