package common;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author: scyang
 * @program: tensquare_parent
 * @package: common
 * @date: 2020-07-13 23:19:35
 * @describe: 邮件工具类
 */
public class FreemarkerUtil {

    private final static String ENCODING = "UTF-8";

    public static String parse(String templateName, String templateContent, Map<String, Object> root, String encoding) {
        String outputContent;
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);

        String templateKey = "template";
        if (templateName != null && templateName.startsWith("/")) {
            templateKey = templateName.substring(1);
        }
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        templateLoader.putTemplate(templateKey, templateContent);
        cfg.setTemplateLoader(templateLoader);
        try (Writer writer = new StringWriter()) {
            Template template = cfg.getTemplate(templateKey, encoding == null ? ENCODING : encoding);
            template.process(root, writer);
            writer.flush();
            outputContent = writer.toString();
        } catch (IOException e) {
            throw new RuntimeException("模板加载出错：" + e.getMessage());
        } catch (TemplateException e) {
            throw new RuntimeException("模板输出出错：" + e.getMessage());
        }
        return outputContent;
    }

    public static String parse(String templateContent, Map<String, Object> root) {
        return parse(String.valueOf(root.hashCode()), templateContent, root, null);
    }

    /**
     * 读取模板为文本
     *
     * @param ftlName 例如：ftl/batch-job-failure.html
     * @return
     */
    public static String getTemplateContentWithFile(String ftlName) {
        String templateContent = null;
        try {
            System.out.println("mypath = " + FreemarkerUtil.class.getClassLoader().getResource(ftlName));
            byte[] bytes = Files.readAllBytes(Paths.get(FreemarkerUtil.class.getClassLoader().getResource(ftlName).toURI()));
            templateContent = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("读取模板异常：", e);
        }
        return templateContent;

    }

    public static String getTemplateContent(String ftlName) {
        String templateContent = null;
        try (InputStream inputStream = FreemarkerUtil.class.getResourceAsStream(ftlName)) {
            byte[] templateBytes = new byte[inputStream.available()];
            inputStream.read(templateBytes);
            templateContent = new String(templateBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return templateContent;
    }
}


