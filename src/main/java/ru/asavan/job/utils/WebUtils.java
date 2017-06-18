package ru.asavan.job.utils;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Utility methods for uploading and downloading content through HTTP.
 */
public class WebUtils {

    public static final String CONNECTION = "Connection";
    public static final String CONTENT_TYPE = "Content-Type";



    // tablet support:   note that android.+mobile is mobile device, android without mobile - tablet
    private static final Pattern UA_TABLET = Pattern.compile("(?i).*(android|ipad|playbook|silk).*");
    private static final Pattern UA_MOBILE = Pattern.compile("(?i).*(android.+mobile|avantgo|bada/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|meego.+mobile|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino).*");
    private static final Pattern UA_SUB = Pattern.compile("(?i)1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\\-(n|u)|c55/|capi|ccwa|cdm\\-|cell|chtm|cldc|cmd\\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\\-s|devi|dica|dmob|do(c|p)o|ds(12|\\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\\-|_)|g1 u|g560|gene|gf\\-5|g\\-mo|go(\\.w|od)|gr(ad|un)|haie|hcit|hd\\-(m|p|t)|hei\\-|hi(pt|ta)|hp( i|ip)|hs\\-c|ht(c(\\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\\-(20|go|ma)|i230|iac( |\\-|/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |/)|klon|kpt |kwc\\-|kyo(c|k)|le(no|xi)|lg( g|/(k|l|u)|50|54|\\-[a-w])|libw|lynx|m1\\-w|m3ga|m50/|ma(te|ui|xo)|mc(01|21|ca)|m\\-cr|me(di|rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\\-2|po(ck|rt|se)|prox|psio|pt\\-g|qa\\-a|qc(07|12|21|32|60|\\-[2-7]|i\\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\\-|oo|p\\-)|sdk/|se(c(\\-|0|1)|47|mc|nd|ri)|sgh\\-|shar|sie(\\-|m)|sk\\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\\-|v\\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\\-|tdg\\-|tel(i|m)|tim\\-|t\\-mo|to(pl|sh)|ts(70|m\\-|m3|m5)|tx\\-9|up(\\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\\-|your|zeto|zte\\-");
    private static final Pattern UA_IOS = Pattern.compile(".*(iPad|iPhone|iPod).*");

    // http://www.useragentstring.com/pages/Java/ to prevent redirect of our link-checker bot
    private static final Pattern UA_JAVA_QUIRK = Pattern.compile("(?i)Java/?1\\.\\d+.*");
    private static final Pattern URL_PATTERN = Pattern.compile("^https?://[\\w\\d\\-\\.]+(/.*?)(\\?.*)?$");
    private static final String GRANT_ACCESS_PARAM_NAME = "_accessGranted";
    private static final String GRANT_ACCESS_PARAM_VALUE = "true";

    private static final String UA_HEADER = "User-Agent";
    private static final String REFERER_HEADER = "Referer";
    private static final String BOT_USER_AGENT = "Mozilla/5.0";

    private static Logger log = Logger.getLogger(WebUtils.class);



    public static String createUrl(String existingUrl, Map<String, String> additionalParams, boolean overrideWithAdditional) {
        String[] href = existingUrl.split("\\?");
        String page = href[0];
        Map<String, String> allParams = new HashMap<String, String>(additionalParams);
        if (href.length == 2) {
            String query = href[1];
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length >= 2) {
                    if (!overrideWithAdditional || allParams.get(keyValue[0]) == null) {
                        try {
                            allParams.put(keyValue[0], URLDecoder.decode(keyValue[1], "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            log.warn("Error while decoding params: " + keyValue[1]);
                        }
                    }
                } else {
                    log.error("Can't split request param " + param);
                }
            }
        }
        String params = tryUrlEncodeMap(allParams, "UTF-8");
        params = StringUtils.isBlank(params) ? "" : "?" + params;
        return page + params;
    }

    public static boolean isIos(HttpServletRequest request) {
        String ua = request.getHeader(UA_HEADER);
        return !(StringUtils.isEmpty(ua) || StringUtils.length(ua) < 4) && UA_IOS.matcher(ua).matches();
    }

    /**
     * Serializes request to string
     *
     * @return http request data
     */
    public static String requestDataToString(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        StringBuilder errors = new StringBuilder();

        try {
            // Appending request url
            errors.append(request.getMethod());
            errors.append(" ");
            errors.append(request.getScheme());
            errors.append("://");
            errors.append(request.getHeader("Host"));
            errors.append(request.getRequestURI());
            String query = request.getQueryString();
            if (!StringUtils.isEmpty(query)) {
                errors.append("?");
                errors.append(query);
            }
            errors.append("\n");

            Enumeration headerNames = request.getHeaderNames();

            while (headerNames.hasMoreElements()) {

                String headerName = headerNames.nextElement().toString();
                errors.append(headerName);
                errors.append(": ");
                String headerValue = request.getHeader(headerName);
                errors.append(headerValue);
                errors.append("\n");
            }
            addParamsToSb(errors, request);
        } catch (Exception ex) {
            log.warn(ex);
        }

        return errors.toString();
    }

    static private void addParamsToSb(StringBuilder sb, HttpServletRequest request) {

        if (request.getParameterMap() == null) {
            return;
        }
        sb.append("Parameters:\n");
        for (Object entry : request.getParameterMap().entrySet()) {
            Map.Entry parameter = (Map.Entry) entry;
            sb.append(parameter.getKey());
            sb.append("=");
            sb.append(request.getParameter((String) parameter.getKey()));
            sb.append("\n");
        }
        sb.append("\n");
    }



