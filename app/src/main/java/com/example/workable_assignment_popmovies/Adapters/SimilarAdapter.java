package com.example.workable_assignment_popmovies.Adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.workable_assignment_popmovies.Models.Similar;
import com.example.workable_assignment_popmovies.NetworkUtils.GetMoviesAsync;
import com.example.workable_assignment_popmovies.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SimilarAdapter extends RecyclerView.Adapter<SimilarAdapter.SimilarsViewHolder> {

    GetMoviesAsync newTask;
    private List<Similar> mSimilars;
    private Context mContext;
    private static final String LOG_TAG = SimilarAdapter.class.getSimpleName();

    public SimilarAdapter(Context c, List<Similar> mSimilars) {
        this.mSimilars = mSimilars;
        mContext = c;
    }

    public void swapData(ArrayList<Similar> similars) {
        if (similars == null || similars.size() == 0)
            return;
        if (mSimilars != null && mSimilars.size() > 0)
            mSimilars.clear();
        mSimilars.addAll(similars);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public SimilarAdapter.SimilarsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.similar_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        SimilarAdapter.SimilarsViewHolder viewHolder = new SimilarAdapter.SimilarsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarAdapter.SimilarsViewHolder holder, int position) {
        Similar similar = mSimilars.get(position);
        // Show movie poster image

        Picasso.get()
                .load(similar.getImagePath())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.similarIv);
    }

    @Override
    public int getItemCount() {
        return (null != mSimilars ? mSimilars.size() : 0);
    }

    public class SimilarsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.similarIv)
        ImageView similarIv;

        public SimilarsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
