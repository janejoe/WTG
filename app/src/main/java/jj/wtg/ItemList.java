package jj.wtg;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class ItemList  extends Fragment{

    ArrayList<ConcertsForList> myList;
    IntervalForSearching intervalForSearching;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.list_fragment, container, false);
        ListView lv = (ListView)v.findViewById(R.id.concertListView);
        Bundle bundle = this.getArguments();

        myList = new ArrayList<>();

        if (bundle != null) {
            myList = (ArrayList<ConcertsForList>) bundle.getSerializable("concertsForList");
        }
        else Toast.makeText(getActivity(), "empty!", Toast.LENGTH_SHORT).show();

        ListAdapter adapter = new SimpleAdapter(getActivity(), myList , R.layout.list_item,
                new String[]{ConcertsForList.TITLE, ConcertsForList.ID},
                new int[]{R.id.nameListTextView, R.id.venueListTextView});

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Fragment fragment = new Info();

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                fragmentTransaction.replace(R.id.frameContent, fragment).addToBackStack( "tag" ).commit();
            }
        });

        lv.setAdapter(adapter);


        return v;
    }
}