    /*
    Generates urlencoded POST data from key-value dictionary
     */
    private static String urlEncodeMap(Map<String, String> data, String encoding) throws UnsupportedEncodingException {
        StringBuilder postData = new StringBuilder();
        if (data == null) {
            return StringUtils.EMPTY;
        }
        Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pair = it.next();
            if (pair.getValue() != null) {
                postData.append(URLEncoder.encode(pair.getKey(), encoding));
                postData.append("=");
                postData.append(URLEncoder.encode(pair.getValue(), encoding));
                if (it.hasNext()) {
                    postData.append("&");
                }
            }
        }
        return postData.toString();
    }

    public static String tryUrlEncodeMap(String encoding, String... params) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < params.length; i += 2) {
            map.put(params[i], params[i + 1]);
        }
        return tryUrlEncodeMap(map, encoding);

    }

    public static String tryUrlEncodeMap(Map<String, String> data, String encoding) {
        String result;
        try {
            result = urlEncodeMap(data, encoding);
        } catch (UnsupportedEncodingException e) {
            Logger.getLogger(WebUtils.class).error("Error encoding", e);

            StringBuilder sb = new StringBuilder();
            Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> param = it.next();
                if (param.getValue() != null) {
                    sb.append(String.format("%s=%s", param.getKey(), param.getValue()));
                    if (it.hasNext()) {
                        sb.append("&");
                    }
                }
            }
            result = sb.toString();
        }

        return result;
    }



    /**
     * Extracts host from the url
     *
     * @param url url
     * @return host name
     */
    public static String getHost(String url) {
        if (StringUtils.isEmpty(url)) {
            return StringUtils.EMPTY;
        }
        int beginIndex = url.indexOf("://");
        if (beginIndex < 4) {
            return StringUtils.EMPTY;
        }
        beginIndex += 3;
        int endIndex = url.indexOf('/', beginIndex);
        if (endIndex < 0) {
            return url.substring(beginIndex);
        }
        return url.substring(beginIndex, endIndex);
    }


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

    public static String removeJSessionFromUrl(String url) {
        return StringUtils.substringBefore(url, ";jsessionid=");
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

    public static class Parameters {

        private Map<String, String> params;

        public Parameters() {
            params = new HashMap<String, String>();
        }

        public Parameters(Map<String, String> params) {
            if (params == null) {
                throw new IllegalArgumentException("Argument 'params' could not be null.");
            }
            this.params = params;
        }

        public Parameters(Parameters params) {
            if (params == null) {
                throw new IllegalArgumentException("Argument 'params' could not be null.");
            }
            this.params = params.getParams();
        }


        public static Parameters parseServletRequest(ServletRequest request) {
            Map<String, String> params = new HashMap<String, String>();

            Enumeration names = request.getParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement().toString();
                String value = request.getParameter(name);
                params.put(name, value);
            }

            return new Parameters(params);
        }

        public Map<String, String> getParams() {
            return params;
        }

        public Parameters getParams(String... paramNames) {
            Parameters result = new Parameters();
            for (String paramName : paramNames) {
                if (has(paramName)) {
                    result.add(paramName, get(paramName));
                }
            }

            return result;
        }

        public Parameters add(String name, Object value) {
            if (value != null) {
                params.put(name, value.toString());
            }
            return this;
        }

        public Parameters add(String name, int value) {
            return add(name, String.valueOf(value));
        }

        public Parameters add(String name, long value) {
            return add(name, String.valueOf(value));
        }

        public Parameters add(Map.Entry<String, String> param) {
            return (param != null) ? add(param.getKey(), param.getValue()) : this;
        }

        public Parameters addAll(Parameters params) {
            getParams().putAll(params.getParams());
            return this;
        }

        public boolean has(String paramName) {
            return params.containsKey(paramName);
        }

        public String get(String paramName) {
            String result = params.get(paramName);
            return StringUtils.isEmpty(result) ? "" : result;
        }

        public String createUrl(String baseUrl, boolean overrideParamIfExists) {
            return WebUtils.createUrl(baseUrl, getParams(), overrideParamIfExists);
        }

        public boolean isEmpty() {
            return getParams().isEmpty();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry entry : params.entrySet()) {
                sb.append(String.format("%s=%s%n", entry.getKey(), entry.getValue()));
            }

            return sb.toString();
        }
    }

    public static Map<String, String> getQueryParams(String query, String enc) throws UnsupportedEncodingException {
        Map<String, String> data = new HashMap<>();
        int idx = query.indexOf('?');
        int state = 1;
        char c;
        StringBuilder pName = new StringBuilder();
        StringBuilder pVal = new StringBuilder();
        for (int i = idx + 1; i < query.length(); ++i) {
            c = query.charAt(i);
            switch (state) {
                case 1:
                    if (c == '=') {
                        state = 2;
                    } else {
                        pName.append(c);
                    }
                    break;
                case 2:
                    if (c == '&') {
                        state = 1;
                        data.put(pName.toString(), URLDecoder.decode(pVal.toString(), enc));
                        pName = new StringBuilder();
                        pVal = new StringBuilder();
                    } else if (i == query.length() - 1) {
                        pVal.append(c);
                        data.put(pName.toString(), URLDecoder.decode(pVal.toString(), enc));
                    } else {
                        pVal.append(c);
                    }
                    break;
            }
        }
        return data;
    }

    @Deprecated
    public static String urlDecode(String url, String encode) {
        String decodeString = "#invalidUrl";
        try {
            decodeString = URLDecoder.decode(url, encode);
        } catch (UnsupportedEncodingException e) {
            log.warn("Error while decoding params: " + url);
        }

        return decodeString;
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

    public static String encodeUrl(String url) {
        String result = url;
        try {
            result = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //never happen
        }
        return result;
    }

}
