package com.example.demo.endpoint.rest.error;

import java.time.Instant;

public record ApiError(Instant timestamp, int status, String error, String message) {}
