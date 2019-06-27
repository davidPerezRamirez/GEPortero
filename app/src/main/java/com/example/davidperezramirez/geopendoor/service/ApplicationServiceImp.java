package com.example.davidperezramirez.geopendoor.service;

import io.reactivex.Single;
import io.reactivex.functions.Function;

public class ApplicationServiceImp implements ApplicationService {

    private NetworkService networkService;

    public NetworkService getNetworkService() {
        return networkService;
    }

    public void setNetworkService(NetworkService networkService) {
        this.networkService = networkService;
    }

    public ApplicationServiceImp(NetworkService networkService) {
        setNetworkService(networkService);
    }

    @Override
    public Single<Boolean> openDoor() {
        String url = "http://app.grupoesfera.com.ar/abrirPuerta";
        return getNetworkService().getJson(url)
                .map(new Function<Boolean, Boolean>() {
                         @Override
                         public Boolean apply(Boolean aBoolean) throws Exception {
                             return aBoolean;
                         }
                     }
                );

    }
}
