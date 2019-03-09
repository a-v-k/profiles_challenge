package org.digicraft.profiles.ui.profilelist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.digicraft.profiles.R;
import org.digicraft.profiles.data.model.Person;
import org.digicraft.profiles.data.viewmodel.ProfileListViewModel;
import org.digicraft.profiles.ui.profileadd.ProfileAddFragment;
import org.digicraft.profiles.ui.profileview.ProfileViewFragment;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileListFragment extends Fragment implements ProfileListAdapter.OnItemClickListener {

    private ProfileListViewModel mViewModel;
    private RecyclerView mRwProfileList;
    private ProfileListAdapter mProfileListAdapter;

    // TODO: 3/7/19 correct icon on FAB

    public static ProfileListFragment newInstance() {
        return new ProfileListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //noinspection ConstantConditions
        mViewModel = ViewModelProviders.of(getActivity()).get(ProfileListViewModel.class);

        mViewModel.getProfileListLiveData().observe(this, personList -> {
            if (personList != null) {
                mProfileListAdapter.updateDataset(personList);
                mProfileListAdapter.notifyDataSetChanged();
            }
        });

        mViewModel.refreshProfileList();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_list_fragment, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        //noinspection ConstantConditions
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> showAddPersonFragment());

        mRwProfileList = view.findViewById(R.id.rw_person_list);
        mRwProfileList.setHasFixedSize(true); // todo: check it
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRwProfileList.getContext(),
                layoutManager.getOrientation());
        mRwProfileList.addItemDecoration(dividerItemDecoration);
        mRwProfileList.setLayoutManager(layoutManager);
        mProfileListAdapter = new ProfileListAdapter(getContext(), new ArrayList<>(), this);
        mRwProfileList.setAdapter(mProfileListAdapter);

        return view;

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

    private void showAddPersonFragment() {
        //noinspection ConstantConditions
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ProfileAddFragment fragment = ProfileAddFragment.newInstance();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.content, fragment, "fragment_add_person");
        transaction.addToBackStack("fragment_add_person");
        transaction.commit();
    }

    @Override
    public void onItemClick(Person person) {
        if (person != null && person.getId() != null) {
            showProfileViewFragment(person.getId());
        }
    }

    private void showProfileViewFragment(Integer profileId) {
        Fragment fragment = ProfileViewFragment.newInstance(profileId);
        //noinspection ConstantConditions
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.content, fragment, "fragment_add_person");
        transaction.addToBackStack("fragment_add_person");
        transaction.commit();
    }


}
