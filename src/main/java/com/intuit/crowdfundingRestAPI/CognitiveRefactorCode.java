
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Service
public class RequestLogFilter extends GenericFilterBean {
    private static final Logger logger = LoggerFactory.getLogger(RequestLogFilter.class);

    private static final String NO_RESPONSE_CONTENT = "No Response Content Found :( ";
    private static final String MESSAGE_TEMPLATE = "\n==================\nRequest Payload:\n{}\n==================\nResponse Content:\n{}\n==================";

    private static final AtomicInteger counter = new AtomicInteger(0);

    @Value("${request.logging.enabled:true}")
    private boolean enableRequestLogging;

    @Value("${request.logging.skip.paths:api-docs,swagger}")
    private List<String> skipPaths;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String feature = request.getHeader("x-feature");
        String authenticatedUser = request.getHeader("x-ms-webstack-authenticated-user");
        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        StringBuilder logMessage = new StringBuilder();
        appendIfNotNull(logMessage, "Feature", feature);
        appendIfNotNull(logMessage, "Authenticated User", authenticatedUser);
        appendIfNotNull(logMessage, "Method", method);
        appendIfNotNull(logMessage, "URI", requestUri);

        logger.info(logMessage.toString());

        if (!enableRequestLogging || skipPaths.stream().anyMatch(requestUri::contains)) {
            chain.doFilter(request, response);
            return;
        }

        int id = counter.incrementAndGet();
        Instant startTs = Instant.now();
        
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        chain.doFilter(requestWrapper, responseWrapper);

        Instant endTs = Instant.now();
        long timeTaken = Duration.between(startTs, endTs).toMillis();

        String requestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        String responseBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

        StringBuilder detailedLogMessage = new StringBuilder();
        detailedLogMessage.append("\n==================\n");
        appendIfNotNull(detailedLogMessage, "Request Payload", requestBody);
        detailedLogMessage.append("\n==================\n");
        appendIfNotNull(detailedLogMessage, "Response Content", responseBody);
        detailedLogMessage.append("\n==================\n");
        appendIfNotNull(detailedLogMessage, "Request ID", String.valueOf(id));
        appendIfNotNull(detailedLogMessage, "Time Taken", timeTaken + " ms");
        appendIfNotNull(detailedLogMessage, "Status", String.valueOf(response.getStatus()));
        appendIfNotNull(detailedLogMessage, "Feature", feature);
        appendIfNotNull(detailedLogMessage, "Authenticated User", authenticatedUser);
        appendIfNotNull(detailedLogMessage, "Method", method);
        appendIfNotNull(detailedLogMessage, "URI", requestUri);

        logger.info(detailedLogMessage.toString());

        responseWrapper.copyBodyToResponse();
    }

    private void appendIfNotNull(StringBuilder sb, String fieldName, String value) {
        Optional.ofNullable(value)
                .ifPresent(val -> sb.append(fieldName).append(": ").append(val).append(", "));
    }
}
-------_------------
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Service
public class RequestLogFilter extends GenericFilterBean {
    private static final Logger logger = LoggerFactory.getLogger(RequestLogFilter.class);

    private static final String NO_RESPONSE_CONTENT = "No Response Content Found :( ";
    private static final String MESSAGE_TEMPLATE = "\n==================\nRequest Payload:\n{}\n==================\nResponse Content:\n{}\n==================";

    private static final AtomicInteger counter = new AtomicInteger(0);

    @Value("${request.logging.enabled:true}")
    private boolean enableRequestLogging;

    @Value("${request.logging.skip.paths:api-docs,swagger}")
    private List<String> skipPaths;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String feature = request.getHeader("x-feature");
        String authenticatedUser = request.getHeader("x-ms-webstack-authenticated-user");
        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        StringBuilder logMessage = new StringBuilder();
        appendIfNotNull(logMessage, "Feature", feature);
        appendIfNotNull(logMessage, "Authenticated User", authenticatedUser);
        appendIfNotNull(logMessage, "Method", method);
        appendIfNotNull(logMessage, "URI", requestUri);

