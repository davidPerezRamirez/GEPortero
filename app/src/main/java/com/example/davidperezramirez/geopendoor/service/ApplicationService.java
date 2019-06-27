package com.example.davidperezramirez.geopendoor.service;

import io.reactivex.Single;

public interface ApplicationService {

    Single<Boolean> openDoor();
}
