package pt.isel.autorouter;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class JsonServer implements AutoCloseable {

    final Javalin server = Javalin.create();
    final static ObjectMapper mapper = new ObjectMapper();

    public JsonServer(Stream<ArHttpRoute> routes) {
        routes.forEach(this::addRoute);
    }

    /**
     * Parses body request as Json and return Json back.
     */
    public final JsonServer addRoute(ArHttpRoute route) {
        Handler handler = httpHandlerForRoute(route);
        switch (route.method()) {
            case GET -> server.get(route.path(), handler);
            case POST -> server.post(route.path(), handler);
            case DELETE -> server.delete(route.path(), handler);
            case PUT -> server.put(route.path(), handler);
        }
        return this;
    }

    /**
     * Creates a Javalin Handler for an autorouter ArHttpRoute.
     * Parses body request as Json.
     */
    private static Handler httpHandlerForRoute(ArHttpRoute route) {
        return ctx -> {
            var routeArgs = ctx.pathParamMap();
            var queryArgs = ctx.queryParamMap().entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().get(0)));
            var bodyArgs = ctx.body().isEmpty() ? null : mapper.readValue(ctx.body(), Map.class);
            var res = route.handler().handle(routeArgs, queryArgs, bodyArgs);
            if (res.isPresent()) {
                if (res.get() instanceof Sequence ) {
                    try (PrintWriter writer = ctx.res().getWriter()) {
                        ctx.contentType("text/html");
                        @SuppressWarnings("unchecked")
                        Sequence<Sequence<?>> sequence = (Sequence<Sequence<?>>) res.get();
                        Iterator<Sequence<?>> iterator = sequence.iterator();
                        while (iterator.hasNext()) {
                            Sequence<?> next = iterator.next();
                            Iterator<?> iterator1 = next.iterator();
                            while (iterator1.hasNext()) {
                                Object next1 = iterator1.next();
                                writer.println(next1);
                                writer.println("<br>");
                                writer.flush();
                            }
                        }
                    }
                } else {
                    ctx.json(res.get());
                }
            } else {
                // Status code 404
                throw new NotFoundResponse();
            }
        };
    }

    public void start(int port) {
        server.start(port);
    }

    @Override
    public void close() {
        server.close();
    }

    @NotNull
    public Javalin javalin() {
        return server;
    }
}
