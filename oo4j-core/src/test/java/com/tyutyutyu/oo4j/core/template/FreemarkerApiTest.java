package com.tyutyutyu.oo4j.core.template;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class FreemarkerApiTest {

    @Test
    void testGenerate() {

        // given
        String templateDir = "classpath:/test-templates/";
        FreemarkerApi freemarkerApi = new FreemarkerApi(templateDir);

        // when
        String actual = freemarkerApi.generate("test1", Map.of("a", "b"));

        // then
        assertThat(actual).isEqualTo("a = b");
    }

}
