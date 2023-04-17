package org.magcube.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.web.servlet.MockMvc


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BaseControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc;

    @Autowired
    private lateinit var template: TestRestTemplate;

    @Test
    @Throws(Exception::class)
    fun getHello() {
        val response = template.getForEntity("/", String::class.java)
        assertThat(response.body).isEqualTo( "Greetings from Little Factory Spring boot controller!")
    }
}