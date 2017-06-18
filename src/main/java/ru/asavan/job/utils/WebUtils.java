package ru.asavan.job.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Utility methods for uploading and downloading content through HTTP.
 */
public class WebUtils {

    private static final String CONNECTION = "Connection";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String REFERER_HEADER = "Referer";

    /**
     * Checks if request is originated from Internet Explorer
     *
     * @param request request
     * @return true if user-agent is IE
     */
    public static boolean isInternetExplorer(HttpServletRequest request) {
        if (request != null) {
            String userAgent = request.getHeader("User-Agent");
            if (userAgent != null) {
                return userAgent.contains("MSIE") && !userAgent.contains("MSIE 10") && !userAgent.contains("ms-office");
            }
        }
        return false;
    }

    /**
     * Appends protocol(http:// or https:// if secured=true) to the passed url
     * Replaces http:// with https:// or vice versa if url already contained protocol, but secured parameter indicated to use different one.
     *
     * @param url     url with or without http
     * @param secured if true -- appends https
     * @return combined url
     */
    public static String appendProtocolIfNecessary(String url, boolean secured) {
        if (url == null) {
            return null;
        }
        url = url.replaceFirst("^https?://", "");
        String protocol = !secured || url.startsWith("localhost") ? "http://" : "https://";
        return protocol + url;
    }

    /**
     * Adds <code>no-cache</code> response headers to passed response {@link HttpServletResponse}
     *
     * @param response response
     */
    public static void setNoCacheResponseHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "private, max-age=0, no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0); // Proxies.
    }

    public static String getReferer(HttpServletRequest request) {
        return request.getHeader(REFERER_HEADER);
    }

    public static String getUriWithQuery(HttpServletRequest request) {
        String requestURL = request.getRequestURI();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL;
        } else {
            return requestURL + "?" + queryString;
        }
    }

    public static void setDefaultHeaders(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(CONTENT_TYPE, "text/html");
        response.setHeader(CONNECTION, "close");
        setNoCacheResponseHeaders(response);
    }
}
