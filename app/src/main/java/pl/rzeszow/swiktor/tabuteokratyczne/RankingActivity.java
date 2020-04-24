package pl.rzeszow.swiktor.tabuteokratyczne;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RankingActivity extends AppCompatActivity {

    ListView rankingListView;
    ArrayList<HashMap<String, String>> rankingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        rankingList = new ArrayList<>();
        rankingListView = (ListView) findViewById(R.id.rankingListView);

        pobierzRanking();
     }

    private void pobierzRanking() {
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());

        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        String url = "https://swiktor.rzeszow.pl/JW/kalambury/pobierzRanking.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        try {
                            JSONArray daneJson = new JSONArray(result);
                            for (int i = 0; i < daneJson.length(); i++) {
                                JSONObject sjo = daneJson.getJSONObject(i);
                                String kto = sjo.getString("kto");
                                String ilePunktow = sjo.getString("punkty");
                                HashMap<String, String> ranking = new HashMap<>();
                                ranking.put("kto", kto);
                                ranking.put("ilePunktow", ilePunktow);
                                rankingList.add(ranking);
                            }
                            ListAdapter adapter = new SimpleAdapter(
                                    RankingActivity.this,
                                    rankingList,
                                    R.layout.ranking_item,
                                    new String[]{"kto", "ilePunktow"},
                                    new int[]{R.id.kto, R.id.ilePunktow});

                            rankingListView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Nie pobrano rankingu", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("funkcja", "pobierzRanking");
                return params;
            }


        };
        queue.add(postRequest);
    }

}