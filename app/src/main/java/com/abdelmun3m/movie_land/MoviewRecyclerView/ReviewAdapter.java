package com.abdelmun3m.movie_land.MoviewRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abdelmun3m.movie_land.R;
import com.abdelmun3m.movie_land.Review;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abdelmun3m on 12/10/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {


    Review[] reviews= new Review[]{};
    reviewClick mReviewClick;
    public ReviewAdapter(reviewClick r){
        mReviewClick = r;
    }

    public void updateReviews(Review[] r){
        this.reviews = r;
        notifyDataSetChanged();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ReviewViewHolder holder ;
        View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_layout,parent,false);
        holder = new ReviewViewHolder(viewHolder);
        return holder;
    }
    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(reviews[position]);
    }

    @Override
    public int getItemCount() {
        if(reviews !=null)  return reviews.length;
        return 0;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tv_review_author)
            TextView reviewAuthor;
        @BindView(R.id.tv_review_content)
            TextView reviewContent;
        @BindView(R.id.tv_review_Url)
            TextView reviewUrl;
        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            reviewUrl.setOnClickListener(this);
        }

        public void bind(Review r){
            reviewAuthor.setText(r.author);
            reviewContent.setText(r.content);
            if(r.content.length() > 200){
                reviewUrl.setText("read More");
            }else{
                reviewUrl.setVisibility(View.GONE);
            }
            itemView.setTag(r.url);
        }
        @Override
        public void onClick(View v) {
            if(v.getId() == reviewUrl.getId()) {mReviewClick.onReviewClick((String) itemView.getTag());}
        }
    }

    public interface reviewClick{
        void onReviewClick(String url);
    }
}
