package kz.book.eater;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class FilterFragment extends BaseFragment {
    private View r;
    private FrameLayout container;
    private RecyclerView genres;
    private FilterAdapter adapter;
    private Button go;
    @Override
    protected void customizeView(View view) {
        container = view.findViewById(R.id.fragment);
        r = LayoutInflater.from(getContext()).inflate(R.layout.fragment_filter, container, true);
        genres = r.findViewById(R.id.genres);
        go = r.findViewById(R.id.go);
        adapter = new FilterAdapter(getContext());
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goClicked();
            }
        });
        genres.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        genres.setAdapter(adapter);
    }
    private void goClicked() {
        ((MainActivity) getContext()).setFragment(new HomeFragment(adapter.getGenres()));
    }

    @Override
    public void getSearchText(String text) {
        adapter.filter(text);
    }
}