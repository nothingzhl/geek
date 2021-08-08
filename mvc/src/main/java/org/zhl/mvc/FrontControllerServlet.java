package org.zhl.mvc;

import org.apache.commons.lang.StringUtils;
import org.zhl.mvc.controller.Controller;
import org.zhl.mvc.controller.PageController;
import org.zhl.mvc.controller.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * @program: gee
 * @description:
 * @author: zhanghanlin
 * @create: 2021-08-08 18:55
 **/
public class FrontControllerServlet extends HttpServlet {

    private Map<String, Controller> controllerMap = new HashMap<>();

    private Map<String, HandlerMethodInfo> methodInfoMap = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        initFrontController();
    }

    private void initFrontController() {

        for (Controller controller : ServiceLoader.load(Controller.class)) {
            final Class<? extends Controller> controllerClass = controller.getClass();
            final Path controllerPath = controllerClass.getAnnotation(Path.class);
            String requestUrl = controllerPath.value();

            final Method[] publicMethods = controllerClass.getMethods();

            for (Method method : publicMethods) {

                Set<String> supportedHttpMethods = findSupportedHttpMethods(method);
                final Path pathFromMethod = method.getAnnotation(Path.class);

                if (Objects.nonNull(pathFromMethod)) {
                    requestUrl += pathFromMethod.value();
                }
                methodInfoMap.put(requestUrl, new HandlerMethodInfo(requestUrl, method, supportedHttpMethods));
            }

            controllerMap.put(requestUrl, controller);
        }

    }

    private Set<String> findSupportedHttpMethods(Method method) {

        Set<String> supportedHttpMethods = new LinkedHashSet<>();

        for (Annotation annotation : method.getAnnotations()) {
            final HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);

            if (Objects.nonNull(httpMethod)) {
                supportedHttpMethods.add(httpMethod.value());
            }
        }

        if (supportedHttpMethods.isEmpty()) {
            supportedHttpMethods.addAll(
                asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.HEAD,
                    HttpMethod.OPTIONS));
        }

        return supportedHttpMethods;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final String requestURI = req.getRequestURI();

        final String webPrefix = req.getContextPath();

        String requestMappingPath = StringUtils.substringAfter(requestURI, StringUtils.replace(webPrefix, "//", "/"));

        final Controller controller = controllerMap.get(requestMappingPath);

        if (Objects.nonNull(controller)) {

            final HandlerMethodInfo handlerMethodInfo = methodInfoMap.get(requestMappingPath);

            try {

                final String reqMethod = req.getMethod();

                if (Objects.nonNull(handlerMethodInfo)) {

                    if (!handlerMethodInfo.supportHttpMethod(reqMethod)) {
                        // HTTP 方法不支持
                        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        return;
                    }

                    if (controller instanceof PageController) {

                        final PageController pageController = PageController.class.cast(controller);

                        String viewPath = pageController.execute(req, resp);

                        if (!StringUtils.startsWith(viewPath, "/")) {
                            viewPath = "/"+viewPath;
                        }

                        // viewPath 必须要 "/" 开头才能跳转
                        final ServletContext servletContext = req.getServletContext();
                        final RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(viewPath);

                        requestDispatcher.forward(req,resp);
                    }else if (controller instanceof RestController){
                        // todo
                    }

                }

            } catch (Throwable throwable) {
                throwable.printStackTrace();
                if (throwable.getCause() instanceof IOException) {
                    throw (IOException)throwable.getCause();
                } else {
                    throw new ServletException(throwable.getCause());
                }
            }

        }

    }
}
