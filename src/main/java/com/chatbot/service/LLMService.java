package com.chatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LLMService {

    @Value("${groq.api.key}")
    private String apiKey;

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    private static final String SYSTEM_PROMPT = """
        You are an expert at converting natural language to SQL.
        Only use this table: customer with columns: id, name, gender, location, ph_number.
        Only return a valid SELECT SQL query. No explanations, no extra symbols, and no formatting — just the raw SQL query.

        If the user's input cannot be understood or mapped to a valid SELECT query, respond only with: INVALID_QUERY
        """;

    @SuppressWarnings("unchecked")
    public String convertToSQL(String nlQuery) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama3-70b-8192");

   
        Object[] messages = new Object[]{
                Map.of("role", "system", "content", SYSTEM_PROMPT),
                Map.of("role", "user", "content", nlQuery)
        };
        body.put("messages", messages);
        body.put("temperature", 0.2);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // Call Groq API
        ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_URL, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> choice = choices.get(0);
            Map<String, String> message = (Map<String, String>) choice.get("message");

            return message.get("content").trim();
        } else {
            throw new RuntimeException("Groq API error: " + response.getStatusCode());
        }
    }
}
