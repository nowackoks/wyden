package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.ByteString;
import org.example.event.TickerEvent;
import org.example.exception.BinanceApiException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class BinanceApiWebSocketListener extends WebSocketListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build();

    public void run(String symbol) {
        String priceStreamFormat = "wss://stream.binance.com:9443/ws/%s@trade";
        Request request = new Request.Builder()
                .url(priceStreamFormat.formatted(symbol))
                .build();

        //alternatively, if solution should be done with no okhttp3 package, ScheduledThreadPoolExecutor could be used here
        client.newWebSocket(request, this);
        client.dispatcher().executorService().shutdown();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        log.info("WebSocket opened");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            TickerEvent event = objectMapper.readValue(text, TickerEvent.class);
            log.info("MESSAGE: " + event.toString());
        } catch (IOException e) {
            throw new BinanceApiException(e);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, reason);
        log.info("CLOSE: %d %s%n", code, reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
    }
}