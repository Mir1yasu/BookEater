package kz.book.eater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterHolder> {
    private final List<String> lines = new ArrayList<>();
    private List<String> audioPaths = null;
    private Context context;

    private MediaPlayer currentMediaPlayer = null;
    private Runnable updateSeekBarRunnable;
    private Handler handler = new Handler();
    private ChapterHolder currentHolder = null;

    public ChapterAdapter(Context context) {
        this.context = context;
    }

    public ChapterAdapter(Context context, List<String> audioPaths) {
        this.context = context;
        this.audioPaths = audioPaths;
    }

    public void addLine(String line) {
        lines.add(line);
        notifyItemInserted(lines.size() - 1);
    }

    @NonNull
    @Override
    public ChapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (audioPaths == null) {
            TextView textView = new TextView(parent.getContext());
            textView.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            textView.setPadding(16, 8, 16, 8);
            return new ChapterHolder(textView);
        } else {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            int dp12 = dpToPx(12, context);
            linearLayout.setPadding(dp12, dp12, dp12, dp12);

            ImageButton playButton = new ImageButton(context);
            LinearLayout.LayoutParams playButtonParams = new LinearLayout.LayoutParams(
                    dpToPx(48, context),
                    dpToPx(48, context)
            );
            playButton.setLayoutParams(playButtonParams);
            playButton.setId(View.generateViewId());
            playButton.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_tab_circled));
            playButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play));

            linearLayout.addView(playButton);

            SeekBar seekBar = new SeekBar(context);
            LinearLayout.LayoutParams seekBarParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            seekBarParams.gravity = Gravity.CENTER_VERTICAL;
            seekBar.setLayoutParams(seekBarParams);
            seekBar.setId(View.generateViewId());
            seekBar.setThumbTintList(ContextCompat.getColorStateList(context, R.color.deep_teal));
            seekBar.setProgressTintList(ContextCompat.getColorStateList(context, R.color.dark_teal));
            seekBar.setMax(1000);

            linearLayout.addView(seekBar);
            return new ChapterHolder(linearLayout, playButton, seekBar);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterHolder holder, @SuppressLint("RecyclerView") int position) {
        if (audioPaths == null) {
            holder.textView.setText(lines.get(position));
            holder.textView.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            System.out.println(position);
            String audioPath = audioPaths.get(position);
            AssetFileDescriptor afd = getAudioFile(audioPath);

            if (afd != null && position < audioPaths.size()) {
                if (currentHolder == holder && currentMediaPlayer != null && currentMediaPlayer.isPlaying()) {
                    holder.playButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause));
                } else {
                    holder.playButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play));
                    holder.seekBar.setProgress(0);
                }

                holder.playButton.setOnClickListener(v -> {
                    if (currentMediaPlayer != null && currentHolder != holder) {
                        stopCurrentMediaPlayer();
                    }

                    if (currentMediaPlayer == null || currentHolder != holder) {
                        try {
                            currentMediaPlayer = new MediaPlayer();
                            currentMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                            currentMediaPlayer.prepare();
                            currentMediaPlayer.start();
                            currentHolder = holder;
                            holder.playButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause));

                            updateSeekBar(holder);
                            currentMediaPlayer.setOnCompletionListener(mp -> {
                                handler.removeCallbacks(updateSeekBarRunnable);
                                holder.seekBar.setProgress(0);
                                holder.playButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play));
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (currentMediaPlayer.isPlaying()) {
                        currentMediaPlayer.pause();
                        holder.playButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play));
                    } else {
                        currentMediaPlayer.start();
                        updateSeekBar(holder);
                        holder.playButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause));
                    }
                });

                holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser && currentMediaPlayer != null && currentHolder == holder) {
                            int newPosition = progress * currentMediaPlayer.getDuration() / 1000;
                            currentMediaPlayer.seekTo(newPosition);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });
            }
        }
    }
    private void stopCurrentMediaPlayer() {
        if (currentMediaPlayer != null) {
            currentMediaPlayer.stop();
            currentMediaPlayer.release();
            currentMediaPlayer = null;
            if (currentHolder != null) {
                currentHolder.seekBar.setProgress(0);
                currentHolder.playButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play));
            }
            handler.removeCallbacks(updateSeekBarRunnable);
            currentHolder = null;
        }
    }

    private void updateSeekBar(ChapterHolder holder) {
        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentMediaPlayer != null && currentMediaPlayer.isPlaying()) {
                    int progress = (int) ((double) currentMediaPlayer.getCurrentPosition() / currentMediaPlayer.getDuration() * 1000);
                    holder.seekBar.setProgress(progress);
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.post(updateSeekBarRunnable);
    }
    @Override
    public int getItemCount() {
        if (audioPaths == null) {
            return lines.size();
        } else {
            return audioPaths.size();
        }
    }

    private int dpToPx(int dp, Context context) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    private AssetFileDescriptor getAudioFile(String path) {
        try {
            AssetManager assetManager = context.getAssets();
            return assetManager.openFd(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void releaseMediaPlayer() {
        if (currentMediaPlayer != null) {
            currentMediaPlayer.stop();
            currentMediaPlayer.release();
            currentMediaPlayer = null;
            handler.removeCallbacks(updateSeekBarRunnable);
            if (currentHolder != null) {
                currentHolder.seekBar.setProgress(0);
                currentHolder.playButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play));
            }
        }
    }
    private void updateSeekBar(SeekBar seekBar, MediaPlayer mediaPlayer) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int progress = (int) (1000.0 * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration());
                    seekBar.setProgress(progress);
                    updateSeekBar(seekBar, mediaPlayer);
                }
            }
        }, 100);
    }

    static class ChapterHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        final ImageButton playButton;
        final SeekBar seekBar;

        ChapterHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
            playButton = null;
            seekBar = null;
        }

        ChapterHolder(@NonNull View itemView, ImageButton playButton, SeekBar seekBar) {
            super(itemView);
            textView = null;
            this.playButton = playButton;
            this.seekBar = seekBar;
        }
    }
}
