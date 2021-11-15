package com.example.chatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.chatapp.R;
import com.example.chatapp.dialog.MessageOptionDialog;
import com.example.chatapp.dto.MessageDto;
import com.example.chatapp.dto.MyMedia;
import com.example.chatapp.enumvalue.MediaType;
import com.example.chatapp.ui.ViewImageActivity;
import com.example.chatapp.ui.ViewVideoActivity;

import java.util.List;

public class LineItemMediaAdapter extends RecyclerView.Adapter<LineItemMediaAdapter.ViewHolder> {

    private final Context context;
    private MessageDto messageDto;
    public final int COLUMNS;
    public final int CORNER = 30;

    public LineItemMediaAdapter(Context context, MessageDto messageDto, int numColumn) {
        this.context = context;
        this.messageDto = messageDto;
        this.COLUMNS = numColumn;
    }

    @NonNull
    @Override
    public LineItemMediaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.line_item_message_media, parent, false);
        return new LineItemMediaAdapter.ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull LineItemMediaAdapter.ViewHolder holder, int position) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        int sumRowCount = (int) Math.ceil((double) getItemCount() / COLUMNS);

        if (messageDto != null) {
            holder.itemView.setOnLongClickListener(v -> showReactionCreateDialog(messageDto));
            List<MyMedia> mediaList = messageDto.getMedia();
            if (mediaList != null && position < mediaList.size()) {
                MyMedia media = mediaList.get(position);
                if (media.getType().equals(MediaType.IMAGE)) {
                    holder.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, ViewImageActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("activityTitle", messageDto.getSender().getDisplayName());
                        bundle.putString("activitySubTitle", messageDto.getCreateAt());
                        bundle.putString("imageUrl", media.getUrl());
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    });
                    int currentRow = position / COLUMNS;
                    if (sumRowCount == 1) {
                        GranularRoundedCorners corners;
                        if (position % COLUMNS == 0) {
                            if (position == getItemCount() - 1)
                                corners = new GranularRoundedCorners(CORNER, CORNER, CORNER, CORNER);
                            else
                                corners = new GranularRoundedCorners(CORNER, 0, 0, CORNER);
                        } else if (position % COLUMNS == (COLUMNS - 1)) {
                            corners = new GranularRoundedCorners(0, CORNER, CORNER, 0);
                        } else {
                            if (position == getItemCount() - 1)
                                corners = new GranularRoundedCorners(0, CORNER, CORNER, 0);
                            else
                                corners = new GranularRoundedCorners(0, 0, 0, 0);
                        }
                        Glide.with(context).load(media.getUrl())
                                .placeholder(R.drawable.background_preload_image_video)
                                .apply(RequestOptions.bitmapTransform(corners))
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(holder.line_item_message_media_thumbnail);
                    } else {
                        if (currentRow == 0) {
                            GranularRoundedCorners corners;
                            if (position % COLUMNS == 0) {
                                if (position == getItemCount() - 1)
                                    corners = new GranularRoundedCorners(CORNER, CORNER, CORNER, CORNER);
                                else
                                    corners = new GranularRoundedCorners(CORNER, 0, 0, 0);

                            } else if (position % COLUMNS == (COLUMNS - 1)) {
                                corners = new GranularRoundedCorners(0, CORNER, 0, 0);
                            } else {
                                corners = new GranularRoundedCorners(0, 0, 0, 0);
                            }
                            Glide.with(context).load(media.getUrl())
                                    .placeholder(R.drawable.background_preload_image_video)
                                    .apply(RequestOptions.bitmapTransform(corners))
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(holder.line_item_message_media_thumbnail);
                        } else if (currentRow == (sumRowCount - 1)) {
                            GranularRoundedCorners corners;
                            if (position % COLUMNS == 0) {
                                if (position == getItemCount() - 1) {
                                    corners = new GranularRoundedCorners(0, 0, CORNER, CORNER);
                                } else {
                                    corners = new GranularRoundedCorners(0, 0, 0, CORNER);
                                }
                            } else if (position % COLUMNS == (COLUMNS - 1)) {
                                corners = new GranularRoundedCorners(0, 0, CORNER, 0);
                            } else {
                                corners = new GranularRoundedCorners(0, 0, 0, 0);
                            }
                            Glide.with(context).load(media.getUrl())
                                    .placeholder(R.drawable.background_preload_image_video)
                                    .apply(RequestOptions.bitmapTransform(corners))
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(holder.line_item_message_media_thumbnail);
                        } else {
                            GranularRoundedCorners corners = new GranularRoundedCorners(0, 0, 0, 0);
                            Glide.with(context).load(media.getUrl())
                                    .placeholder(R.drawable.background_preload_image_video)
                                    .apply(RequestOptions.bitmapTransform(corners))
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(holder.line_item_message_media_thumbnail);
                        }
                    }
                } else if (media.getType().equals(MediaType.VIDEO)) {
                    holder.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, ViewVideoActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("activityTitle", messageDto.getSender().getDisplayName());
                        bundle.putString("activitySubTitle", messageDto.getCreateAt());
                        bundle.putString("videoUrl", media.getUrl());
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    });
                    Drawable[] layers = new Drawable[2];
                    layers[1] = AppCompatResources.getDrawable(context, R.drawable.ic_play_video);

                    Glide.with(context).load(media.getUrl()).into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            layers[0] = resource;
                            LayerDrawable layerDrawable = new LayerDrawable(layers);
                            int currentRow = position / COLUMNS;
                            if (sumRowCount == 1) {
                                GranularRoundedCorners corners;
                                if (position % COLUMNS == 0) {
                                    if (position == getItemCount() - 1)
                                        corners = new GranularRoundedCorners(CORNER, CORNER, CORNER, CORNER);
                                    else
                                        corners = new GranularRoundedCorners(CORNER, 0, 0, CORNER);
                                } else if (position % COLUMNS == (COLUMNS - 1)) {
                                    corners = new GranularRoundedCorners(0, CORNER, CORNER, 0);
                                } else {
                                    if (position == getItemCount() - 1)
                                        corners = new GranularRoundedCorners(0, CORNER, CORNER, 0);
                                    else
                                        corners = new GranularRoundedCorners(0, 0, 0, 0);
                                }
                                Glide.with(context).load(layerDrawable)
                                        .placeholder(R.drawable.background_preload_image_video)
                                        .apply(RequestOptions.bitmapTransform(corners))
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .into(holder.line_item_message_media_thumbnail);
                            } else {
                                if (currentRow == 0) {
                                    GranularRoundedCorners corners;
                                    if (position % COLUMNS == 0) {
                                        if (position == getItemCount() - 1)
                                            corners = new GranularRoundedCorners(CORNER, CORNER, CORNER, CORNER);
                                        else
                                            corners = new GranularRoundedCorners(CORNER, 0, 0, 0);

                                    } else if (position % COLUMNS == (COLUMNS - 1)) {
                                        corners = new GranularRoundedCorners(0, CORNER, 0, 0);
                                    } else {
                                        corners = new GranularRoundedCorners(0, 0, 0, 0);
                                    }
                                    Glide.with(context).load(layerDrawable)
                                            .placeholder(R.drawable.background_preload_image_video)
                                            .apply(RequestOptions.bitmapTransform(corners))
                                            .transition(DrawableTransitionOptions.withCrossFade())
                                            .into(holder.line_item_message_media_thumbnail);
                                } else if (currentRow == (sumRowCount - 1)) {
                                    GranularRoundedCorners corners;
                                    if (position % COLUMNS == 0) {
                                        if (position == getItemCount() - 1) {
                                            corners = new GranularRoundedCorners(0, 0, CORNER, CORNER);
                                        } else {
                                            corners = new GranularRoundedCorners(0, 0, 0, CORNER);
                                        }
                                    } else if (position % COLUMNS == (COLUMNS - 1)) {
                                        corners = new GranularRoundedCorners(0, 0, CORNER, 0);
                                    } else {
                                        corners = new GranularRoundedCorners(0, 0, 0, 0);
                                    }
                                    Glide.with(context).load(layerDrawable)
                                            .placeholder(R.drawable.background_preload_image_video)
                                            .apply(RequestOptions.bitmapTransform(corners))
                                            .transition(DrawableTransitionOptions.withCrossFade())
                                            .into(holder.line_item_message_media_thumbnail);
                                } else {
                                    GranularRoundedCorners corners = new GranularRoundedCorners(0, 0, 0, 0);
                                    Glide.with(context).load(layerDrawable)
                                            .placeholder(R.drawable.background_preload_image_video)
                                            .apply(RequestOptions.bitmapTransform(corners))
                                            .transition(DrawableTransitionOptions.withCrossFade())
                                            .into(holder.line_item_message_media_thumbnail);
                                }
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
                } else if (media.getType().equals(MediaType.FILE)) {
                    holder.itemView.setOnClickListener(view -> {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(media.getUrl()));
                        context.startActivity(i);
                    });

                    Drawable[] layers = new Drawable[2];
                    layers[0] = AppCompatResources.getDrawable(context, R.drawable.background_file);
                    layers[1] = AppCompatResources.getDrawable(context, R.drawable.ic_round_file_24);
                    LayerDrawable layerDrawable = new LayerDrawable(layers);
                    int currentRow = position / COLUMNS;
                    if (sumRowCount == 1) {
                        GranularRoundedCorners corners;
                        if (position % COLUMNS == 0) {
                            if (position == getItemCount() - 1)
                                corners = new GranularRoundedCorners(CORNER, CORNER, CORNER, CORNER);
                            else
                                corners = new GranularRoundedCorners(CORNER, 0, 0, CORNER);
                        } else if (position % COLUMNS == (COLUMNS - 1)) {
                            corners = new GranularRoundedCorners(0, CORNER, CORNER, 0);
                        } else {
                            if (position == getItemCount() - 1)
                                corners = new GranularRoundedCorners(0, CORNER, CORNER, 0);
                            else
                                corners = new GranularRoundedCorners(0, 0, 0, 0);
                        }
                        Glide.with(context).load(layerDrawable)
                                .apply(RequestOptions.bitmapTransform(corners))
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(holder.line_item_message_media_thumbnail);
                    } else {
                        if (currentRow == 0) {
                            GranularRoundedCorners corners;
                            if (position % COLUMNS == 0) {
                                if (position == getItemCount() - 1)
                                    corners = new GranularRoundedCorners(CORNER, CORNER, CORNER, CORNER);
                                else
                                    corners = new GranularRoundedCorners(CORNER, 0, 0, 0);

                            } else if (position % COLUMNS == (COLUMNS - 1)) {
                                corners = new GranularRoundedCorners(0, CORNER, 0, 0);
                            } else {
                                corners = new GranularRoundedCorners(0, 0, 0, 0);
                            }
                            Glide.with(context).load(layerDrawable)
                                    .apply(RequestOptions.bitmapTransform(corners))
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(holder.line_item_message_media_thumbnail);
                        } else if (currentRow == (sumRowCount - 1)) {
                            GranularRoundedCorners corners;
                            if (position % COLUMNS == 0) {
                                if (position == getItemCount() - 1) {
                                    corners = new GranularRoundedCorners(0, 0, CORNER, CORNER);
                                } else {
                                    corners = new GranularRoundedCorners(0, 0, 0, CORNER);
                                }
                            } else if (position % COLUMNS == (COLUMNS - 1)) {
                                corners = new GranularRoundedCorners(0, 0, CORNER, 0);
                            } else {
                                corners = new GranularRoundedCorners(0, 0, 0, 0);
                            }
                            Glide.with(context).load(layerDrawable)
                                    .apply(RequestOptions.bitmapTransform(corners))
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(holder.line_item_message_media_thumbnail);
                        } else {
                            GranularRoundedCorners corners = new GranularRoundedCorners(0, 0, 0, 0);
                            Glide.with(context).load(layerDrawable)
                                    .apply(RequestOptions.bitmapTransform(corners))
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(holder.line_item_message_media_thumbnail);
                        }
                    }
                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean showReactionCreateDialog(MessageDto messageDto) {
        Log.d("message of user id", messageDto.getSender().getId());
        MessageOptionDialog messageOptionDialog = new MessageOptionDialog(context, messageDto);
        messageOptionDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), messageOptionDialog.getTag());
        return true;
    }

    @Override
    public int getItemCount() {
        if (messageDto == null)
            return 0;
        if (messageDto.getMedia() == null)
            return 0;
        return messageDto.getMedia().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView line_item_message_media_thumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            line_item_message_media_thumbnail = itemView.findViewById(R.id.line_item_message_media_thumbnail);
        }

    }

}