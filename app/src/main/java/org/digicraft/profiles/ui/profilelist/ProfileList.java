package org.digicraft.profiles.ui.profilelist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.digicraft.profiles.R;
import org.digicraft.profiles.data.viewmodel.ProfileListViewModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileList extends Fragment {

    private ProfileListViewModel mViewModel;
    private RecyclerView mRwProfileList;
    private ProfileListAdapter mProfileListAdapter;

    public static ProfileList newInstance() {
        return new ProfileList();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_list_fragment, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        //noinspection ConstantConditions
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> Snackbar.make(view1, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        mRwProfileList = view.findViewById(R.id.rw_person_list);
        mRwProfileList.setHasFixedSize(true); // todo: check it
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRwProfileList.getContext(),
                layoutManager.getOrientation());
        mRwProfileList.addItemDecoration(dividerItemDecoration);
        mRwProfileList.setLayoutManager(layoutManager);
        mProfileListAdapter = new ProfileListAdapter(getContext(), new ArrayList<>());
        mRwProfileList.setAdapter(mProfileListAdapter);

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //noinspection ConstantConditions
        mViewModel = ViewModelProviders.of(getActivity()).get(ProfileListViewModel.class);

        mViewModel.getProfileListLiveData().observe(this, personList -> {
            if (personList != null) {
                mProfileListAdapter.updateDataset(personList);
                mProfileListAdapter.notifyDataSetChanged();
            }
        });

        if (mViewModel.getProfileListLiveData().getValue() == null) {
            mViewModel.fillDummyData();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
