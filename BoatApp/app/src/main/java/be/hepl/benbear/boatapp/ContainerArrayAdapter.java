package be.hepl.benbear.boatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import be.hepl.benbear.iobrep.Container;

public class ContainerArrayAdapter extends ArrayAdapter<Container> {

    public ContainerArrayAdapter(Context context, int textViewResourceId,
                                 List<Container> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.container_row_layout, parent, false);
        ((TextView) rowView.findViewById(R.id.textViewContainerId)).setText(this.getItem(position).getId());
        ((TextView) rowView.findViewById(R.id.textViewContainerDestination)).setText(this.getItem(position).getDestination());
        ((TextView) rowView.findViewById(R.id.textViewDateArrival)).setText(this.getItem(position).getArrival().toString());
        return rowView;
    }
}
