package pl.rzeszow.swiktor.tabuteokratyczne;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    int RC_SIGN_IN = 0;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;

    String zwrotka = "";
    String stan = "nowy";
    String personId = "";
    String imie = "";
    String nazwisko = "";
    String email = "";
    String zdjecieURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            stan = "nowy";
            personId = account.getId();
            imie = account.getDisplayName().substring(0, account.getDisplayName().indexOf(" "));

            if (!TextUtils.isEmpty(account.getDisplayName().substring(account.getDisplayName().indexOf(" ")).trim())) {
                nazwisko = account.getDisplayName().substring(account.getDisplayName().indexOf(" ")).trim();
            } else {
                nazwisko = "XYZ";
            }

            email = account.getEmail();
            zdjecieURL = account.getPhotoUrl().toString();
            ;
            wyslijID(stan, personId, imie, nazwisko, email);
        } catch (ApiException e) {
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            stan = "stary";
            personId = account.getId();
            imie = account.getDisplayName().substring(0, account.getDisplayName().indexOf(" "));

            if (!TextUtils.isEmpty(account.getDisplayName().substring(account.getDisplayName().indexOf(" ")).trim())) {
                nazwisko = account.getDisplayName().substring(account.getDisplayName().indexOf(" ")).trim();
            } else {
                nazwisko = "XYZ";
            }
            
            email = account.getEmail();
            zdjecieURL = account.getPhotoUrl().toString();
            wyslijID(stan, personId, imie, nazwisko, email);
        }
        super.onStart();
    }

    private void wyslijID(final String stan, final String personId, final String imie, final String nazwisko, final String email) {
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());

        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        String url = "https://swiktor.rzeszow.pl/JW/kalambury/googleCheck.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            zwrotka = obj.getString("zwrotka");

                            Intent intent = new Intent();
                            intent.setClass(LoginActivity.this, FragmentyActivity.class);
                            intent.putExtra("personId", personId);
                            intent.putExtra("imie", imie);
                            intent.putExtra("nazwisko", nazwisko);
                            intent.putExtra("email", email);
                            intent.putExtra("zdjecieURL", zdjecieURL);
                            intent.putExtra("zwrotka", zwrotka);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Nie wysłano nic", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("stan", stan);
                params.put("personId", personId);
                params.put("imie", imie);
                params.put("nazwisko", nazwisko);
                params.put("email", email);
                return params;
            }
        };
        queue.add(postRequest);
    }
}


