package edu.java.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RateLimitTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.getCache("rate-limit-buckets").clear();
    }

    @Test
    @DisplayName("Проверка отсутствия 429 кода")
    void rateLimitTestNoLimitExceeded() throws Exception {
        // when-then
        mockMvc.perform(post("/")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Проверка наличия 429 кода")
    void rateLimitTestTooManyRequestsException() throws Exception {
        // when-then
        mockMvc.perform(post("/"));
        mockMvc.perform(post("/")).andExpect(status().isTooManyRequests());
    }

    @DynamicPropertySource
    static void bucket4jProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cache.cache-names[0]", () -> "rate-limit-buckets");
        registry.add("spring.cache.caffeine.spec", () -> "maximumSize=100000,expireAfterAccess=3600s");
        registry.add("bucket4j.enabled", () -> true);
        registry.add("bucket4j.filters[0].cache-name", () -> "rate-limit-buckets");
        registry.add("bucket4j.filters[0].url", () -> ".*");
        registry.add("bucket4j.filters[0].rate-limits[0].cache-key", () -> "getRemoteAddr()");
        registry.add("bucket4j.filters[0].rate-limits[0].bandwidths[0].capacity", () -> 1);
        registry.add("bucket4j.filters[0].rate-limits[0].bandwidths[0].capacity", () -> 1);
        registry.add("bucket4j.filters[0].rate-limits[0].bandwidths[0].time", () -> 1);
        registry.add("bucket4j.filters[0].rate-limits[0].bandwidths[0].unit", () -> "hours");
    }
}
