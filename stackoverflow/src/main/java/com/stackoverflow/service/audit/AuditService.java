package com.stackoverflow.service.audit;

import com.stackoverflow.bo.Audit;
import com.stackoverflow.util.LoggerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuditService {

    @Value("${rest.audit.api.url}")
    private String apiUrl;

    private final WebClient webClient = WebClient.create();

    public void auditPost(Audit audit) {
        webClient.post()
                .uri(apiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(audit), Audit.class)
                .retrieve()
                .bodyToMono(Audit.class)
                .doOnSuccess(result -> LoggerService.loggerDebug("Audit saved successfully"))
                .doOnError(e -> LoggerService.loggerDebug("Error saved audit"))
                .subscribe();
    }
}
