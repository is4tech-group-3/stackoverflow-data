package com.stackoverflow.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackoverflow.bo.Audit;
import com.stackoverflow.bo.User;
import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.audit.AuditService;
import com.stackoverflow.util.AuditAnnotation;
import com.stackoverflow.util.CurrentUser;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.*;

@AllArgsConstructor
@Component
public class AuditInterceptor implements HandlerInterceptor {
    private final AuditService auditService;
    private final CurrentUser currentUser;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            Method method = handlerMethod.getMethod();
            AuditAnnotation auditAnnotation = method.getAnnotation(AuditAnnotation.class);

            if (auditAnnotation != null) {
                Audit audit = new Audit();
                audit.setEntity(auditAnnotation.value());
                audit.setHttpMethod(request.getMethod());
                audit.setDateOperation(new Date());
                User user = getUserIdFromRequest(request);
                if (user != null) {
                    audit.setUserId(user.getId());
                    audit.setEmail(user.getEmail());
                } else {
                    throw new AuthenticationException("It seems that the user is not authenticated");
                }

                if (isMultipartRequest(request)) {
                    Map<String, Object> multipartData = extractMultipartData(request);
                    audit.setRequest(Objects.requireNonNullElse(multipartData, "NO REQUEST BODY"));
                } else if (!(request instanceof ContentCachingRequestWrapper)) {
                    request = new ContentCachingRequestWrapper(request);
                }

                request.setAttribute("audit", audit);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            Audit audit = (Audit) request.getAttribute("audit");

            if (audit == null) {
                return;
            }

            if (audit.getRequest() == null || !isMultipartRequest(request)) {
                ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;
                Object requestBody = getRequestBody(cachingRequest);
                audit.setRequest(Objects.requireNonNullElse(requestBody, "NO REQUEST BODY"));
            }
            audit.setStatusCode(response.getStatus());
            audit.setStatusDescription(HttpStatus.valueOf(response.getStatus()).getReasonPhrase());
            Object responseBody = request.getAttribute("responseBody");
            if (responseBody instanceof PageImpl<?> page) {
                List<?> content = page.getContent();
                if (!content.isEmpty()) {
                    Object item = content.getFirst();
                    Map<String, Object> newContent = new HashMap<>();
                    newContent.put("content", item);
                    newContent.put("info", "more data responded but not stored in audit");
                    PageImpl<?> newPage = new PageImpl<>(List.of(newContent), page.getPageable(), page.getTotalElements());
                    audit.setResponse(newPage);
                } else {
                    audit.setResponse("NO CONTENT");
                }
            } else {
                audit.setResponse(Objects.requireNonNullElse(responseBody, "NO RESPONSE BODY"));
            }
            auditService.auditPost(audit);
        }
    }

    private Object getRequestBody(ContentCachingRequestWrapper cachingRequest) {
        byte[] content = cachingRequest.getContentAsByteArray();
        if (content.length == 0) return null;
        String body = extractBody(content, cachingRequest);
        return parseBodyAsObject(body);
    }

    private String extractBody(byte[] content, ContentCachingRequestWrapper cachingRequest) {
        try {
            return new String(content, cachingRequest.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException("Encoding not supported for request body");
        }
    }

    private Object parseBodyAsObject(String body) {
        try {
            return new ObjectMapper().readValue(body, Object.class);
        } catch (JsonProcessingException e) {
            return body;
        }
    }

    private boolean isMultipartRequest(HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().startsWith(MediaType.MULTIPART_FORM_DATA_VALUE);
    }

    private Map<String, Object> extractMultipartData(HttpServletRequest request) {
        Map<String, Object> multipartData = new HashMap<>();
        try {
            request.getParameterMap().forEach((key, values) -> multipartData.put(key, Arrays.toString(values)));

            if (request instanceof MultipartHttpServletRequest multipartRequest) {
                for (MultipartFile file : multipartRequest.getFileMap().values()) {
                    multipartData.put(file.getName(), file.getOriginalFilename());
                }
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("Error parsing multipart request", e);
        }
        return multipartData;
    }

    private User getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String email = currentUser.extractUserId(token);
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        }
        return null;
    }
}
