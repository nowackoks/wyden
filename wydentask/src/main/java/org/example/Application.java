package org.example;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.NoAssetDefinedException;

@Slf4j
public class Application {

    private static final BinanceApiWebSocketListener webSocketListener =
            new BinanceApiWebSocketListener();

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new NoAssetDefinedException();
        }

        ApplicationContext context = Micronaut.run(Application.class, args);
        context.registerSingleton(BinanceApiWebSocketListener.class, webSocketListener);

        webSocketListener.run(args[0]);
    }
}
