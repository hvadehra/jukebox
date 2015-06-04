package org.melocine.components.web.servlets.display;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 11/25/14
 * Time: 10:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultDisplayServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        Path template = Paths.get("nowplaying.template");
        Charset charset = StandardCharsets.UTF_8;
        String output = new String(Files.readAllBytes(template), charset);
        response.getWriter().print(output);
    }
}