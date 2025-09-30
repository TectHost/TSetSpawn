package utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.HtmlManager;
import org.jetbrains.annotations.NotNull;
import tsetspawn.TSetSpawn;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class SpawnHttpServer {

    private final TSetSpawn plugin;
    private final SpawnDataService spawnDataService;
    private final HtmlManager htmlManager;
    private HttpServer server;

    private final int port;
    private static final String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";
    private static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";
    private static String FOOTER = "";

    public SpawnHttpServer(@NotNull TSetSpawn plugin, SpawnDataService spawnDataService, HtmlManager htmlManager) {
        this.plugin = plugin;
        this.spawnDataService = spawnDataService;
        this.htmlManager = htmlManager;
        this.port = plugin.getWebManager().getPort();
        FOOTER =
                "<footer style='text-align:center; margin-top:20px; margin-bottom:20px; font-size:12px;'>"
                        + "TSetSpawn " + plugin.getDescription().getVersion()
                        + " - Made by Tect.host"
                        + "</footer>";
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new IndexHandler());
            server.createContext("/spawns", new SpawnsHandler());
            server.createContext("/command", new CommandHandler());
            server.createContext("/modules", new ModulesHandler());
            server.setExecutor(null);
            server.start();

            plugin.getLogger().log(Level.INFO, "Spawn HTTP Server started on port " + port);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error starting SpawnHttpServer", e);
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            plugin.getLogger().log(Level.INFO, "Spawn HTTP Server stopped");
        }
    }

    public void restart() {
        stop();
        start();
    }

    private void sendJson(@NotNull HttpExchange exchange, int statusCode, @NotNull String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", CONTENT_TYPE_JSON);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private class IndexHandler implements HttpHandler {
        @Override
        public void handle(@NotNull HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            byte[] originalContent = htmlManager.getHtmlBytes();

            if (originalContent.length == 0) {
                String msg = "File index.html not found";
                exchange.sendResponseHeaders(404, msg.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(msg.getBytes(StandardCharsets.UTF_8));
                }
                plugin.getLogger().warning("File HTML not found for /");
                return;
            }

            String html = new String(originalContent, StandardCharsets.UTF_8);

            html += FOOTER;

            byte[] contentWithFooter = html.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", CONTENT_TYPE_HTML);
            exchange.sendResponseHeaders(200, contentWithFooter.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(contentWithFooter);
            }
        }
    }

    private class SpawnsHandler implements HttpHandler {
        @Override
        public void handle(@NotNull HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET" -> handleGet(exchange);
                case "POST" -> handlePost(exchange);
                default -> exchange.sendResponseHeaders(405, -1);
            }
        }

        private void handleGet(@NotNull HttpExchange exchange) throws IOException {
            try {
                String json = spawnDataService.toJsonAllSpawns();
                sendJson(exchange, 200, json);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error getting spawns:", e);
                sendJson(exchange, 500, "{\"status\":\"error\",\"message\":\"Internal Server Error\"}");
            }
        }

        private void handlePost(@NotNull HttpExchange exchange) throws IOException {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                spawnDataService.addSpawnFromJson(requestBody);
                DebugLogger.log("Received POST method: " + requestBody);
                sendJson(exchange, 200, "{\"status\":\"success\"}");
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error when adding spawn: " + e.getMessage());
                String errorJson = "{\"status\":\"error\",\"message\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
                sendJson(exchange, 400, errorJson);
            }
        }
    }

    private class ModulesHandler implements HttpHandler {
        @Override
        public void handle(@NotNull HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            try {
                String json = plugin.getConfigManager().getModulesAsJson().toString();
                sendJson(exchange, 200, json);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error loading modules config", e);
                sendJson(exchange, 500, "{\"status\":\"error\",\"message\":\"Internal Server Error\"}");
            }
        }
    }

    private class CommandHandler implements HttpHandler {
        @Override
        public void handle(@NotNull HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String command = body.trim();

            if (command.isEmpty()) {
                sendJson(exchange, 400, "{\"status\":\"error\",\"message\":\"Command is empty\"}");
                return;
            }

            plugin.getServer().getScheduler().runTask(plugin, () ->
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command)
            );

            sendJson(exchange, 200, "{\"status\":\"success\",\"message\":\"Command executed\"}");
        }
    }
}
