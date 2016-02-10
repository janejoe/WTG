package jj.wtg;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import com.vk.sdk.api.model.VKList;

import java.io.File;
import java.util.ArrayList;

import java.util.TreeSet;

public class MainSearching extends Fragment implements View.OnClickListener {

    private Button search;
    private Button updateButton;
    ProgressBar progress;


    private TreeSet<String> artistSet = new TreeSet<>();
    private ArrayList<ConcertsForList> concertsForList = new ArrayList<>();
    private ArrayList<ConcertsInfo> concertsInfo = new ArrayList<>();

    private ConcertsDatabaseHelper concertsDatabaseHelper;
    private ParsePonominalu parsePonominalu;

    private SharedPreferences mSettings;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.interval_fragment, container, false);
        mSettings = getActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);

        search = (Button) v.findViewById(R.id.searchButton);
        updateButton = (Button) v.findViewById(R.id.dateSearchButton);
        progress = (ProgressBar) v.findViewById(R.id.progress);

        updateButton.setEnabled(false);
        search.setOnClickListener(this);
        updateButton.setOnClickListener(this);
        font();

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dateSearchButton:
                //update
                break;

            case R.id.searchButton:
                search.setEnabled(false);
                search.setAlpha((float) 0.5);
                artistSet = getArtistSet();
                break;
        }
    }

    private TreeSet getArtistSet() {

        VKParameters params = new VKParameters();
        params.put(VKApiConst.COUNT, 6000);

        VKRequest requestAudio = VKApi.audio().get(params);
        requestAudio.executeWithListener(new VKRequest.VKRequestListener() {
            @Override

            public void onComplete(VKResponse response) {
                super.onComplete(response);
                for (int i = 0; i < ((VKList<com.vk.sdk.api.model.VKApiAudio>) response.parsedModel).size(); i++) {
                    com.vk.sdk.api.model.VKApiAudio vkApiAudio = ((VKList<com.vk.sdk.api.model.VKApiAudio>) response.parsedModel).get(i);
                    if (vkApiAudio.artist.startsWith(" "))
                        artistSet.add(vkApiAudio.artist.substring(1));
                    else
                        artistSet.add(vkApiAudio.artist);
                }


                try {
                    new ScanAsyncTask().execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
            }
        });
        return artistSet;
    }

    private class ScanAsyncTask extends AsyncTask<Void, Void, ArrayList<ConcertsInfo>> {
        @Override
        protected void onPreExecute() {
            progress.setVisibility(getView().VISIBLE);
            search.setVisibility(getView().INVISIBLE);
            updateButton.setVisibility(getView().INVISIBLE);
        }

        @Override
        protected ArrayList<ConcertsInfo> doInBackground(Void... parameter) {

            parsePonominalu = new ParsePonominalu();

            //----Check DataBase
            File database = getActivity().getDatabasePath("concerts.db");
            if (!database.exists()) {
                // Database is not Found
                if (!(artistSet.isEmpty())) {
                    String content = null;
                    content = parsePonominalu.getAllConcerts();
                    //Get all concert's titles and id's
                    concertsDatabaseHelper = new ConcertsDatabaseHelper(getActivity(), "concerts.db", null, 1);
                    concertsDatabaseHelper.getConcertsId(content, concertsDatabaseHelper);

                }
            }
            else {
                concertsDatabaseHelper = new ConcertsDatabaseHelper(getActivity(), "concerts.db", null, 1);

            }
            int eng = mSettings.getInt(MainActivity.APP_PREFERENCES_ENG_IND,
                    ConcertsDatabaseHelper.indEng);
            int rus = mSettings.getInt(MainActivity.APP_PREFERENCES_RUS_IND,
                    ConcertsDatabaseHelper.indRus);

            concertsForList = concertsDatabaseHelper.searchWithoutTree(concertsDatabaseHelper, artistSet, concertsForList,
                    eng,rus);

            concertsInfo = parsePonominalu.fillInfoList(concertsForList, concertsInfo);
            return concertsInfo;
        }

        @Override
        protected void onPostExecute(ArrayList<ConcertsInfo> result) {
            super.onPostExecute(result);
            progress.setVisibility(View.GONE);

            if (!result.isEmpty()) {

                Fragment fragment = new ItemList();
                Bundle bundle = new Bundle();

                bundle.putSerializable("concertsInfo", result);
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameContent, fragment).addToBackStack("tag").commit();
            } else
                Toast.makeText(getActivity(), getString(R.string.notFound), Toast.LENGTH_LONG).show();
        }
    }

    private void font() {
        Typeface type2 = Typeface.createFromAsset(getActivity().getAssets(), RepeatData.TYPEFONT);
        search.setTypeface(type2);
        updateButton.setTypeface(type2);
    }
}

