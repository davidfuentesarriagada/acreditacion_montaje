package com.sicep.exponor2023.acreditacion_montaje.resources;

public class ServiceLayerException extends Exception {
    public ServiceLayerException() {
    }

    public ServiceLayerException(String message) {
        super(message);
    }

    public ServiceLayerException(Throwable cause) {
        super(cause);
    }

    public ServiceLayerException(String message, Throwable cause) {
        super(message, cause);
    }

}
