package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerShutdownEvent;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.ByteString;
import org.example.event.BookTickerEvent;
import org.example.exception.BinanceApiException;
import org.example.exception.StreamAlreadyClosedException;
import org.example.exception.StreamAlreadyOpenedException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.net.http.WebSocket.NORMAL_CLOSURE;

@Slf4j
public final class BinanceApiWebSocketListener extends WebSocketListener
        implements ApplicationEventListener<ServerShutdownEvent> {

    private static final String priceStreamFormat = "wss://stream.binance.com:9443/ws/%s@bookTicker";
    private static final long eventCooldownMs = PropertyLoader.getEventCooldownMs();

    private final ObjectMapper objectMapper;
    private final OkHttpClient client;
    private final ConcurrentHashMap<String, WebSocket> openedSockets;
    private ThreadLocal<Long> lastEventTime = ThreadLocal.withInitial(() -> 0L);

    public BinanceApiWebSocketListener(String[] args) {
        this.objectMapper = new ObjectMapper();
        this.client = new OkHttpClient.Builder()
                .dispatcher(initDispatcher())
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
        this.openedSockets = new ConcurrentHashMap<>();
        log.info("Event cooldown set to {} ms", eventCooldownMs);

        Arrays.stream(args).forEach(this::initNewWebSocket);
    }

    private Dispatcher initDispatcher() {
        var streamsLimit = PropertyLoader.getStreamsLimit();
        var dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(streamsLimit);
        dispatcher.setMaxRequestsPerHost(streamsLimit);
        return dispatcher;
    }

    //in big projects such methods should be placed in separate service class
    public void initNewWebSocket(String symbol) {
        preventFromStreamDuplicates(symbol);
        Request request = new Request.Builder()
                .url(priceStreamFormat.formatted(symbol))
                .build();

        WebSocket webSocket = client.newWebSocket(request, this);
        log.warn("{} price monitoring has been started.", symbol.toUpperCase());
        openedSockets.put(symbol, webSocket);
    }

    //in big projects such methods should be placed in separate service class
    public void closeWebSocket(String symbol) {
        if (!openedSockets.containsKey(symbol)) {
            throw new StreamAlreadyClosedException(); //it is not necessary, because remove on map does nothing, it's just to keep nice message.
        }
        WebSocket socketToClose = openedSockets.remove(symbol);
        socketToClose.close(NORMAL_CLOSURE, "WebSocket has been closed due to http DELETE call.");
        log.warn("{} price monitoring has been stopped.", symbol.toUpperCase());
    }

    private void preventFromStreamDuplicates(String symbol) {
        if (openedSockets.containsKey(symbol)) {
            throw new StreamAlreadyOpenedException();
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        final long currentTime = System.currentTimeMillis();
        // protection against too frequent handling of incoming events (by default each socket can process independently at maximum frequency once per two seconds)
        if (currentTime - lastEventTime.get() >= eventCooldownMs) {
            lastEventTime.set(currentTime);
            try {
                BookTickerEvent event = objectMapper.readValue(text, BookTickerEvent.class);
                log.info("{} MESSAGE: {}", new Date(currentTime), event.toString());
            } catch (IOException e) {
                throw new BinanceApiException(e);
            }
        } else {
            log.trace("Ignoring event because it is too frequent: {}", text);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        if (openedSockets.isEmpty()) {
            System.exit(0); //last unsubscription results with emitting ServerShutdownEvent and gracefully stop the app (onApplicationEvent handler will be called).
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
    }

    @Override
    public void onApplicationEvent(ServerShutdownEvent event) {
        log.info("Shutting down the application...");
        // Clean up resources and shut down the OkHttpClient
        client.dispatcher().executorService().shutdown();
        log.info("Application has been shut down.");
    }
}
