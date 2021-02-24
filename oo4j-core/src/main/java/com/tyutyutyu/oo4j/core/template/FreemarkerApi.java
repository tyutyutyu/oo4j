package com.tyutyutyu.oo4j.core.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.StringWriter;

@Slf4j
public class FreemarkerApi {

    private final Configuration cfg;

    @SneakyThrows
    public FreemarkerApi(String templateDir, boolean inClasspath) {

        cfg = new Configuration(Configuration.VERSION_2_3_29);

        if (inClasspath) {
            cfg.setClassForTemplateLoading(this.getClass(), templateDir);
        } else {
            cfg.setDirectoryForTemplateLoading(new File(templateDir));
        }
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
    }

    @SneakyThrows
    public String generate(String templateName, Object dataModel) {

        log.debug("generate - templateName: {}, dataModel: {}", templateName, dataModel);

        Template template = cfg.getTemplate(templateName + ".ftl");

        String result;
        try (StringWriter out = new StringWriter()) {
            template.process(dataModel, out);
            result = out.toString();
        }

        return result;
    }

}
