package tk.therealsuji.vtopchennai.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import tk.therealsuji.vtopchennai.R;
import tk.therealsuji.vtopchennai.adapters.EmptyStateAdapter;
import tk.therealsuji.vtopchennai.adapters.ReceiptsItemAdapter;
import tk.therealsuji.vtopchennai.adapters.SpotlightGroupAdapter;
import tk.therealsuji.vtopchennai.helpers.AppDatabase;
import tk.therealsuji.vtopchennai.interfaces.ReceiptsDao;
import tk.therealsuji.vtopchennai.interfaces.SpotlightDao;
import tk.therealsuji.vtopchennai.models.Receipt;
import tk.therealsuji.vtopchennai.models.Spotlight;

public class RecyclerViewFragment extends Fragment {
    public static final int TYPE_RECEIPTS = 1;
    public static final int TYPE_SPOTLIGHT = 2;

    int contentType;

    AppDatabase appDatabase;
    RecyclerView recyclerView;

    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    private void attachReceipts() {
        ReceiptsDao receiptsDao = this.appDatabase.receiptsDao();

        receiptsDao
                .getReceipts()
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Receipt>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onSuccess(@NonNull List<Receipt> receipts) {
                        if (receipts.isEmpty()) {
                            displayEmptyState(EmptyStateAdapter.TYPE_NO_DATA, null);
                            return;
                        }

                        recyclerView.setAdapter(new ReceiptsItemAdapter(receipts));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        displayEmptyState(EmptyStateAdapter.TYPE_ERROR, "Error: " + e.getLocalizedMessage());
                    }
                });
    }

    private void attachSpotlight() {
        SpotlightDao spotlightDao = this.appDatabase.spotlightDao();
        spotlightDao.get()
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Spotlight>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onSuccess(@NonNull List<Spotlight> spotlight) {
                        if (spotlight.isEmpty()) {
                            displayEmptyState(EmptyStateAdapter.TYPE_NO_DATA, null);
                            return;
                        }

                        recyclerView.setAdapter(new SpotlightGroupAdapter(spotlight));
                        spotlightDao.setRead()
                                .subscribeOn(Schedulers.single())
                                .subscribe();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        displayEmptyState(EmptyStateAdapter.TYPE_ERROR, "Error: " + e.getLocalizedMessage());
                    }
                });
    }

    private void displayEmptyState(int type, String message) {
        this.recyclerView.setAdapter(new EmptyStateAdapter(type, message));
        this.recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bottomNavigationVisibility = new Bundle();
        bottomNavigationVisibility.putBoolean("isVisible", false);
        getParentFragmentManager().setFragmentResult("bottomNavigationVisibility", bottomNavigationVisibility);
    }

    @Override
    public void onResume() {
        super.onResume();

        String screenName = "RecyclerView Fragment";
        Bundle arguments = this.getArguments();

        if (arguments != null) {
            int contentType = arguments.getInt("content_type", 0);
            switch (contentType) {
                case TYPE_RECEIPTS:
                    screenName = "Receipts";
                    break;
                case TYPE_SPOTLIGHT:
                    screenName = "Spotlight";
                    break;
            }
        }

        // Firebase Analytics Logging
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "RecyclerViewFragment");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        FirebaseAnalytics.getInstance(this.requireContext()).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View recyclerViewFragment = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        recyclerViewFragment.getRootView().setBackgroundColor(requireContext().getColor(R.color.secondary_container_95));
        recyclerViewFragment.getRootView().setOnTouchListener((view, motionEvent) -> true);

        View header = recyclerViewFragment.findViewById(R.id.linear_layout_header);
        this.recyclerView = recyclerViewFragment.findViewById(R.id.recycler_view);
        this.appDatabase = AppDatabase.getInstance(this.requireActivity().getApplicationContext());

        getParentFragmentManager().setFragmentResultListener("customInsets2", this, (requestKey, result) -> {
            int systemWindowInsetLeft = result.getInt("systemWindowInsetLeft");
            int systemWindowInsetTop = result.getInt("systemWindowInsetTop");
            int systemWindowInsetRight = result.getInt("systemWindowInsetRight");
            int systemWindowInsetBottom = result.getInt("systemWindowInsetBottom");
            float pixelDensity = getResources().getDisplayMetrics().density;

            header.setPaddingRelative(
                    systemWindowInsetLeft,
                    systemWindowInsetTop,
                    systemWindowInsetRight,
                    0
            );

            this.recyclerView.setPaddingRelative(
                    systemWindowInsetLeft,
                    0,
                    systemWindowInsetRight,
                    (int) (systemWindowInsetBottom + 20 * pixelDensity)
            );
        });

        int titleId = 0;
        Bundle arguments = this.getArguments();

        if (arguments != null) {
            titleId = arguments.getInt("title_id", 0);
            this.contentType = arguments.getInt("content_type", 0);
        }

        recyclerViewFragment.findViewById(R.id.image_button_back).setOnClickListener(view -> requireActivity().getSupportFragmentManager().popBackStack());
        ((TextView) recyclerViewFragment.findViewById(R.id.text_view_title)).setText(getString(titleId));

        switch (this.contentType) {
            case TYPE_RECEIPTS:
                this.attachReceipts();
                break;
            case TYPE_SPOTLIGHT:
                this.attachSpotlight();
                break;
        }

        return recyclerViewFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Bundle bottomNavigationVisibility = new Bundle();
        bottomNavigationVisibility.putBoolean("isVisible", true);
        getParentFragmentManager().setFragmentResult("bottomNavigationVisibility", bottomNavigationVisibility);

        if (this.contentType == TYPE_SPOTLIGHT) {
            getParentFragmentManager().setFragmentResult("getUnreadCount", new Bundle());
        }
    }
}
