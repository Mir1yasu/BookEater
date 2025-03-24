package kz.book.eater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder> {
    private Context context;
    private HashMap<String, Boolean> genres = new HashMap<>(), removedGenres = new HashMap<>();
    private final String[] extensions = {".jpg", ".png", ".jpeg", ".bmp", ".gif"};
    public FilterAdapter(Context context) {
        this.context = context;
        for (String genre : Genres.getGenres()) {
            genres.put(genre, false);
        }
    }
    public void filter(String filter) {
        List<String> items = new ArrayList<>(
                Arrays.asList(
                        Arrays.stream(genres.keySet().toArray())
                                .map(String.class::cast)
                                .toArray(String[]::new)
                )
        );
        List<String> removedItems = new ArrayList<>(
                Arrays.asList(
                        Arrays.stream(removedGenres.keySet().toArray())
                                .map(String.class::cast)
                                .toArray(String[]::new)
                )
        );
        for (int i = items.size() - 1; i >= 0; i--) {
            if (
                    ((String) items.get(i)).toLowerCase().contains(filter.toLowerCase())
            ) {

            } else {
                removedGenres.put(items.get(i), false);
                genres.remove(items.get(i));
                items.remove(i);
            }
        }
        for (int i = removedItems.size() - 1; i >= 0; i--) {
            if (
                    removedItems.get(i).toLowerCase().contains(filter.toLowerCase())
            ) {
                genres.put(removedItems.get(i), false);
                removedGenres.remove(removedItems.get(i));
                removedItems.remove(i);
            }
        }
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public FilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilterHolder(LayoutInflater.from(context).inflate(R.layout.view_genres, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilterHolder holder, @SuppressLint("RecyclerView") int position) {
        if (genres.get(Genres.getGenre(position)) != null && genres.get(Genres.getGenre(position)) == true) {
            holder.shadow.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.light_shadow_teal)));
        } else {
            holder.shadow.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.shadow_teal)));
        }
        for (int i = 0; i < extensions.length; i++) {
            if (hasFile(position, extensions[i])) {
                try (InputStream imageStream = context.getAssets().open("illustrations/" + Genres.getGenre(position) + extensions[i])) {
                    if (imageStream != null) {
                        holder.image.setImageBitmap(BitmapFactory.decodeStream(imageStream));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        holder.genre.setText(Genres.getGenre(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genres.put(Genres.getGenre(position), !genres.get(Genres.getGenre(position)));
                notifyDataSetChanged();
                //if (onGenreClickListener != null) onGenreClickListener.onGenreClick(position);
            }
        });
    }
    private boolean hasFile(int position, String extension) {
        try (InputStream imageStream = context.getAssets().open("illustrations/" + Genres.getGenre(position) + extension)) {
            if (imageStream != null) {
                return true;
            }
        } catch (IOException e) {}
        return false;
    }
    @Override
    public int getItemCount() {
        return genres.size();
    }
    public List<String> getGenres() {
        List<String> genres = new ArrayList<>();
        for (int i = 0; i < this.genres.size(); i++) {
            if (this.genres.get(Genres.getGenre(i)) == true)
                genres.add(Genres.getGenre(i));
        }
        return genres;
    }
    protected static class FilterHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView genre;
        private View shadow;
        public FilterHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            ((FrameLayout) image.getParent()).setClipToOutline(true);
            genre = itemView.findViewById(R.id.genre);
            genre.setTextColor(0xFFFFFFFF);
            shadow = itemView.findViewById(R.id.shadow);
        }
    }
}
