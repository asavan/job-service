package ru.asavan.job.utils;

/**
 * Helper class for parsing console arguments
 */
public final class ParametersUtils {

    /**
     * Gets specified parameter value or defaultValue if not set
     *
     */
    public static String getParameter(String[] args, String parameterName, String defaultValue) {
        if (args == null || args.length == 0) {
            return defaultValue;
        }

        String value = defaultValue;

        for (String arg : args) {
            if (arg.startsWith(parameterName)) {
                value = arg.replaceFirst(parameterName + "=", "");
            }
        }

        return value;
    }
}
