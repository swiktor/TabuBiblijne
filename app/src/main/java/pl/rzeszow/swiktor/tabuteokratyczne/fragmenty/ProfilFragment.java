package pl.rzeszow.swiktor.tabuteokratyczne.fragmenty;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import pl.rzeszow.swiktor.tabuteokratyczne.NarzedziaWspolne;
import pl.rzeszow.swiktor.tabuteokratyczne.R;

public class ProfilFragment  extends Fragment {

    private NarzedziaWspolne.TitleChangeListener listener;

    public static ProfilFragment newInstance() {
        ProfilFragment fragment = new ProfilFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_webview, container, false);







        if (listener != null)
            listener.onTitleSet(getResources().getString(R.string.profil));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NarzedziaWspolne.TitleChangeListener) {
            listener = (NarzedziaWspolne.TitleChangeListener) context;
        } else {
            throw new ClassCastException(context.toString() + " musi  implementować interfejs:Utils.TitleChangeListener");
        }
    }

}