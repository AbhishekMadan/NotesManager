package com.abhishek.abc.notes.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.abhishek.abc.notes.network.NetworkClient;
import com.abhishek.abc.notes.network.NetworkInterface;
import com.abhishek.abc.notes.network.models.UserModel;
import com.abhishek.abc.notes.utils.PrefUtil;

import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    private NetworkInterface networkInterface;
    private final String TAG="NOTE_SP_ACTY";
    private CompositeDisposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        disposable = new CompositeDisposable();
        if (PrefUtil.getAPIKey(this)==null) {
            registerUser();
        } else {
            launchMainActivity();
        }
    }

    private void registerUser() {
        networkInterface = NetworkClient.getClient(this).create(NetworkInterface.class);
        // unique id to identify the device
        String uniqueId = UUID.randomUUID().toString();
        disposable.add(
                networkInterface.register(uniqueId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UserModel>() {
                    @Override
                    public void onSuccess(UserModel userModel) {
                        PrefUtil.saveAPIKey(SplashActivity.this,userModel.getApiKey());
                        launchMainActivity();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG,"Error registering device");
                        finish();
                    }
                }));
    }

    private void launchMainActivity() {
        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        disposable.clear();
        finish();
    }
}
