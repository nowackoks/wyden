package org.example.exception;

public class StreamAlreadyClosedException extends RuntimeException {
    public StreamAlreadyClosedException() {
        super("Stream is already closed.");
    }
}
