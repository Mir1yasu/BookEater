package kz.book.eater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookHolder> {
    private Context context;
    private List<BookItem> items, removedItems = new ArrayList<>();
    private OnBookClickListener onBookItemClickListener;
    public interface OnBookClickListener {
        void onBookClick(BookItem item);
    }

    public void setOnBookItemClickListener(OnBookClickListener onBookItemClickListener) {
        this.onBookItemClickListener = onBookItemClickListener;
    }

    public void filter(String filter) {
        for (int i = items.size() - 1; i >= 0; i--) {
            if (
                    items.get(i).getTitle().toLowerCase().contains(filter.toLowerCase()) ||
                    items.get(i).getAuthor().toLowerCase().contains(filter.toLowerCase())
            ) {

            } else {
                removedItems.add(items.get(i));
                items.remove(i);
            }
        }
        for (int i = removedItems.size() - 1; i >= 0; i--) {
            if (
                    removedItems.get(i).getTitle().toLowerCase().contains(filter.toLowerCase()) ||
                    removedItems.get(i).getAuthor().toLowerCase().contains(filter.toLowerCase())
            ) {
                items.add(removedItems.get(i));
                removedItems.remove(i);
            }
        }
        notifyDataSetChanged();
    }

    public BookAdapter(Context context, List<BookItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookHolder(LayoutInflater.from(context).inflate(R.layout.view_book, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.author.setText(items.get(position).getAuthor());
        holder.image.setImageBitmap(BitmapFactory.decodeByteArray(items.get(position).getImage(), 0, items.get(position).getImage().length));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBookItemClickListener.onBookClick(items.get(position));
            }
        });
    }
    public void setGenre(List<String> genres) {
        Set<String> set = new HashSet<>(genres);
        for (int i = items.size() - 1; i >= 0; i--) {
            for (String element : items.get(i).getGenres()) {
                if (!set.contains(element)) {
                    items.remove(i);
                    break;
                }
            }
        }
    }
    public void setBooks(List<Long> ids) {
        Set<Long> set = new HashSet<>(ids);
        for (int i = 0; i < items.size(); i++) {
            if (!set.contains(items.get(i).getId())) {
                items.remove(i);
            }
        }
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class BookHolder extends RecyclerView.ViewHolder {
        private TextView title, author;
        private ImageView image;
        public BookHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);
            image = itemView.findViewById(R.id.image);
        }
    }
}
