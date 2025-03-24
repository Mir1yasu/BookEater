package kz.book.eater;

import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

public class DetailsFragment extends Fragment {
    private View r;
    private BookItem item;
    private LinearLayout pages;
    private ImageView image, icon;
    private TextView title, author, description;
    private Button read, listen, bookmark, purchase;
    private ViewFlipper flipper;
    private User user;
    public DetailsFragment(BookItem item) {
        this.item = item;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        r = inflater.inflate(R.layout.fragment_details, container, false);
        image = r.findViewById(R.id.image);
        icon = r.findViewById(R.id.icon);
        title = r.findViewById(R.id.title);
        author = r.findViewById(R.id.author);
        pages = r.findViewById(R.id.pages);
        description = r.findViewById(R.id.description);
        read = r.findViewById(R.id.read);
        listen = r.findViewById(R.id.listen);
        bookmark = r.findViewById(R.id.bookmark);
        purchase = r.findViewById(R.id.purchase);
        flipper = r.findViewById(R.id.flipper);
        image.setImageBitmap(BitmapFactory.decodeByteArray(item.getImage(), 0, item.getImage().length));
        title.setText(item.getTitle());
        author.setText(item.getAuthor());
        description.setText(item.getDescription());
        if (item.getAudio() == null)
            ((LinearLayout) listen.getParent()).setVisibility(View.GONE);
        flipper.setDisplayedChild(0);
        user = ((MainActivity) getContext()).getUser();
        if (user.getCart().contains((long)item.getId())) {
            read.setText("Читать");
            icon.setImageResource(R.drawable.book);
        }
        if (user.getCart().contains((long)item.getId())) {
            purchase.setText("Убрать из корзины");
        }
        if (user.getFavorite().contains((long)item.getId())) {
            bookmark.setText("Убрать из закладок");
        }
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getContext()).setFragment(new ChapterFragment(item.getChapter()));
            }
        });
        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getContext()).setFragment(new ChapterFragment(item.getAudio()));
            }
        });
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getFavorite().contains((long)item.getId())) {
                    bookmark.setText("Добавить в закладки");
                    user.getFavorite().remove((long)item.getId());
                } else {
                    bookmark.setText("Убрать из закладок");
                    user.getFavorite().add((long)item.getId());
                }
                ((MainActivity) getContext()).setUser(user);
            }
        });
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < user.getCart().size(); i++) {
                    System.out.println(user.getCart().get(i));
                }
                if (user.getCart().contains((long)item.getId())) {
                    purchase.setText("Добавить в корзину");
                    user.getCart().remove((long)item.getId());
                } else {
                    purchase.setText("Убрать из корзины");
                    user.getCart().add((long)item.getId());
                }
                ((MainActivity) getContext()).setUser(user);
            }
        });
        List<Button> pageButtons = new ArrayList<>();
        for (int i = 0; i < pages.getChildCount(); i++) {
            Button page = ((Button) pages.getChildAt(i));
            pageButtons.add(page);
            final int finalI = i;
            page.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flipper.setDisplayedChild(finalI);
                    for (int j = 0; j < pageButtons.size(); j++) {
                        pageButtons.get(j).setBackground(new ColorDrawable(0x00000000));
                    }
                    pageButtons.get(finalI).setBackground(getContext().getDrawable(R.drawable.shape_underline));
                }
            });
        }
        return r;
    }
}