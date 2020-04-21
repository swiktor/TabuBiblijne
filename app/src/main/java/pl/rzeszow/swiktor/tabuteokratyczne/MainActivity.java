package pl.rzeszow.swiktor.tabuteokratyczne;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button losujHasloButton;
    Button biblioteczkaButton;
    Button takButton;
    Button nieButton;

    ListView zakazaneListView;

    String zgadnieteStanString = "NIE";
    String hasloString;

    TextView zegarTextView;
    TextView hasloTextView;
    TextView zgadnieteTextView;

    String[] biblioteczka;

    String personId = getIntent().getStringExtra("personId");
    String imie = getIntent().getStringExtra("imie");
    String zwrotka = getIntent().getStringExtra("zwrotka");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        zakazaneListView = (ListView) findViewById(R.id.zakazaneListView);
        zgadnieteTextView = (TextView) findViewById(R.id.zgadniete);
        takButton = (Button) findViewById(R.id.takButton);
        nieButton = (Button) findViewById(R.id.nieButton);

        biblioteczkaButton = (Button) findViewById(R.id.biblioteczkaButton);

        takButton.setTypeface(null, Typeface.BOLD);
        nieButton.setTypeface(null, Typeface.BOLD);

        hasloTextView = (TextView) findViewById(R.id.hasloTextView);

        losujHasloButton = (Button) findViewById(R.id.losujHasloButton);
        losujHasloButton.setText(R.string.losujHasloButton);

        zegarTextView = (TextView) findViewById(R.id.zegarTextView);

        losujHasloButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                downloadJSON("https://swiktor.rzeszow.pl/JW/kalambury/pobierzHaslo.php");

                zegar.cancel();
                zegar.start();

                zgadnieteTextView.setVisibility(View.VISIBLE);
                takButton.setVisibility(View.VISIBLE);
                nieButton.setVisibility(View.VISIBLE);
                biblioteczkaButton.setVisibility(View.VISIBLE);

                losujHasloButton.setVisibility(View.GONE);

            }
        });

        takButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                zegar.cancel();

                zgadnieteTextView.setVisibility(View.INVISIBLE);
                takButton.setVisibility(View.INVISIBLE);
                nieButton.setVisibility(View.INVISIBLE);
                biblioteczkaButton.setVisibility(View.INVISIBLE);

                losujHasloButton.setVisibility(View.VISIBLE);

                zgadnieteStanString = "TAK";
                InsertData(zgadnieteStanString, hasloString);
            }
        });

        nieButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                zegar.cancel();

                zgadnieteTextView.setVisibility(View.INVISIBLE);
                takButton.setVisibility(View.INVISIBLE);
                nieButton.setVisibility(View.INVISIBLE);
                biblioteczkaButton.setVisibility(View.INVISIBLE);

                losujHasloButton.setVisibility(View.VISIBLE);

                zgadnieteStanString = "NIE";
                InsertData(zgadnieteStanString, hasloString);
            }
        });

        biblioteczkaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, BiblioteczkaActivity.class);
                intent.putExtra("biblioteczka", biblioteczka[0]);
                startActivity(intent);
            }
        });

    }

    final CountDownTimer zegar = new CountDownTimer(121500, 1000) {

        @SuppressLint("SetTextI18n")
        public void onTick(long millisUntilFinished) {
            zegarTextView.setText("Pozostało: " + millisUntilFinished / 1000 + " sekund");
            zegarTextView.setTypeface(null, Typeface.BOLD);
        }

        public void onFinish() {
            zegarTextView.setText(R.string.koniecCzasu);
            zegarTextView.setTypeface(null, Typeface.BOLD);
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);

            zgadnieteTextView.setVisibility(View.INVISIBLE);
            takButton.setVisibility(View.INVISIBLE);
            nieButton.setVisibility(View.INVISIBLE);
            biblioteczkaButton.setVisibility(View.INVISIBLE);

            losujHasloButton.setVisibility(View.VISIBLE);

            zgadnieteStanString = "NIE";
            InsertData(zgadnieteStanString, hasloString);
        }
    };

    private void InsertData(final String zgadniete, final String haslo) {

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());

        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        String url = "https://swiktor.rzeszow.pl/JW/kalambury/wyslijHaslo.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Nie wysłano aktualizacji hasła", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("zgadniety", zgadniete);
                params.put("haslo", haslo);
                return params;
            }
        };
        queue.add(postRequest);

    }

    private void downloadJSON(final String urlWebService) {

        @SuppressLint("StaticFieldLeak")
        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    loadIntoListView(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute();
    }

    private void loadIntoListView(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String[] stocks = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            stocks[i] = obj.getString("haslo");
        }

        hasloString = stocks[new Random().nextInt(stocks.length)];
        pobierzZakazane(hasloString);
        hasloTextView.setText(hasloString);
        hasloTextView.setTypeface(null, Typeface.BOLD);
    }

    private void pobierzZakazane(final String hasloWylosowane) {
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());

        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        String url = "https://swiktor.rzeszow.pl/JW/kalambury/pobierzZakazane.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            String[] stocks = new String[jsonArray.length()];
                            biblioteczka = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                final JSONObject obj = (JSONObject) jsonArray.getJSONObject(i);
                                stocks[i] = obj.getString("zakazane");
                                biblioteczka[i] = obj.getString("biblioteczka");
                            }

                            ArrayList<String> zakazneLista = new ArrayList<String>(Arrays.asList(stocks));
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.simplerow, zakazneLista);
                            zakazaneListView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                         Toast.makeText(getApplicationContext(), "Nie pobrano zakazanych", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("haslo", hasloWylosowane);
                return params;
            }
        };
        queue.add(postRequest);
    }
}