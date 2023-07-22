package org.example.api.http;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Inject;
import org.example.BinanceApiWebSocketListener;

@Controller("/subscribe")
public class SubscribeController {
    @Inject
    private BinanceApiWebSocketListener webSocketListener;

    @Post("/{symbol}")
    @Produces(MediaType.TEXT_PLAIN)
    public String subscribeToASymbol(String symbol) {
        webSocketListener.run(symbol);
        return symbol + " subscribed.";
    }
}
