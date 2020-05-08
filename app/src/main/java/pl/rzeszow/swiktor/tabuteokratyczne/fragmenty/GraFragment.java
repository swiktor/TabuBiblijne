package pl.rzeszow.swiktor.tabuteokratyczne.fragmenty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import pl.rzeszow.swiktor.tabuteokratyczne.NarzedziaWspolne;
import pl.rzeszow.swiktor.tabuteokratyczne.R;

public class GraFragment extends Fragment {
    private Button losujHasloButton;
    private Button biblioteczkaButton;
    private Button takButton;
    private Button nieButton;

    private ListView zakazaneListView;

    private String zgadnieteStanString = "NIE";
    private String hasloString;

    private TextView zegarTextView;
    private TextView hasloTextView;
    private TextView zgadnieteTextView;

    private String[] biblioteczka;

    private String personId = "";

    private NarzedziaWspolne.TitleChangeListener listener;
    MediaPlayer mediaPlayer;

    public static GraFragment newInstance() {
        GraFragment fragment = new GraFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_main, container, false);

        personId = getArguments().getString("personId");

        zakazaneListView = (ListView) view.findViewById(R.id.zakazaneListView);
        zgadnieteTextView = (TextView) view.findViewById(R.id.zgadniete);
        takButton = (Button) view.findViewById(R.id.takButton);
        nieButton = (Button) view.findViewById(R.id.nieButton);

        biblioteczkaButton = (Button) view.findViewById(R.id.biblioteczkaButton);

        takButton.setTypeface(null, Typeface.BOLD);
        nieButton.setTypeface(null, Typeface.BOLD);

        hasloTextView = (TextView) view.findViewById(R.id.hasloTextView);

        losujHasloButton = (Button) view.findViewById(R.id.losujHasloButton);
        losujHasloButton.setText(R.string.losujHasloButton);

        zegarTextView = (TextView) view.findViewById(R.id.zegarTextView);
        losujHasloButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                downloadJSON("https://swiktor.rzeszow.pl/JW/kalambury/pobierzHaslo.php");

                zegar.cancel();
                zegar.start();

                zgadnieteTextView.setVisibility(View.VISIBLE);
                zakazaneListView.setVisibility(View.VISIBLE);
                takButton.setVisibility(View.VISIBLE);
                nieButton.setVisibility(View.VISIBLE);
                biblioteczkaButton.setVisibility(View.VISIBLE);

                losujHasloButton.setVisibility(View.GONE);

            }
        });

        takButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                zegar.cancel();
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                zakazaneListView.setVisibility(View.GONE);
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
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                zakazaneListView.setVisibility(View.GONE);
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
                Fragment fragment = BiblioteczkaFragment.newInstance();
                Bundle args = new Bundle();
                args.putString("biblioteczka", biblioteczka[0]);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.graAll, fragment);
                ft.addToBackStack(null);
                ft.commit();

            }
        });

        if (listener != null)
            listener.onTitleSet(getResources().getString(R.string.gra));
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);
        if (context instanceof NarzedziaWspolne.TitleChangeListener) {
            listener = (NarzedziaWspolne.TitleChangeListener) context;
        } else {
            throw new ClassCastException(context.toString() + " musi  implementować interfejs:Utils.TitleChangeListener");
        }
    }

    private final CountDownTimer zegar = new CountDownTimer(120100, 1000) {

        @SuppressLint("SetTextI18n")
        public void onTick(long millisUntilFinished) {
            zegarTextView.setText("Pozostało: " + millisUntilFinished / 1000 + " sekund");
            if (millisUntilFinished / 1000 == 10) {

                mediaPlayer = MediaPlayer.create(getContext(), R.raw.odliczanie);
                mediaPlayer.start();
            }

            zegarTextView.setTypeface(null, Typeface.BOLD);
        }

        public void onFinish() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            zegarTextView.setText(R.string.koniecCzasu);
            zegarTextView.setTypeface(null, Typeface.BOLD);

            zgadnieteTextView.setVisibility(View.INVISIBLE);
            zakazaneListView.setVisibility(View.GONE);
            takButton.setVisibility(View.INVISIBLE);
            nieButton.setVisibility(View.INVISIBLE);
            biblioteczkaButton.setVisibility(View.INVISIBLE);

            losujHasloButton.setVisibility(View.VISIBLE);

            zgadnieteStanString = "NIE";
            InsertData(zgadnieteStanString, hasloString);
        }
    };

    private void InsertData(final String zgadniete, final String haslo) {

        Cache cache = new DiskBasedCache(Objects.requireNonNull(getActivity()).getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());

        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        String url = "https://swiktor.rzeszow.pl/JW/kalambury/wyslijHaslo.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), getResources().getString(R.string.blad_aktualizacji), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("zgadniety", zgadniete);
                params.put("haslo", haslo);
                params.put("personId", personId);
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
        Cache cache = new DiskBasedCache(Objects.requireNonNull(getActivity()).getCacheDir(), 1024 * 1024); // 1MB cap
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
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Objects.requireNonNull(getContext()), R.layout.simplerow, zakazneLista);
                            zakazaneListView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Nie pobrano zakazanych", Toast.LENGTH_SHORT).show();
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
