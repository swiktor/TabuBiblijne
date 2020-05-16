package pl.rzeszow.swiktor.tabuteokratyczne.fragmenty;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import pl.rzeszow.swiktor.tabuteokratyczne.NarzedziaWspolne;
import pl.rzeszow.swiktor.tabuteokratyczne.R;

public class ProfilFragment extends Fragment {

    private NarzedziaWspolne.TitleChangeListener listener;

    public static ProfilFragment newInstance() {
        ProfilFragment fragment = new ProfilFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        String zdjecieURL = getArguments().getString("zdjecieURL");
        ImageView zdjecieImageView = (ImageView) view.findViewById(R.id.zdjecieImageView);

        String imie_nazwisko = getArguments().getString("imie_nazwisko");
        TextView imienazwiskoTextView = view.findViewById(R.id.imienazwiskoTextView);
        imienazwiskoTextView.setText(imie_nazwisko);

        String email = getArguments().getString("email");
        TextView emailTextView = view.findViewById(R.id.emailTextView);
        emailTextView.setText(email);

        String punkty = getArguments().getString("punkty");
        TextView punktyTextView = view.findViewById(R.id.punktyTextView);
        punktyTextView.setText(punkty);

        Picasso.get().load(zdjecieURL).placeholder(R.mipmap.ic_launcher) // optional
                .error(R.mipmap.ic_launcher) //if error
                .resize(600, 600)
                .into(zdjecieImageView, new com.squareup.picasso.Callback() {

                    @Override
                    public void onSuccess() {
                        Log.w("Picasso", "Pykło");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.w("Picasso", e);
                    }

                });

        if (listener != null)
            listener.onTitleSet(getResources().getString(R.string.profil));
        return view;
    }

}