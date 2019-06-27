package com.example.davidperezramirez.geopendoor;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.example.davidperezramirez.geopendoor.service.ApplicationService;
import com.example.davidperezramirez.geopendoor.service.ApplicationServiceImp;
import com.example.davidperezramirez.geopendoor.service.NetworkService;
import com.example.davidperezramirez.geopendoor.service.NetworkServiceImp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ApplicationService applicationService;
    private NetworkService networkService;
    private CompositeDisposable compositeDisposable;
    private Unbinder unbinder;

    @BindView(R.id.btn_opendoor)
    Button btnOpenDoor;
    @BindView(R.id.idSnackbar)
    View snackbarContent;

    @OnClick(R.id.btn_opendoor)
    public void onClickOpenDoor() {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.feedback_button);

        btnOpenDoor.startAnimation(myAnim);
        Disposable disposable = getApplicationService().openDoor()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) {
                        if (result) {
                            Log.i("GE", "Puerta abierta");
                            showSnackbar("Puerta abierta");
                        } else {
                            showSnackbar("Ocurrio un error al conectarse al servicio");
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        showSnackbar("Error al intentar abrir la puerta: " + throwable.getMessage());
                        Log.i("GE", "Error al abrir puerta");
                    }
                });

        getCompositeDisposable().add(disposable);
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(snackbarContent, message, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    public void setCompositeDisposable(CompositeDisposable compositeDisposable) {
        this.compositeDisposable = compositeDisposable;
    }

    public NetworkService getNetworkService() {
        return networkService;
    }

    public void setNetworkService(NetworkService networkService) {
        this.networkService = networkService;
    }

    public ApplicationService getApplicationService() {
        return applicationService;
    }

    public void setApplicationService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);
        setCompositeDisposable(new CompositeDisposable());
        setNetworkService(new NetworkServiceImp(this));
        setApplicationService(new ApplicationServiceImp(getNetworkService()));
    }

    @Override
    protected void onDestroy() {
        getCompositeDisposable().clear();
        unbinder.unbind();
        super.onDestroy();
    }
}
