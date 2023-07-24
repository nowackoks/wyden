package org.example.api.http;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import org.example.BinanceApiWebSocketListener;

@Controller("/subscribe")
public class SubscribeController {

    private final BinanceApiWebSocketListener webSocketListener;

    @Inject
    public SubscribeController(BinanceApiWebSocketListener webSocketListener) {
        this.webSocketListener = webSocketListener;
    }

    @Post("/{symbol}")
    public String subscribe(String symbol) {
        webSocketListener.initNewWebSocket(symbol);
        return symbol + " subscribed.";
    }

    @Delete("/{symbol}")
    public String unsubscribe(String symbol) {
        webSocketListener.closeWebSocket(symbol);
        return symbol + " unsubscribed.";
    }
}
