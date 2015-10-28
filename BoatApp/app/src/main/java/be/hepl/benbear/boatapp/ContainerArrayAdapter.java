package be.hepl.benbear.boatapp;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

import be.hepl.benbear.iobrep.Container;

public class ContainerArrayAdapter extends ArrayAdapter<Container> {

    HashMap<Container, Integer> mIdMap = new HashMap<>();

    public ContainerArrayAdapter(Context context, int textViewResourceId,
                                 List<Container> objects) {
        super(context, textViewResourceId, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public long getItemId(int position) {
        Container item = getItem(position);
        return mIdMap.get(item);
    }
}
