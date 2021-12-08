package com.kinopoisklite.view;

import androidx.lifecycle.ViewModelProviders;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.kinopoisklite.BuildConfig;
import com.kinopoisklite.databinding.LoginFragmentBinding;
import com.kinopoisklite.security.AuthenticationProviders;
import com.kinopoisklite.viewModel.LoginViewModel;
import com.kinopoisklite.R;

import java.io.IOException;
import java.util.Map;

public class LoginFragment extends Fragment {
    private LoginFragmentBinding binding;

    private LoginViewModel mViewModel;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = LoginFragmentBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!binding.username.getText().toString().isEmpty()
                            && !binding.password.getText().toString().isEmpty()) {
                        mViewModel.authenticate(binding.username.getText().toString(),
                                binding.password.getText().toString()).observe(getViewLifecycleOwner(), sessionUser -> {
                            if (sessionUser != null)
                                Navigation.findNavController(v).popBackStack();
                            else
                                Toast.makeText(requireContext(), "Неверный логин или пароль!",
                                        Toast.LENGTH_LONG).show();
                        });
                    } else
                        Toast.makeText(requireContext(), "Введите логин и пароль!",
                                Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        binding.loginVK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> providerParams = mViewModel
                        .getProviderParams(AuthenticationProviders.VK);
                externalAuth(AuthenticationProviders.VK,
                        providerParams.get("uri"), providerParams.get("authCodeParam"),
                        providerParams.get("errorParam"));
            }
        });
        binding.loginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> providerParams = mViewModel
                        .getProviderParams(AuthenticationProviders.GOOGLE);
                externalAuth(AuthenticationProviders.GOOGLE,
                        providerParams.get("uri"), providerParams.get("authCodeParam"),
                        providerParams.get("errorParam"));
            }
        });
    }

    private void externalAuth(AuthenticationProviders providerCode,
                              String uri, String authCodeParam, String errorParam) {
        binding.externalAuth.setVisibility(View.VISIBLE);
        binding.externalAuth.getSettings().setJavaScriptEnabled(true);
        binding.loginForm.setVisibility(View.INVISIBLE);
        binding.externalAuth.loadUrl(uri);
        binding.externalAuth.getSettings().setUserAgentString(BuildConfig.USER_AGENT);
        binding.externalAuth.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains(authCodeParam + "=")) {
                    Uri uri = Uri.parse(url);
                    String authCode = uri.getQueryParameter(authCodeParam);
                    try {
                        mViewModel.authenticateExternal(authCode, providerCode).observe(getViewLifecycleOwner(), sessionUser -> {
                                    if (sessionUser != null)
                                        Navigation.findNavController(view).popBackStack();
                                    else
                                        Toast.makeText(requireContext(), "Ошибка авторизации!", Toast.LENGTH_LONG).show();
                                }
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    binding.externalAuth.loadUrl("");
                    binding.externalAuth.setVisibility(View.INVISIBLE);
                    binding.loginForm.setVisibility(View.VISIBLE);
                } else if (url.contains(errorParam + "=")) {
                    Uri uri = Uri.parse(url);
                    String errorMessage = uri.getQueryParameter(errorParam);
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                    binding.externalAuth.loadUrl("");
                    binding.externalAuth.setVisibility(View.INVISIBLE);
                    binding.loginForm.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        mViewModel = null;
    }
}