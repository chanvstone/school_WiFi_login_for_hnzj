package com.sio2.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.sio2.login.databinding.ActivityMainBinding;

import java.io.IOException;
import java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int NO_WIFI = -1, NO_ACCOUNT = -2, ERROR_USER = -3, ERROR_PASSWORD = -4, FAIL = 0, DOING = 1, SUCCESS = 2;
    private ActivityMainBinding binding;
    public final Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case ERROR_PASSWORD:
                    showASnackBar("密码错误", null, "OK");
                    break;
                case ERROR_USER:
                    showASnackBar("账号不存在", null, "OK");
                    break;
                case NO_ACCOUNT:
                    showASnackBar("未设置账号", null, "OK");
                    break;
                case NO_WIFI:
                    showASnackBar("未连接校园网", v -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)), "WLAN设置");
                    break;
                case FAIL:
                    showASnackBar("登陆失败", null, "OK");
                    break;
                case DOING:
                    showASnackBar("登陆中...", null, null);
                    break;
                case SUCCESS:
                    showASnackBar("登陆成功", null, "OK");
                    break;
            }
            return false;
        }
    });
    private MainViewModel viewModel;
    private boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(MainActivity.this).get(MainViewModel.class);
        viewModel.context.setValue(MainActivity.this);

        init_toolBar();
        init_fab();
        init_drawer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.loadAccount();
        viewModel.getWiFiInterface();
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.saveAccount();
    }

    @Override
    public void onClick(View v) {
        if (!viewModel.isConnectedWiFi()) {
            handler.sendEmptyMessage(NO_WIFI);
            return;
        } else if (!viewModel.account.getValue().isValuable()) {
            handler.sendEmptyMessage(NO_ACCOUNT);
            return;
        }
        doLogin();
    }

    private int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private int getNavigationBarHeight() {
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private void init_fab() {
        CoordinatorLayout.LayoutParams floatingButtonLayoutParams = (CoordinatorLayout.LayoutParams) binding.floatingButton.getLayoutParams();
        floatingButtonLayoutParams.setMargins(
                floatingButtonLayoutParams.leftMargin,
                floatingButtonLayoutParams.topMargin,
                floatingButtonLayoutParams.rightMargin,
                floatingButtonLayoutParams.bottomMargin + getNavigationBarHeight()
        );
        binding.floatingButton.setLayoutParams(floatingButtonLayoutParams);

        binding.floatingButton.setOnClickListener(this);
    }

    private void init_toolBar() {
        setSupportActionBar(binding.toolBar);
        AppBarLayout.LayoutParams toolBarLayoutParams = (AppBarLayout.LayoutParams) binding.toolBar.getLayoutParams();
        toolBarLayoutParams.setMargins(
                toolBarLayoutParams.leftMargin,
                toolBarLayoutParams.topMargin + getStatusBarHeight(),
                toolBarLayoutParams.rightMargin,
                toolBarLayoutParams.bottomMargin
        );
        binding.toolBar.setLayoutParams(toolBarLayoutParams);
    }

    private void init_drawer() {
        NavController navController = Navigation.findNavController(this, R.id.nav_fragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_graph).setOpenableLayout(binding.drawerLayout).build();
        NavigationUI.setupWithNavController(binding.toolBar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navigationView, navController);
        NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, appBarConfiguration);
    }

    private void doLogin() {
        Thread loginThread = new Thread("login") {
            @Override
            public void run() {
                handler.sendEmptyMessage(DOING);
                try {
                    HttpURLConnection connection = (HttpURLConnection) viewModel.wifi.getValue().openConnection(viewModel.makeLoginUrl());
                    connection.setConnectTimeout(15 * 1000);
                    connection.setReadTimeout(15 * 1000);
                    connection.setRequestMethod("GET");
                    connection.setInstanceFollowRedirects(true);
                    connection.setDoInput(true);
                    connection.getInputStream();
                    String query = connection.getURL().getQuery();
                    if (query.contains("ACLogOut=5&RetCode=2") || !query.contains("RetCode")) {
                        handler.sendEmptyMessage(SUCCESS);
                    } else if (query.contains("ErrorMsg=dXNlcmlkIGVycm9yMg%3D%3D")) {
                        handler.sendEmptyMessage(ERROR_PASSWORD);
                    } else if (query.contains("ErrorMsg=dXNlcmlkIGVycm9yMQ%3D%3D")) {
                        handler.sendEmptyMessage(ERROR_USER);
                    } else {
                        Thread.sleep(1 * 1000);
                        doLogin();
                    }

                } catch (IOException | InterruptedException e) {
                    handler.sendEmptyMessage(FAIL);
                }
            }
        };
        loginThread.start();
    }

    private void showASnackBar(String msg, @Nullable View.OnClickListener action, @Nullable String actionText) {
        Snackbar snackbar = Snackbar.make(binding.coordinatorLayout, msg, Snackbar.LENGTH_LONG);
        if (actionText != null) {
            snackbar.setAction(actionText, action != null ? action : v -> {
            });
        }
        snackbar.show();
    }
}