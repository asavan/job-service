package ru.asavan.job.utils;


import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
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

    private static final Logger log = Logger.getLogger(WebUtils.class);


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
