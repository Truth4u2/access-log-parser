enum HttpMethod {
    GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE, CONNECT, PATCH;

    public static HttpMethod fromString(String methodStr) {
        for (HttpMethod method : HttpMethod.values()) {
            if (method.name().equalsIgnoreCase(methodStr)) {
                return method;
            }
        }
        return null;
    }
}