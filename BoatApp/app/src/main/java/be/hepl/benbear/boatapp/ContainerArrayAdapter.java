package be.hepl.benbear.boatapp;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

import be.hepl.benbear.iobrep.Container;

public class ContainerArrayAdapter extends ArrayAdapter<Container> {

    public ContainerArrayAdapter(Context context, int textViewResourceId,
                                 List<Container> objects) {
        super(context, textViewResourceId, objects);
    }

}
