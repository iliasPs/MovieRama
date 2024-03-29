package com.example.workable_assignment_popmovies.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.workable_assignment_popmovies.Models.Review;
import com.example.workable_assignment_popmovies.R;

import java.util.ArrayList;
import java.util.List;

public class MovieReviewsRvAdapter extends RecyclerView.Adapter<MovieReviewsRvAdapter.ReviewsViewHolder> {

    private List<Review> mReviews;
    private Context mContext;


    public MovieReviewsRvAdapter(Context c, List<Review> myMoviesReview) {
        mContext = c;
        mReviews = myMoviesReview;
    }


    public void swapData(ArrayList<Review> reviews) {
        if (reviews == null || reviews.size() == 0)
            return;
        if (mReviews != null && mReviews.size() > 0)
            mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MovieReviewsRvAdapter.ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ReviewsViewHolder viewHolder = new ReviewsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {

        Review review = this.mReviews.get(position);
        holder.authorName.setText(review.getReviewAuthor());
        holder.reviewContent.setText(review.getReviewContent());
    }

    @Override
    public int getItemCount() {
        return (null != mReviews ? mReviews.size() : 0);
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder {

        TextView authorName;
        TextView reviewContent;

        public ReviewsViewHolder(View itemView) {
            super(itemView);
            authorName = itemView.findViewById(R.id.reviewAuthor);
            reviewContent = itemView.findViewById(R.id.reviewContent);
        }
    }
}
