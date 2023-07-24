package org.example.exception;

public class StreamAlreadyOpenedException extends RuntimeException {
    public StreamAlreadyOpenedException() {
        super("Stream is already opened.");
    }
}
