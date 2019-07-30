package com.eryanet.m85musicsdk.inter;

import java.io.IOException;



 public interface IHttpCallback {
    public void onFailure(int errorCode, String errorMessage);
    public void onResponse(String data, String finalUrl) throws IOException;
}
