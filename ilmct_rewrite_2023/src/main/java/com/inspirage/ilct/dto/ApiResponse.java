package com.inspirage.ilct.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiResponse {
    private HttpStatus status;
    private String message;
    private Object data;
    private String error;
    private long timestamp;
    private int statusCode;
    private String token;
    public int index;
    public int recordsPerPage;
    public long totalRecords;


    public ApiResponse() {
    }

    public ApiResponse(int value, String message) {
        this.statusCode = value;
        this.message = message;
    }

    public ApiResponse(int value, Object message) {
        this.statusCode = value;
        this.data = message;
    }

    public ApiResponse(int value, List<Object> message) {
        this.statusCode = value;
        this.data = message;
    }

    public ApiResponse(int value, String message, List<Object> list, int index, int recordsPerPage, long totalRecords) {
        this.statusCode = value;
        this.message = message;
        this.data = list;
        this.index = index;
        this.recordsPerPage = recordsPerPage;
        this.totalRecords = totalRecords;
    }

    public ApiResponse(ApiResponseBuilder responseBuilder) {
        this.setStatus(responseBuilder.status);
        this.setMessage(responseBuilder.message);
        this.setData(responseBuilder.data);
        this.setError(responseBuilder.error);
        this.setTimestamp(responseBuilder.timestamp);
        this.setStatusCode(responseBuilder.status.value());
    }

    public ApiResponse withData(Object data) {
        this.data = data;
        return this;
    }

    public ApiResponse pageInfo(int index, int recordsPerPage, long totalRecords) {
        this.index = index;
        this.recordsPerPage = recordsPerPage;
        this.totalRecords = totalRecords;
        return this;
    }
    public ApiResponse message(String message){
        this.message=message;
        return this;
    }


    public void setToken(String token) {
        this.token = token;
    }

    public ApiResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
        this.statusCode = status.value();
    }

    public ApiResponse(int status, String message, Object data) {
        this.statusCode = status;
        this.message = message;
        this.data = data;
    }
    public ApiResponse ApiResponse(int status, String message, Object data) {
        this.statusCode = status;
        this.message = message;
        this.data = data;
        return this;
    }


    public ApiResponse(HttpStatus status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.statusCode = status.value();
    }

    public ApiResponse(HttpStatus status, Object data) {
        this.status = status;
        this.data = data;
    }

    public ApiResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public ApiResponse(HttpStatus status, String message, int statusCode) {
        this.status = status;
        this.message = message;
        this.statusCode = statusCode;
    }

    public ApiResponse(HttpStatus status, String error, String message, Object resultObject) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.data = resultObject;
        this.timestamp = new Timestamp(new Date().getTime()).getTime();
        this.statusCode = status.value();
    }


    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ApiResponse)) return false;
        final ApiResponse other = (ApiResponse) o;
        if (!other.canEqual(this)) return false;
        final Object this$status = this.getStatus();
        final Object other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) return false;
        final Object this$message = this.getMessage();
        final Object other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) return false;
        final Object this$data = this.getData();
        final Object other$data = other.getData();
        if (this$data == null ? other$data != null : !this$data.equals(other$data)) return false;
        final Object this$error = this.getError();
        final Object other$error = other.getError();
        if (this$error == null ? other$error != null : !this$error.equals(other$error)) return false;
        if (this.getTimestamp() != other.getTimestamp()) return false;
        return this.getStatusCode() == other.getStatusCode();
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 43 : $status.hashCode());
        final Object $message = this.getMessage();
        result = result * PRIME + ($message == null ? 43 : $message.hashCode());
        final Object $data = this.getData();
        result = result * PRIME + ($data == null ? 43 : $data.hashCode());
        final Object $error = this.getError();
        result = result * PRIME + ($error == null ? 43 : $error.hashCode());
        final long $timestamp = this.getTimestamp();
        result = result * PRIME + (int) ($timestamp >>> 32 ^ $timestamp);
        result = result * PRIME + this.getStatusCode();
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ApiResponse;
    }

    public String toString() {
        return "ApiResponse(status=" + this.getStatus() + ", message=" + this.getMessage() + ", data=" + this.getData() + ", error=" + this.getError() + ", timestamp=" + this.getTimestamp() + ", statusCode=" + this.getStatusCode() + ")";
    }

    public static class ApiResponseBuilder {
        private HttpStatus status;
        private String message;
        private Object data;
        private String error;

        private final long timestamp = System.currentTimeMillis();

        public ApiResponseBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public ApiResponseBuilder setData(Object data) {
            this.data = data;
            return this;
        }

        public ApiResponseBuilder setStatus(HttpStatus status) {
            this.status = status;
            return this;
        }

        public ApiResponse build() {
            return new ApiResponse(this);
        }
    }
}