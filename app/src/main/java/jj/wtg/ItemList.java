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
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by Лера on 03.01.2016.
 */
public class ItemList  extends Fragment{

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private ArrayList<HashMap<String,String>> myList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.list_fragment, container, false);
        ListView lv = (ListView)v.findViewById(R.id.concertListView);

        myList = new ArrayList<>();


        for(int i = 0; i < 30; i++){
            HashMap<String, String>  hm = new HashMap<>();
            hm.put(TITLE, "title " + i);
            hm.put(DESCRIPTION, "description " + i);
            myList.add(hm);
        }

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), myList , R.layout.list_item, new String[]{TITLE, DESCRIPTION},
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
