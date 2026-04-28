package com.michele.mocks.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> home() {
        String html = """
                <!DOCTYPE html>
                <html lang=\"en\">
                <head>
                    <meta charset=\"UTF-8\" />
                    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />
                    <title>Seedcaft API</title>
                </head>
                <body>
                    <h1>Seedcaft API</h1>
                    <p>Welcome to the API service.</p>
                    <p><a href=\"/swagger-ui/index.html\">Open Swagger Docs</a></p>
                </body>
                </html>
                """;

        return ResponseEntity.ok(html);
    }
}
