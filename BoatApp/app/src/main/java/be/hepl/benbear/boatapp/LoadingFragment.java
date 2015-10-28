package be.hepl.benbear.boatapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import be.hepl.benbear.iobrep.Container;
import be.hepl.benbear.iobrep.ContainerOutEndPacket;
import be.hepl.benbear.iobrep.ContainerOutPacket;
import be.hepl.benbear.iobrep.Criteria;
import be.hepl.benbear.iobrep.GetContainersPacket;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoadingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoadingFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private final LinkedList<Container> list = new LinkedList<Container>();
    private ListView listview;
    private ContainerArrayAdapter adapter;
    private int loadedContainerPosition;



    public static LoadingFragment newInstance() {
        LoadingFragment fragment = new LoadingFragment();
        return fragment;
    }

    public LoadingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list.add(new Container(0, 0, "Container1", "Paris", Calendar.getInstance().getTime()));
        list.add(new Container(0, 1, "Container2", "Rome", Calendar.getInstance().getTime()));
        list.add(new Container(1, 0, "Container3", "Londres", Calendar.getInstance().getTime()));
        list.add(new Container(1, 1, "Container4", "Bruxelles", Calendar.getInstance().getTime()));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        //((MainActivity)getActivity()).scs.isEstablished()
        View v = inflater.inflate(R.layout.fragment_loading, container, false);
        listview = (ListView) v.findViewById(R.id.listViewLoading);
        adapter = new ContainerArrayAdapter(v.getContext(),
                android.R.layout.simple_expandable_list_item_1, list);
        listview.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        // Get Container Click
        ((Button)v.findViewById(R.id.buttonGetContainers)).setOnClickListener(new View.OnClickListener() {
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
        ((Button) v.findViewById(R.id.buttonEndContainerOut)).setOnClickListener(new View.OnClickListener() {
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
            }
        });

        // ListView Element click
        ((ListView) v.findViewById(R.id.listViewLoading)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Load container")
                        .setMessage("Do you want to load the container \""+ list.get(position).getId() +"\" ?")
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

    public void containerLoaded() {
        list.remove(loadedContainerPosition);
        adapter.notifyDataSetChanged();
        listview.setEnabled(true);
    }

    public void clearContainerList() {
        list.clear();
        adapter.notifyDataSetChanged();
        listview.setEnabled(false);
    }

    public void fillContainerList(List<Container> listFill) {
        for (Container container : listFill) {
            list.add(container);
        }
        adapter.notifyDataSetChanged();
        listview.setEnabled(true);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class ContainerArrayAdapter extends ArrayAdapter<Container> {

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
}
