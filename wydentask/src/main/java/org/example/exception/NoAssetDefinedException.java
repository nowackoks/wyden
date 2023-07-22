package org.example.exception;

public class NoAssetDefinedException extends RuntimeException {
    public NoAssetDefinedException() {
        super("No symbol has been defined during application start! (e.g. btcusdt)");
    }
}
