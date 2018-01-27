package com.siebren.roosterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.SslErrorHandler;
import android.widget.Button;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Context;
import android.graphics.Bitmap;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    private static final String DEFAULT_HOME_PAGE = "https://portal.rocwb.nl/CookieAuth.dll?GetLogon?curl=Z2F&reason=0&formdir=51";
    final Context context = this;
    int NumberOfWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
    int DayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

    WebView view;
    String url;

    Boolean loggingIn = false;
    boolean busyLoading = false;
    String username;
    String password;
    String klas;
    String week;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = (WebView) this.findViewById(R.id.webView);

        view.setWebViewClient(new SSLTolerentWebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url2, Bitmap favicon) {
                super.onPageStarted(view, url2, favicon);
                if(loggingIn && view.getUrl().equals("https://portal.rocwb.nl/default.aspx")) {
                    loggingIn = false;
                    view.loadUrl(DEFAULT_HOME_PAGE);
                }
                else if(view.getUrl().equals("https://portal.rocwb.nl/default.aspx")) {
                    view.loadUrl(url);
                    loggingIn = true;
                }
                else if(view.getUrl().equals(url)){
                    loggingIn = false;
                }

            }
        });

        LoadPrefs();
        setupLoginButton();
        setupHomeButton();
        view.loadUrl(DEFAULT_HOME_PAGE);
    }

    private void setupLoginButton() {
        final Button loginButton = (Button) findViewById(R.id.btnLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoadPrefs())
                    return;

                if (!busyLoading) {
                    initializeUrl();
                    login();
                } else {
                    Toast.makeText(MainActivity.this, "Pagina nog niet geladen! Even geduld alstublieft.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login() {
        final String Login = "document.querySelector(\"#username\").value = '" + username + "';";
        String Pass = "document.querySelector(\"#password\").value = '" + password + "';";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            view.evaluateJavascript(Login, null);
            view.evaluateJavascript(Pass, null);
            view.evaluateJavascript("document.querySelector(\"#SubmitCreds\").click();", null);
        } else {
            view.loadUrl(Login);
            view.loadUrl(Pass);
            view.loadUrl("document.querySelector(\"#SubmitCreds\").click();");
        }
        view.getSettings().setBuiltInZoomControls(true);
        view.getSettings().setSupportZoom(true);
    }

    private void setupHomeButton() {
        final Button homeButton = (Button) findViewById(R.id.btnHome);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loggingIn = false;
                view.loadUrl(DEFAULT_HOME_PAGE);
            }
        });
    }

    private class SSLTolerentWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (getBaseDomain(view.getUrl()).equals("rocwb.nl")) {
                handler.proceed();
            } else {
                handler.cancel();
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
                dlgAlert.setMessage("Sorry maar het domein was onveilig. Het aangevraagde request naar de webpagina is gecanceld. Neem contact op met developer alstublieft.");
                dlgAlert.setTitle("ERROR DOMAIN_NOT_SAFE");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(1);
                            }
                        });
                dlgAlert.create().show();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


    private void initializeUrl() {
        NumberOfWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

        if(week.toString().equals("2131624027"))
            NumberOfWeek++;

        if (NumberOfWeek < 10) {
            url = "https://portal.rocwb.nl/portalapps/roosters/RC/THW350/0" + NumberOfWeek;

        } else {
            url = "https://portal.rocwb.nl/portalapps/roosters/RC/THW350/" + NumberOfWeek;
        }
        int tempKlas = Integer.parseInt(klas.toString());
        tempKlas++;

        url = url + "/c/c" + String.format("%05d", tempKlas) + ".htm";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        startActivity(new Intent(this, OptiesActivity.class));

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean LoadPrefs() {

        view.getSettings().setJavaScriptEnabled(true);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        username = sp.getString("USER", "");
        password = sp.getString("PASS", "");
        klas = sp.getString("KLAS", "");
        week = sp.getString("WEEK","");


        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(klas)) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
            dlgAlert.setMessage("We hebben geen opgeslagen gegevens kunnen vinden op deze telefoon. Graag je gegevens invoeren in de opties.");
            dlgAlert.setTitle("Geen gegevens gevonden.");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(false);
            dlgAlert.create().show();
            return false;
        }
        return true;
    }

    public static String getHost(String url) {
        if (url == null || url.length() == 0)
            return "";

        int doubleslash = url.indexOf("//");
        if (doubleslash == -1)
            doubleslash = 0;
        else
            doubleslash += 2;

        int end = url.indexOf('/', doubleslash);
        end = end >= 0 ? end : url.length();

        int port = url.indexOf(':', doubleslash);
        end = (port > 0 && port < end) ? port : end;

        return url.substring(doubleslash, end);
    }

    public static String getBaseDomain(String url) {
        String host = getHost(url);

        int startIndex = 0;
        int nextIndex = host.indexOf('.');
        int lastIndex = host.lastIndexOf('.');
        while (nextIndex < lastIndex) {
            startIndex = nextIndex + 1;
            nextIndex = host.indexOf('.', startIndex);
        }
        if (startIndex > 0) {
            return host.substring(startIndex);
        } else {
            return host;
        }
    }
}

