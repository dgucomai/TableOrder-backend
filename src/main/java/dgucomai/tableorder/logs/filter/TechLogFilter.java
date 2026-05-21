package dgucomai.tableorder.logs.filter;

import dgucomai.tableorder.logs.service.LogService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@RequiredArgsConstructor
public class TechLogFilter implements Filter {

  private final LogService logService;

  private static final List<String> EXCLUDED_PREFIXES =
      List.of("/api/admin/logs/", "/_next/static/", "/images/", "/favicon.ico");

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpReq = (HttpServletRequest) request;
    HttpServletResponse httpRes = (HttpServletResponse) response;
    String uri = httpReq.getRequestURI();

    if (isExcluded(uri)) {
      chain.doFilter(request, response);
      return;
    }

    ContentCachingRequestWrapper wrappedReq = new ContentCachingRequestWrapper(httpReq, 10000);
    ContentCachingResponseWrapper wrappedRes = new ContentCachingResponseWrapper(httpRes);

    long start = System.currentTimeMillis();
    try {
      chain.doFilter(wrappedReq, wrappedRes);
    } finally {
      long duration = System.currentTimeMillis() - start;
      String body = new String(wrappedReq.getContentAsByteArray(), StandardCharsets.UTF_8);
      logService.saveTechLog(
          httpReq.getMethod(),
          uri,
          body.isBlank() ? null : body,
          wrappedRes.getStatus(),
          duration,
          httpReq.getRemoteAddr());
      wrappedRes.copyBodyToResponse();
    }
  }

  private boolean isExcluded(String uri) {
    return EXCLUDED_PREFIXES.stream().anyMatch(uri::startsWith);
  }
}
