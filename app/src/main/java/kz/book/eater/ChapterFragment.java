package kz.book.eater;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChapterFragment extends Fragment {
    private View r;
    private RecyclerView chapter;
    private TextView chapterIndex;
    private FrameLayout navigation;
    private Button back, backward, forward;
    private String page;
    private List<String> audioPath = null;
    private ChapterAdapter adapter;
    private int index = 0;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public ChapterFragment(String page) {
        this.page = page;
    }
    public ChapterFragment(List<String>audioPath) {
        this.audioPath = audioPath;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        r = inflater.inflate(R.layout.fragment_chapter, container, false);
        chapter = r.findViewById(R.id.chapter);
        navigation = r.findViewById(R.id.navigation);
        back = r.findViewById(R.id.back);
        backward = r.findViewById(R.id.backward);
        forward = r.findViewById(R.id.forward);
        chapterIndex = r.findViewById(R.id.chapterIndex);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getContext()).setFragment(new HomeFragment());
            }
        });
        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChapters(page, --index);
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChapters(page, ++index);
            }
        });
        ((MainActivity) getContext()).hideTab();
        getChapters(page, index);
        ((MotionLayout)chapter.getParent()).addTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                navigation.setPadding(0,0,0, (int)(64 * progress));
                backward.setAlpha(Math.min((progress - 0.9f) * 10f, 1f));
                forward.setAlpha(Math.min((progress - 0.9f) * 10f, 1f));
                System.out.println(progress);
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                navigation.setPadding(0,0,0, (int)(64 * 1));
                backward.setAlpha(Math.min((motionLayout.getProgress() - 0.9f) * 10f, 1f));
                forward.setAlpha(Math.min((motionLayout.getProgress() - 0.9f) * 10f, 1f));
                System.out.println(12412124214214f);
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });
        return r;
    }
    private void getChapters(String name, int index) {
        if (audioPath == null) {
            adapter = new ChapterAdapter(getContext());
            executor.execute(() -> {
                try {
                    String[] files = getContext().getAssets().list(name);
                    chapterIndex.setText("Глава " + (index + 1));
                    System.out.println(files.length);
                    if (index == files.length - 1) {
                        forward.setScaleY(0);
                    } else {
                        forward.setScaleY(1);
                    }
                    if (index == 0) {
                        backward.setScaleY(0);
                    } else {
                        backward.setScaleY(1);
                    }
                    if (files != null && index >= 0 && index < files.length) {
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(getContext().getAssets().open(name + "/" + files[index])))) {

                            String line;
                            while ((line = reader.readLine()) != null) {
                                String finalLine = line;
                                handler.post(() -> adapter.addLine(finalLine));
                            }
                            handler.post(() -> adapter.addLine("\n\n\n\n\n"));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(() -> adapter.addLine("Error loading text: " + e.getMessage()));
                }
            });
        } else {
            adapter = new ChapterAdapter(getContext(), audioPath);
        }
        chapter.setAdapter(adapter);
        chapter.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.releaseMediaPlayer();
        }
        ((MainActivity) getContext()).showTab();
    }
}