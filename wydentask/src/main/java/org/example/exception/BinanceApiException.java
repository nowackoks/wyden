package org.example.exception;

/**
 * An exception which can occur while invoking methods of the Binance API.
 */
public class BinanceApiException extends RuntimeException {

    public BinanceApiException(Throwable cause) {
        super(cause);
    }
}

