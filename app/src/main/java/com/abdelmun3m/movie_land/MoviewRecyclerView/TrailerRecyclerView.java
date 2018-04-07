package com.abdelmun3m.movie_land.MoviewRecyclerView;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.abdelmun3m.movie_land.utilities.FontManger;

import com.abdelmun3m.movie_land.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by abdelmun3m on 08/10/17.
 */

public class TrailerRecyclerView extends RecyclerView.Adapter<TrailerRecyclerView.TrailerViewHolder> {



    private String[] listOfUrl ;
    private trailerClicke myTrailerClicke;
    Typeface icon ;

    public TrailerRecyclerView(trailerClicke trailerClickeObject){
        myTrailerClicke= trailerClickeObject;

    }

    public void updateTrailers(String[] newList) {
        listOfUrl = newList;

        notifyDataSetChanged();
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TrailerViewHolder holder ;
        View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_item,parent,false);
        holder = new TrailerViewHolder(viewHolder);


        return holder;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(position,listOfUrl[position]);
    }

    @Override
    public int getItemCount() {
        if (listOfUrl == null) {
            return 0;
        }else if(listOfUrl.length > 4){
            return 4;
        }else{
            return  listOfUrl.length;
        }
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TrailerViewHolder(View itemView) {
            super(itemView);
//            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);


        }

        public void bind(int id , String movieKey){


            itemView.setTag(movieKey);
            //icon = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/ionicons.ttf");
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == itemView.getId()) myTrailerClicke.onTrailerClick((String) itemView.getTag());
        }
    }

    public interface trailerClicke{
        void onTrailerClick(String url);
    }
}
