package com.example.oleg.imagelist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;


public class AuthenticationActivity extends ActionBarActivity {

    private Button buttonCancel = null;
    private WebView webviewAuth = null;
    private String strAuthURL = "https://instagram.com/oauth/authorize/?client_id=bd3d78d339ee4096a6a8a831f40c5315&redirect_uri=http://localhost&response_type=token";
    private String strRespURL = "http://localhost";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        buttonCancel = (Button)findViewById(R.id.buttonCancel);
        webviewAuth = (WebView)findViewById(R.id.webView);
        webviewAuth.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                if(url.contains(strRespURL)) {
                    String strToken = url.split("=")[1];
                    OnAuthed(strToken);
                }
            }
        });
        webviewAuth.getSettings().setJavaScriptEnabled(true);
        webviewAuth.loadUrl(strAuthURL);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenImageActivity();
            }
        });

    }

    private void OnAuthed(String strToken) {
        // we have received token just now
        InetHandler.getInstance().startFetchImages(strToken);
        OpenImageActivity();
    }

    private void OpenImageActivity() {
        Intent intent = new Intent(this, ImagesActivity.class);
        this.startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_authentication, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
