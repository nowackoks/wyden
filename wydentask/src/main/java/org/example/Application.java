package org.example;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {

    public static void main(String[] args) {
        ApplicationContext micronautAppContext = Micronaut.run(Application.class, args);
        micronautAppContext.registerSingleton(BinanceApiWebSocketListener.class, new BinanceApiWebSocketListener(args));
    }
}
