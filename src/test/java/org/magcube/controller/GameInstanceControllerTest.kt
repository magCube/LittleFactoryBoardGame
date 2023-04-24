package org.magcube.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatusCode
import org.springframework.test.web.servlet.MockMvc


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class GameInstanceControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc;

    @Autowired
    private lateinit var template: TestRestTemplate;

    @Test
    @Throws(Exception::class)
    fun getWithoutIdShouldReturn400() {
        val response = template.getForEntity("/game", String::class.java)
        assertThat(response.statusCode == HttpStatusCode.valueOf(400))
    }

    @Test
    @Throws(Exception::class)
    fun getGameInstanceById() {
        val response = template.getForEntity("/game?id=1", String::class.java)
        assertThat(response.statusCode == HttpStatusCode.valueOf(200))
    }

    @Test
    @Throws(Exception::class)
    fun createGameInstance() {
        //TODO: test for the real implementation
        val response = template.postForEntity("/game", "", String::class.java)
        assertThat(response.statusCode == HttpStatusCode.valueOf(200))
    }
}