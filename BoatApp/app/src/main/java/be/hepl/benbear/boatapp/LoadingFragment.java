package be.hepl.benbear.boatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import java.util.LinkedList;
import java.util.List;

import be.hepl.benbear.iobrep.Container;
import be.hepl.benbear.iobrep.ContainerOutEndPacket;
import be.hepl.benbear.iobrep.ContainerOutPacket;
import be.hepl.benbear.iobrep.Criteria;
import be.hepl.benbear.iobrep.GetContainersPacket;


public class LoadingFragment extends Fragment {

    private final LinkedList<Container> list = new LinkedList<>();
    private ListView listview;
    private ContainerArrayAdapter adapter;
    private int loadedContainerPosition;

    public static LoadingFragment newInstance() {
        return new LoadingFragment();
    }

    public LoadingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_loading, container, false);
        listview = (ListView) v.findViewById(R.id.listViewLoading);
        adapter = new ContainerArrayAdapter(v.getContext(),
                android.R.layout.simple_expandable_list_item_1, list);
        listview.setAdapter(adapter);

        // Get Container Click
        v.findViewById(R.id.buttonGetContainers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View inputView = inflater.inflate(R.layout.getcontainer_layout, container, false);
                new AlertDialog.Builder(getActivity())
                        .setTitle("Get containers")
                        .setMessage("Enter the name of the destination")
                        .setView(inputView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String dest = ((EditText) inputView.findViewById(R.id.editTextDestination)).getText().toString();
                                Criteria crit = ((Switch) inputView.findViewById(R.id.switchOrder)).isChecked() ? Criteria.FIRST : Criteria.RANDOM;
                                if (!"".equals(dest)) {
                                    try {
                                        ((MainActivity) getActivity()).scs.writePacket(
                                                new GetContainersPacket(((MainActivity) getActivity()).scs.getSession(), dest, crit));
                                    } catch (ProtocolException e) {
                                        e.printStackTrace();
                                    }
                                    getActivity().findViewById(R.id.buttonGetContainers).setEnabled(false);
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

        // End Container Out Click
        final View b = v.findViewById(R.id.buttonEndContainerOut);
        b.setEnabled(false);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("End loading container")
                        .setMessage("Do you want to stop loading containers?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    ((MainActivity) getActivity()).scs.writePacket(
                                            new ContainerOutEndPacket(((MainActivity) getActivity()).scs.getSession()));
                                } catch (ProtocolException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                getActivity().findViewById(R.id.buttonGetContainers).setEnabled(false);
            }
        });

        // ListView Element click
        ((ListView) v.findViewById(R.id.listViewLoading)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Load container")
                        .setMessage("Do you want to load the container \"" + list.get(position).getId() + "\" ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    LoadingFragment.this.loadContainer(position);
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

    private void loadContainer(int position) throws ProtocolException {
        Container cont = list.get(position);
        ((MainActivity) getActivity()).scs.writePacket(new ContainerOutPacket(((MainActivity) getActivity()).scs.getSession(), cont.getId()));
        listview.setEnabled(false);
        loadedContainerPosition = position;
    }

    public Container containerLoaded() {
        Container cont = list.remove(loadedContainerPosition);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                listview.setEnabled(true);
            }
        });
        return cont;
    }

    public void clearContainerList() {
        list.clear();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                listview.setEnabled(false);
            }
        });
    }

    public void fillContainerList(List<Container> listFill) {
        for (Container container : listFill) {
            list.add(container);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });;
        listview.setEnabled(true);
    }
}
