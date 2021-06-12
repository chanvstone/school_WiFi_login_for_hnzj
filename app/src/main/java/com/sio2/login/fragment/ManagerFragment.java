package com.sio2.login.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sio2.login.MainViewModel;
import com.sio2.login.account.Account;
import com.sio2.login.databinding.FragmentManagerBinding;

public class ManagerFragment extends Fragment {
    private FragmentManagerBinding binding;
    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        init_webView();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.webViewManager.loadUrl("http://222.22.99.178:8080/Self/");
    }

    private void init_webView() {
        WebSettings settings = binding.webViewManager.getSettings();
        settings.setUseWideViewPort(true);//设定支持viewport
        settings.setLoadWithOverviewMode(true);   //自适应屏幕
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(true);//设定支持缩放
        settings.setJavaScriptEnabled(true);
        binding.webViewManager.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.startsWith("http://222.22.99.178:8080/Self/login")) {
                    view.loadUrl(
                            "javascript:" +
                                    "document.querySelector(\"input#account.form-control\").value=" + viewModel.account.getValue().getUsername() + ";" +
                                    "document.querySelector(\"input#password.form-control\").value=" + viewModel.account.getValue().getPassword() + ";" +
                                    "document.querySelector(\"button[type=submit][name=submit]\").click();"
                    );
                }
            }
        });

    }

}
