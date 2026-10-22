package com.dripgrow.api.dto;

public record CustomerLoginResponse(String token, long expiresIn, CustomerProfileResponse customer) { }
