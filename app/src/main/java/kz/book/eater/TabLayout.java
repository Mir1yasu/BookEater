package kz.book.eater;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class TabLayout extends LinearLayout {
    private LinearLayout main;
    private FrameLayout home, filter, bookmark, cart;
    private PageInterface pageInterface;
    private Context context;
    private BaseFragment parentFragment;
    private String[] pageDescriptions = {"Погрузитесь в незабываемый\n" +
            "мир книг!",
            "Отсортируйте книги по\n" +
            "предпочитаемым жанрам",
            "Ваши любимые книги\n" +
                    "собраны здесь",
            "Купите книги, чтобы получить\n" +
                    "к ним неограниченный доступ!"};
    private int index;
    public TabLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.tab_layout, this);
        init();
    }
    private void init() {
        main = findViewById(R.id.main);
        home = findViewById(R.id.home);
        filter = findViewById(R.id.filter);
        bookmark = findViewById(R.id.bookmark);
        cart = findViewById(R.id.cart);
        initClickListeners();
    }
    private void initClickListeners() {
        setClickListener(home, new HomeFragment(), pageDescriptions[0], 0);
        setClickListener(filter, new FilterFragment(), pageDescriptions[1], 1);
        setClickListener(bookmark, new BookmarkFragment(), pageDescriptions[2], 2);
        setClickListener(cart, new CartFragment(), pageDescriptions[3], 3);
    }
    private void setClickListener(FrameLayout button, Fragment fragment, String description, int position) {
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pageInterface != null)
                    pageInterface.getPageDescription(description);
                ((MainActivity) context).setFragment(fragment);
                for (int i = 0; i < main.getChildCount(); i++) {
                    ((ImageView) ((FrameLayout) main.getChildAt(i)).getChildAt(0)).setBackground(
                            ContextCompat.getDrawable(context, R.color.empty
                            ));
                }
                ((ImageView) button.getChildAt(0)).setBackground(
                        ContextCompat.getDrawable(context, R.drawable.shape_selected
                        ));
                index = position;
            }
        });
    }

    public void setPageInterface(PageInterface pageInterface) {
        this.pageInterface = pageInterface;
    }

    public void setParentFragment(BaseFragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public void performClick(int index, Fragment fragment) {
        this.index = index;
        ((MainActivity) context).setFragment(fragment);
        if (pageInterface != null && pageDescriptions.length < index)
            pageInterface.getPageDescription(pageDescriptions[index]);
        for (int i = 0; i < main.getChildCount(); i++) {
            ((ImageView) ((FrameLayout) main.getChildAt(i)).getChildAt(0)).setBackground(
                    ContextCompat.getDrawable(context, R.color.empty
                    ));
        }
        ((ImageView) ((FrameLayout) main.getChildAt(index)).getChildAt(0)).setBackground(
                ContextCompat.getDrawable(context, R.drawable.shape_selected
                ));
    }
    public void performClick(int index) {
        this.index = index;
        if (pageInterface != null && pageDescriptions.length < index)
            pageInterface.getPageDescription(pageDescriptions[index]);
        for (int i = 0; i < main.getChildCount(); i++) {
            ((ImageView) ((FrameLayout) main.getChildAt(i)).getChildAt(0)).setBackground(
                    ContextCompat.getDrawable(context, R.color.empty
                    ));
        }
        ((ImageView) ((FrameLayout) main.getChildAt(index)).getChildAt(0)).setBackground(
                ContextCompat.getDrawable(context, R.drawable.shape_selected
                ));
    }

    public String getPageDescriptions() {
        return pageDescriptions[index];
    }
}
