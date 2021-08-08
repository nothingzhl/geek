package org.zhl.mvc;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

/**
 * @program: gee
 * @description:
 * @author: zhanghanlin
 * @create: 2021-08-08 18:53
 **/
public class HandlerMethodInfo {

    private final String requestPath;

    private final Method handlerMethod;

    private final Set<String> supportedHttpMethods;

    private final Parameter[] parameters;


    public HandlerMethodInfo(String requestPath, Method handlerMethod, Set<String> supportedHttpMethods) {
        this.requestPath = requestPath;
        this.handlerMethod = handlerMethod;
        this.supportedHttpMethods = supportedHttpMethods;
        this.parameters = handlerMethod.getParameters();
    }

    public String getRequestPath() {
        return requestPath;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public Set<String> getSupportedHttpMethods() {
        return supportedHttpMethods;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public boolean supportHttpMethod(String method){
        return supportedHttpMethods.contains(method);
    }
}
