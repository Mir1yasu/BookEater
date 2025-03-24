package kz.book.eater;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public abstract class BaseFragment extends Fragment implements PageInterface {
    private View r;
    private FrameLayout fragment;
    private SearchView search;
    private static TextView pageDescription;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        r = inflater.inflate(R.layout.fragment_base, container, false);
        fragment = r.findViewById(R.id.fragment);
        search = r.findViewById(R.id.search);
        pageDescription = r.findViewById(R.id.pageDescription);
        search.setQueryHint("Поиск");
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getSearchText(newText);
                return true;
            }
        });
        customizeView(r);
        ((MainActivity) getContext()).getPageDescription();
        return r;
    }
    public static void setPageDescription(String description) {
        pageDescription.setText(description);
    }
    @Override
    public void getPageDescription(String description) {
        pageDescription.setText(description);
    }
    public void setFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commitAllowingStateLoss();
    }
    protected abstract void customizeView(View view);
}