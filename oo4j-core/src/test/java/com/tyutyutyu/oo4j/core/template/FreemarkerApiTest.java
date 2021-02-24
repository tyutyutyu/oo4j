package com.tyutyutyu.oo4j.core.template;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FreemarkerApiTest {

    @Test
    void testGenerate() throws IOException {

        // given
        String templateDir = "/test-templates/";
        FreemarkerApi freemarkerApi = new FreemarkerApi(templateDir, true);

        // when
        String actual = freemarkerApi.generate("test1", Map.of("a", "b"));

        // then
        assertThat(actual).isEqualTo("a = b");
    }

}
