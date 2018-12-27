package competitions.domain.com.sportcompetitions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import competitions.domain.com.sportcompetitions.model.Competition;


public class CompetitionAdapter extends RecyclerView.Adapter<CompetitionAdapter.CompetitionViewHolder> {
    private List<Competition> mCompetitions;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
        //void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    public static class CompetitionViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextViewName;
        public TextView mTextViewDate;

        public CompetitionViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_for_pot);
            mTextViewName = itemView.findViewById(R.id.textView_for_name_comp);
            mTextViewDate = itemView.findViewById(R.id.textView_for_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public CompetitionAdapter(List<Competition> competitions) {
        mCompetitions = competitions;
    }

    @NonNull
    @Override
    public CompetitionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.competitions_item, parent, false);
        return new CompetitionViewHolder(v,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CompetitionViewHolder holder, int position) {
        Competition currentItem = mCompetitions.get(position);

        holder.mTextViewName.setText(currentItem.getTournament_name());
        holder.mTextViewDate.setText(currentItem.getTime_of_comp());
    }

    @Override
    public int getItemCount() {
        return mCompetitions.size();
    }

    public void sortByDate(){
        Collections.sort(mCompetitions,Competition.Comparators.DATE);
    }
    public void setCompetitions(List<Competition> competitions){
        mCompetitions = competitions;
        notifyDataSetChanged();
    }
}
