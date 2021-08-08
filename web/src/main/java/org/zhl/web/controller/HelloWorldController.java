package org.zhl.web.controller;

import org.zhl.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @program: gee
 * @description:
 * @author: zhanghanlin
 * @create: 2021-08-08 18:41
 **/
    @Path("/hello")
public class HelloWorldController implements PageController {

    @GET
    @Override
    @Path("/world")
    public String execute(HttpServletRequest request,
        HttpServletResponse response) throws Throwable {
        return "index.jsp";
    }
}
