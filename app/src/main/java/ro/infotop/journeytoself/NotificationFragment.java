package ro.infotop.journeytoself;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import ro.infotop.journeytoself.adapter.NotificationAdapter;
import ro.infotop.journeytoself.listener.RecyclerItemClickListener;
import ro.infotop.journeytoself.model.notificationModel.Notification;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.service.NotificationCenter;
import ro.infotop.journeytoself.service.UserController;

public class NotificationFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Notification>> {

    private static final String NOTIFICATIONS_LIST_KEY = "notiListKey";
    private final static int NOTIFICATION_LOADER_ID = 1;
    private boolean FIRST_LOAD_FLAG = true;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private User user;
    private NotificationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_notification, container, false);

        checkUser();

        assignView(layout);

        List<Notification> savedNotifs = null;
        if (savedInstanceState != null) {
            savedNotifs = savedInstanceState.getParcelableArrayList(NOTIFICATIONS_LIST_KEY);
        }

        setUpRecycler(savedNotifs);

        startLoading();

        return layout;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(NOTIFICATIONS_LIST_KEY, adapter.getNotificationArrayList());
    }

    private void assignView(View layout) {
        recyclerView = layout.findViewById(R.id.notifications_recycler);
        progressBar = layout.findViewById(R.id.progress_load_notifications);
    }

    private void setUpRecycler(List<Notification> savedNotifs) {
        List<Notification> adapterData = savedNotifs != null ? savedNotifs : new ArrayList<>();

        adapter = new NotificationAdapter(getContext(), adapterData);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position) {
                return false;
            }

            @Override
            public boolean onLongItemClick(View view, int position) {
                return false;
            }
        }));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                adapter.remove(position);
                adapter.notifyItemRemoved(position);
            }
        }).attachToRecyclerView(recyclerView);
    }

    // TODO: delete this
    private void checkUser() {
        user = UserController.getInstance().getLoggedUser();
        if (user == null) {
            //gfinish();
        }
    }

    private void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
        if (FIRST_LOAD_FLAG) {
            FIRST_LOAD_FLAG = false;
            getActivity().getSupportLoaderManager().initLoader(NOTIFICATION_LOADER_ID, null, this).forceLoad();
        } else {
            getActivity().getSupportLoaderManager().restartLoader(NOTIFICATION_LOADER_ID, null, this).forceLoad();
        }
    }

    @NonNull
    @Override
    public Loader<List<Notification>> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == NOTIFICATION_LOADER_ID) {
            return new NotificationLoaderTask(getContext(), user);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Notification>> loader, List<Notification> notifications) {
        progressBar.setVisibility(View.GONE);
        //adapter.clear();
        adapter.addAll(notifications);
        adapter.notifyItemRangeInserted(0, notifications.size());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Notification>> loader) {

    }

    private static class NotificationLoaderTask extends AsyncTaskLoader<List<Notification>> {

        private User user;

        public NotificationLoaderTask(@NonNull Context context, User user) {
            super(context);
            this.user = user;
        }

        @Nullable
        @Override
        public List<Notification> loadInBackground() {
            Deque<Notification> deque = NotificationCenter.retrieveNotifications(user);
            return new ArrayList<>(deque);
        }
    }
}
