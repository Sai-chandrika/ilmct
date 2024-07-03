package com.inspirage.ilct.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.inspirage.ilct.dto.ApiResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;


@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageableResponse extends ApiResponse {

    private PageInfo pageInfo;

    public PageableResponse(PageableResponseBuilder pageableResponseBuilder) {
        super(pageableResponseBuilder.status, pageableResponseBuilder.error, pageableResponseBuilder.message, pageableResponseBuilder.data);
        this.pageInfo = new PageInfo(pageableResponseBuilder.index, pageableResponseBuilder.recordsPerPage, pageableResponseBuilder.totalRecords);
    }

    public static class PageableResponseBuilder {
        public HttpStatus status;
        public String message;
        public Object data;
        public String error;

        public int index;
        public int recordsPerPage;
        public long totalRecords;

        public PageableResponseBuilder(HttpStatus status) {
            this.status = status;
        }

        public PageableResponseBuilder withStatus(HttpStatus status) {
            this.status = status;
            return this;
        }

        public PageableResponseBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public PageableResponseBuilder withData(Object data) {
            this.data = data;
            return this;
        }
        public PageableResponseBuilder withError(String error) {
            this.error = error;
            return this;
        }

        public PageableResponseBuilder withPageInfo(int index, int recordsPerPage, long totalRecords) {
            this.index = index;
            this.recordsPerPage = recordsPerPage;
            this.totalRecords = totalRecords;
            return this;
        }

        public ApiResponse build() {
            return new PageableResponse(this);
        }
    }

    public static class PageInfo {
        public int index;
        public int recordsPerPage;
        public long totalRecords;

        public PageInfo(int index, int recordsPerPage, long totalRecords) {
            this.index = index;
            this.recordsPerPage = recordsPerPage;
            this.totalRecords = totalRecords;
        }
    }
}
