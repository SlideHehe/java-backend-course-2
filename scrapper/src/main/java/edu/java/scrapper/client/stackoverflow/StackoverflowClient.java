package edu.java.scrapper.client.stackoverflow;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface StackoverflowClient {
    @GetExchange("/questions/{id}?site=stackoverflow")
    StackoverflowQuestion getQuestion(@PathVariable Long id);
}
