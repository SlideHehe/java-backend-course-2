package edu.java.scrapper.client.stackoverflow;

import edu.java.scrapper.client.stackoverflow.dto.StackoverflowQuestion;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface StackoverflowClient {
    @GetExchange("/questions/{id}?site=stackoverflow")
    StackoverflowQuestion getQuestion(@PathVariable Long id);
}