        logger.info(logMessage.toString());

        if (!enableRequestLogging || skipPaths.stream().anyMatch(requestUri::contains)) {
            chain.doFilter(request, response);
            return;
        }

        int id = counter.incrementAndGet();
        Instant startTs = Instant.now();
        
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        chain.doFilter(requestWrapper, responseWrapper);

        Instant endTs = Instant.now();
        long timeTaken = Duration.between(startTs, endTs).toMillis();

        String requestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        String responseBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

        StringBuilder detailedLogMessage = new StringBuilder();
        detailedLogMessage.append("\n==================\n");
        detailedLogMessage.append("Request Payload:\n").append(requestBody).append("\n");
        detailedLogMessage.append("==================\n");
        detailedLogMessage.append("Response Content:\n").append(responseBody).append("\n");
        detailedLogMessage.append("==================\n");
        detailedLogMessage.append("Request ID: ").append(id).append(", ");
        detailedLogMessage.append("Time Taken: ").append(timeTaken).append(" ms, ");
        detailedLogMessage.append("Status: ").append(response.getStatus()).append(", ");
        detailedLogMessage.append("Feature: ").append(feature).append(", ");
        detailedLogMessage.append("Authenticated User: ").append(authenticatedUser).append(", ");
        detailedLogMessage.append("Method: ").append(method).append(", ");
        detailedLogMessage.append("URI: ").append(requestUri);

        logger.info(detailedLogMessage.toString());

        responseWrapper.copyBodyToResponse();
    }

    private void appendIfNotNull(StringBuilder sb, String fieldName, String value) {
        if (value != null) {
            sb.append(fieldName).append(": ").append(value).append(", ");
        }
    }
}
----------------
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Service
public class RequestLogFilter extends GenericFilterBean {
    private static final Logger logger = LoggerFactory.getLogger(RequestLogFilter.class);

    private static final String NO_RESPONSE_CONTENT = "No Response Content Found :( ";
    private static final String MESSAGE_TEMPLATE = "\n==================\nRequest Payload:\n{}\n==================\nResponse Content:\n{}\n==================";

    private static final AtomicInteger counter = new AtomicInteger(0);

    @Value("${request.logging.enabled:true}")
    private boolean enableRequestLogging;

    @Value("${request.logging.skip.paths:api-docs,swagger}")
    private List<String> skipPaths;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String feature = request.getHeader("x-feature");
        String authenticatedUser = request.getHeader("x-ms-webstack-authenticated-user");
        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        StringBuilder logMessage = new StringBuilder();
        appendIfNotNull(logMessage, "Feature", feature);
        appendIfNotNull(logMessage, "Authenticated User", authenticatedUser);
        appendIfNotNull(logMessage, "Method", method);
        appendIfNotNull(logMessage, "URI", requestUri);

        logger.info(logMessage.toString());

        if (!enableRequestLogging || skipPaths.stream().anyMatch(requestUri::contains)) {
            chain.doFilter(request, response);
            return;
        }

        int id = counter.incrementAndGet();
        Instant startTs = Instant.now();
        
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        chain.doFilter(requestWrapper, responseWrapper);

        Instant endTs = Instant.now();
        long timeTaken = Duration.between(startTs, endTs).toMillis();

        String requestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        String responseBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

        logger.info(MESSAGE_TEMPLATE, requestBody, responseBody);
        logger.info("Request ID: {}, Time Taken: {} ms, Status: {}", id, timeTaken, response.getStatus());

        responseWrapper.copyBodyToResponse();
    }

    private void appendIfNotNull(StringBuilder sb, String fieldName, String value) {
        if (value != null) {
            sb.append(fieldName).append(": ").append(value).append(", ");
        }
    }
}
