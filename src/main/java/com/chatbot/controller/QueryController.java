package com.chatbot.controller;
import com.chatbot.dto.QueryRequest;
import com.chatbot.dto.QueryResponse;
import com.chatbot.service.LLMService;
import com.chatbot.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/query")
@CrossOrigin(origins = "*")
public class QueryController {

    @Autowired
    private LLMService llmService;

    @Autowired
    private QueryService queryService;

    private static final Set<String> GREETINGS = Set.of(
            "hi", "hello", "hey", "hii", "helloo", "yo", "what's", "hola", "namaste", "sup", "greetings", "bonjour", "howdy"
    );

    @PostMapping
    public ResponseEntity<QueryResponse> handleQuery(@RequestBody QueryRequest request) {
        String nlQuery = request.getQuery().trim().toLowerCase();
        QueryResponse response = new QueryResponse();

        if (nlQuery.isEmpty()) {
            response.setMessage("❗Please enter a valid question.");
            return ResponseEntity.ok(response);
        }

        String[] words = nlQuery.split("\\s+");
        for (String word : words) {
            if (GREETINGS.contains(word)) {
                response.setMessage("👋 Hi! What can I help you find in the database?");
                return ResponseEntity.ok(response);
            }
        }


        try {
            String sql = llmService.convertToSQL(nlQuery);

            if (sql.equalsIgnoreCase("INVALID_QUERY") || !sql.toLowerCase().startsWith("select")) {
                response.setSql(sql);
                response.setMessage("❌ Couldn't understand your query.\n👋 Try asking something like: who lives in Mumbai?");
                return ResponseEntity.ok(response);
            }

            List<Map<String, Object>> result = queryService.executeSelectQuery(sql);
            response.setSql(sql);
            response.setResult(result);
        } catch (Exception e) {
            response.setError(e.getMessage());
            response.setMessage("🚨 Something went wrong while processing your request.");
        }

        return ResponseEntity.ok(response);
    }
}