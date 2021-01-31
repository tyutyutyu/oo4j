package com.tyutyutyu.oo4j.core.template;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FreemarkerApiTest {

    @Test
    void testGenerate() throws IOException {

        // given
        File templateDir = new ClassPathResource("/test-templates/").getFile();
        FreemarkerApi freemarkerApi = new FreemarkerApi(templateDir);

        // when
        String actual = freemarkerApi.generate("test1", Map.of("a", "b"));

        // then
        assertThat(actual).isEqualTo("a = b");
    }

}
