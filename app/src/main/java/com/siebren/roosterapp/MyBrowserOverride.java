package com.siebren.roosterapp;

import android.webkit.WebView;

/**
 * Created by Admin on 23-9-2015.
 */
public interface MyBrowserOverride {
    boolean ShouldOverrideUrlLoading(WebView view, String Url);
}
