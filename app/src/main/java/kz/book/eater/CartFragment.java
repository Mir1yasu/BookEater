package kz.book.eater;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class CartFragment extends BaseFragment {
    private View r;
    private FrameLayout container;
    private RecyclerView books;
    private BookAdapter adapter;
    @Override
    protected void customizeView(View view) {
        container = view.findViewById(R.id.fragment);
        r = LayoutInflater.from(getContext()).inflate(R.layout.fragment_cart, container, true);
        books = r.findViewById(R.id.books);
        adapter = new BookAdapter(getContext(), JsonReader.readJson());
        adapter.setBooks(((MainActivity) getContext()).getUser().getCart());
        adapter.setOnBookItemClickListener(new BookAdapter.OnBookClickListener() {
            @Override
            public void onBookClick(BookItem item) {
                ((MainActivity) getContext()).setFragment(new DetailsFragment(item));
            }
        });
        books.setLayoutManager(new GridLayoutManager(getContext(),2));
        books.setAdapter(adapter);
    }

    @Override
    public void getSearchText(String text) {
        adapter.filter(text);
    }
}