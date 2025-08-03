package com.chatbot.dto;

import java.util.List;
import java.util.Map;

public class QueryResponse {
    private String sql;
    private List<Map<String, Object>> result;
    private String error;
    private String message;

    public QueryResponse() {
    }

    public QueryResponse(String sql, List<Map<String, Object>> result, String error, String message) {
        this.sql = sql;
        this.result = result;
        this.error = error;
        this.message = message;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, Object>> result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}