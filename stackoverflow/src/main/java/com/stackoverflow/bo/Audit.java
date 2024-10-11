package com.stackoverflow.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Audit {
    private String entity;
    @JsonProperty("http_method")
    private String httpMethod;
    private Object request;
    private Object response;
    @JsonProperty("status_code")
    private Integer statusCode;
    @JsonProperty("status_description")
    private String statusDescription;
    @JsonProperty("date_operation")
    private Date dateOperation;
    private String email;
    @JsonProperty("user_id")
    private Long userId;
}
