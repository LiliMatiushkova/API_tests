package org.epam.specifications;

import java.util.HashMap;
import java.util.Map;

public class Authentification {
    private static String apiKeyName = "key";
    private static String key = System.getenv("APIkey");

    private static String apiTokenName = "token";
    private static String token = System.getenv("APItoken");

    private static Map<String, String> authQueryParams = new HashMap<>();

    public static Map<String, String> getAuthentificationParameters() {
        if (authQueryParams.isEmpty()) {
            authQueryParams.put(apiKeyName, key);
            authQueryParams.put(apiTokenName, token);
        }
        return authQueryParams;
    }
}
