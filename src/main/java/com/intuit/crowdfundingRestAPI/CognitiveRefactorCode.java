import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestSourceFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(RequestSourceFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String source = httpRequest.getHeader("X-Request-Source");

        if (source != null) {
            switch (source) {
                case "update":
                    logger.info("Handling update relationship request");
                    break;
                case "bulk-update":
                    logger.info("Handling bulk relationship update request");
                    break;
                case "clone":
                    logger.info("Handling clone relationship request");
                    break;
                case "add":
                    logger.info("Handling add relationship request");
                    break;
                default:
                    logger.warn("Unknown request source: {}", source);
                    break;
            }
        } else {
            logger.warn("X-Request-Source header is missing");
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}
===============
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<RequestSourceFilter> loggingFilter() {
        FilterRegistrationBean<RequestSourceFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestSourceFilter());
        registrationBean.addUrlPatterns("/updateRelationship/*");
        return registrationBean;
    }
}
======================
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/updateRelationship")
public class RelationshipController {
    private static final Logger logger = LoggerFactory.getLogger(RelationshipController.class);

    @PostMapping
    public ResponseEntity<String> updateRelationship(@RequestHeader("X-Request-Source") String source, @RequestBody RelationshipRequest request) {
        switch (source) {
            case "update":
                logger.info("Processing update relationship request");
                return handleUpdate(request);
            case "bulk-update":
                logger.info("Processing bulk relationship update request");
                return handleBulkUpdate(request);
            case "clone":
                logger.info("Processing clone relationship request");
                return handleClone(request);
            case "add":
                logger.info("Processing add relationship request");
                return handleAdd(request);
            default:
                logger.warn("Unknown request source: {}", source);
                return ResponseEntity.badRequest().body("Unknown request source");
        }
    }

    // Define your handlers for different functionalities
    private ResponseEntity<String> handleUpdate(RelationshipRequest request) {
        logger.debug("Update details: {}", request);
        return ResponseEntity.ok("Update successful");
    }

    private ResponseEntity<String> handleBulkUpdate(RelationshipRequest request) {
        logger.debug("Bulk update details: {}", request);
        return ResponseEntity.ok("Bulk update successful");
    }

    private ResponseEntity<String> handleClone(RelationshipRequest request) {
        logger.debug("Clone details: {}", request);
        return ResponseEntity.ok("Clone successful");
    }

    private ResponseEntity<String> handleAdd(RelationshipRequest request) {
        logger.debug("Add details: {}", request);
        return ResponseEntity.ok("Add successful");
    }
}
