package jj.wtg;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;


public class ItemList  extends Fragment{


    ArrayList<ConcertsInfo> concertsInfo;
    IntervalForSearching intervalForSearching;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.list_fragment, container, false);
        ListView lv = (ListView)v.findViewById(R.id.concertListView);
        Bundle bundle = this.getArguments();


        if (bundle != null) {
            concertsInfo = (ArrayList<ConcertsInfo>) bundle.getSerializable("concertsInfo");
        }
        else Toast.makeText(getActivity(), "Мероприятий не найдено", Toast.LENGTH_SHORT).show();

        final ListAdapter adapter = new SimpleAdapter(getActivity(), concertsInfo , R.layout.list_item,
                new String[]{ConcertsInfo.TITLE, ConcertsInfo.DATE},
                new int[]{R.id.nameListTextView, R.id.venueListTextView});

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Fragment fragment = new Info();
                Bundle bundle = new Bundle();
                bundle.putSerializable("concertsInfo", concertsInfo.get(position));
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.frameContent, fragment).addToBackStack( "tag" ).commit();
            }
        });

        lv.setAdapter(adapter);


        return v;
    }


    //Parse ponominalu


}


