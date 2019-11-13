/*
 * Copyright 2019 Mikołaj 'zozol' Szmalc
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package me.zozol.botlistapi.http.WebHook;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.undertow.Undertow;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import me.zozol.botlistapi.http.ListedBot;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebhookServer {
    public static class Builder {
        private String authorization = null;
        private String botId = null;
        private String host = "localhost";
        private String errorResponseMessage = "{\"error\": \"Bad request\"}";
        private String successResponseMessage = "{\"success\": \"OK\"}";
        private int port = 8080;
        private boolean silent = false;

        public Builder setBotId(String botId) {
            this.botId = botId;
            return this;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setAuthorization(String authorization) {
            this.authorization = authorization;
            return this;
        }

        public Builder setErrorResponse(String responseMessage) {
            this.errorResponseMessage = responseMessage;
            return this;
        }

        public Builder setSuccessResponse(String responseMessage) {
            this.successResponseMessage = responseMessage;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setSilent() {
            return setSilent(true);
        }

        public Builder setSilent(boolean silent) {
            this.silent = silent;
            return this;
        }

        public WebhookServer build() {
            if (botId == null)
                throw new NullPointerException("Bot id can not be null!");
            return new WebhookServer(authorization, botId, host, errorResponseMessage, successResponseMessage, port, silent);
        }
    }

    private Undertow server;
    private String authorization;
    private String botId;
    private String host;
    private String errorResponseMessage;
    private String successResponseMessage;
    private int port;
    private boolean silent;

    private Set<WebhookListener> webhookListeners;
    private Logger logger;

    public WebhookServer(String authorization, String botId, String host, String errorResponseMessage, String successResponseMessage, int port, boolean silent) {
        this.authorization = authorization;
        this.botId = botId;
        this.host = host;
        this.errorResponseMessage = errorResponseMessage;
        this.successResponseMessage = successResponseMessage;
        this.port = port;
        this.silent = silent;

        webhookListeners = new HashSet<>();
        logger = ListedBot.logger;
        server = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(exchange -> {
                    if (!silent)
                        logger.info("[Webserver > dbl.kresmc.pl] Handling request: " + exchange.getRequestURL());
                    HeaderMap headerValues = exchange.getRequestHeaders();
                    try {
                        AtomicBoolean allowed = new AtomicBoolean(false);

                        if (authorization == null) {
                            allowed.set(true);
                        } else {
                            headerValues.forEach(value -> value.forEach(s -> {
                                if (value.getHeaderName().toString().equals("Authorization") && s.equals(authorization))
                                    allowed.set(true);
                            }));
                        }

                        if (!allowed.get()) {
                            if (!silent) logger.info("[Webserver > dbl.kresmc.pl] Request failed");
                            exchange.getResponseHeaders().put(Headers.STATUS, 400);
                            exchange.getResponseSender().send(errorResponseMessage);
                            return;
                        }

                        exchange.getRequestReceiver().receiveFullString((httpServerExchange, s) -> {
                            if (!silent) logger.info("[Webserver > dbl.kresmc.pl] Parsing request...");

                            JsonElement element = new JsonParser().parse(s);
                            JsonObject object = element.getAsJsonObject();

                            String user = object.get("user").getAsString();
                            String bot = object.get("bot").getAsString();

                            callListeners(user, bot);

                            if (!silent)
                                logger.info("[Webserver > dbl.kresmc.pl] Request parsed, vote listeners were called");
                            exchange.getResponseHeaders().put(Headers.STATUS, 200);
                            exchange.getResponseSender().send(successResponseMessage);
                        });
                    } catch (Exception ignored) {
                        if (!silent)
                            logger.warn("[Webserver > dbl.kresmc.pl] Could not handle request");
                        exchange.getResponseHeaders().put(Headers.STATUS, 400);
                        exchange.getResponseSender().send(errorResponseMessage);
                    }
                }).build();
    }

    private void callListeners(String userId, String botId) {
        webhookListeners.forEach(listener -> listener.onUserVote(userId, botId));
    }

    public void registerListeners(Set<WebhookListener> listeners) {
        webhookListeners.addAll(listeners);
    }

    public void registerListener(WebhookListener listener) {
        webhookListeners.add(listener);
    }

    public void unregisterListener(WebhookListener listener) {
        webhookListeners.remove(listener);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop();
    }
}