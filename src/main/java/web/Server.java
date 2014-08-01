package web;


import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.ResourceHandler;


/**
 * Created by Kali on 14-7-31.
 */
public class Server  {
    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        SocketListener listener = new SocketListener();
        listener.setPort(8081);
        server.addListener(listener);
        HttpContext context = new HttpContext();
        context.setContextPath("/web");
        context.setResourceBase("./webapp");
        context.addHandler(new ResourceHandler());
        server.addContext(context);

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
