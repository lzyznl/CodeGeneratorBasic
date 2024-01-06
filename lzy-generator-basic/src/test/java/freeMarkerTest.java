import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author lzy
 * @date 2024-01-06
 * FreeMarker模板引擎测试类
 */
public class freeMarkerTest {


    @Test
    public void test() throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDirectoryForTemplateLoading(new File("src/main/resources/template"));
        cfg.setDefaultEncoding("UTF-8");
        //获取到对应的模板
        Template template = cfg.getTemplate("testFreeMarker.html.ftl");
        //向模板中投喂数据
        Map<String, Object> root = new HashMap<>();
        root.put("user","lzy");
        Map<String,Object> latestProduct = new HashMap<>();
        latestProduct.put("url","https://github.com/lzyznl");
        latestProduct.put("name","程序员小刘的Github");
        root.put("latestProduct",latestProduct);
        //指定模板引擎生成的文件路径
        String projectPath = System.getProperty("user.dir");
        Writer output = new FileWriter("testFreeMarker.html");
        template.process(root,output);
        output.close();
    }
}
