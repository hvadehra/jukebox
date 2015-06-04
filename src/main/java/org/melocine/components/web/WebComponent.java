package org.melocine.components.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.melocine.components.web.servlets.display.DefaultDisplayServlet;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 11/25/14
 * Time: 10:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class WebComponent {

    public WebComponent(int port) throws Exception {
        Server server = new Server(port);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(DefaultDisplayServlet.class, "/*");

        server.start();
        server.join();

    }
}
