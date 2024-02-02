package com.example.refuseclassification.guidefragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.fragment.app.Fragment;
import com.example.refuseclassification.R;

public class DryFragment extends Fragment {

  private WebView webView;

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.frag_dry, container, false);
    webView = view.findViewById(R.id.dry_web);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.setWebViewClient(new WebViewClient());
    // 百度百科
    webView.loadUrl("https://baike.baidu.com/item/%E5%B9%B2%E5%9E%83%E5%9C%BE");
    return view;
  }
}
