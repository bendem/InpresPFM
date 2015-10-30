package be.hepl.benbear.boatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Calendar;
import java.util.LinkedList;

import be.hepl.benbear.iobrep.BoatArrivedPacket;
import be.hepl.benbear.iobrep.Container;
import be.hepl.benbear.iobrep.ContainerInEndPacket;
import be.hepl.benbear.iobrep.ContainerInPacket;


public class UnloadingFragment extends Fragment {

    private final LinkedList<Container> list = new LinkedList<Container>();
    private ListView listview;
    private ContainerArrayAdapter adapter;

    public static UnloadingFragment newInstance() {
        UnloadingFragment fragment = new UnloadingFragment();
        return fragment;
    }

    public UnloadingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_unloading, container, false);
        listview = (ListView) v.findViewById(R.id.listViewUnloading);
        adapter = new ContainerArrayAdapter(v.getContext(),
                android.R.layout.simple_expandable_list_item_1, list);
        listview.setAdapter(adapter);
        listview.setEnabled(false);

        // Boat Arrival Click
        v.findViewById(R.id.buttonBoatArrival).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View inputView = inflater.inflate(R.layout.boatarrival_layout, container, false);
                new AlertDialog.Builder(getActivity())
                        .setTitle("Boat arrival")
                        .setView(inputView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String id = ((EditText) inputView.findViewById(R.id.editTextBoatId)).getText().toString();
                                String dest = ((EditText) inputView.findViewById(R.id.editTextDestination)).getText().toString();
                                if (!"".equals(dest) && !"".equals(id)) {
                                    try {
                                        ((MainActivity) getActivity()).scs.writePacket(
                                                new BoatArrivedPacket(((MainActivity) getActivity()).scs.getSession(), id, dest));
                                    } catch (ProtocolException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });

        // Container Arrival Click
        v.findViewById(R.id.buttonContainerArrival).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View inputView = inflater.inflate(R.layout.containerin_layout, container, false);
                new AlertDialog.Builder(getActivity())
                        .setTitle("Container arrival")
                        .setView(inputView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String id = ((EditText) inputView.findViewById(R.id.editTextContainerId)).getText().toString();
                                String dest = ((EditText) inputView.findViewById(R.id.editTextContainerDestination)).getText().toString();
                                if (!"".equals(dest) && !"".equals(id)) {
                                    try {
                                        ((MainActivity) getActivity()).scs.writePacket(
                                                new ContainerInPacket(((MainActivity) getActivity()).scs.getSession(),
                                                        new Container(-1, -1, id, dest, Calendar.getInstance().getTime())));
                                    } catch (ProtocolException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });

        // End Container In Click
        v.findViewById(R.id.buttonEndContainerIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("End unloading container")
                        .setMessage("Do you want to stop unloading containers?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    ((MainActivity) getActivity()).scs.writePacket(
                                            new ContainerInEndPacket(((MainActivity) getActivity()).scs.getSession()));
                                } catch (ProtocolException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        return v;
    }

    public void containerUnloaded(Container container) {
        list.add(container);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void clearContainerList() {
        list.clear();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
