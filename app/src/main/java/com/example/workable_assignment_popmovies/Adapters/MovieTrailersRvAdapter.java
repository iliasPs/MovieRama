package com.example.workable_assignment_popmovies.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.workable_assignment_popmovies.Models.Trailer;
import com.example.workable_assignment_popmovies.R;
import com.example.workable_assignment_popmovies.Utils.MovieLaunchTrailerUtility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieTrailersRvAdapter extends RecyclerView.Adapter<MovieTrailersRvAdapter.TrailersViewHolder> {


    private List<Trailer> mTrailers;
    private Context mContext;
    private static final String LOG_TAG = MovieTrailersRvAdapter.class.getSimpleName();

    public MovieTrailersRvAdapter(Context c, List<Trailer> mTrailers) {
        this.mTrailers = mTrailers;
        mContext = c;
    }

    public void swapData(ArrayList<Trailer> trailers) {
        if (trailers == null || trailers.size() == 0)
            return;
        if (mTrailers != null && mTrailers.size() > 0)
            mTrailers.clear();
        mTrailers.addAll(trailers);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MovieTrailersRvAdapter.TrailersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        TrailersViewHolder viewHolder = new TrailersViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieTrailersRvAdapter.TrailersViewHolder holder, int position) {
        Trailer trailer = mTrailers.get(position);
        holder.trailerTitle.setText(trailer.getTrailerName());

        // Show movie poster image
        String thumbnailPath = "http://img.youtube.com/vi/" + trailer.getTrailerKey() + "/0.jpg";
        Picasso.get()
                .load(thumbnailPath)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.trailerThumb);
    }

    @Override
    public int getItemCount() {
        return (null != mTrailers ? mTrailers.size() : 0);
    }

    public class TrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.trailer_title)
        TextView trailerTitle;
        @BindView(R.id.trailer_thumb)
        AppCompatImageView trailerThumb;

        public TrailersViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            int position = this.getAdapterPosition();
            Trailer trailer = mTrailers.get(position);
            try {
                MovieLaunchTrailerUtility.launchTrailerVideoInYoutubeApp(context, trailer.getTrailerKey());
            } catch (ActivityNotFoundException exception) {
                MovieLaunchTrailerUtility.launchTrailerVideoInYoutubeBrowser(context, trailer.getTrailerKey());
            }
        }
    }
}

