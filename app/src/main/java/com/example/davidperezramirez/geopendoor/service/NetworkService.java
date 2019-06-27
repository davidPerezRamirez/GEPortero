package com.example.davidperezramirez.geopendoor.service;

import io.reactivex.Single;

public interface NetworkService {

    Single<Boolean> getJson(String url);
}
