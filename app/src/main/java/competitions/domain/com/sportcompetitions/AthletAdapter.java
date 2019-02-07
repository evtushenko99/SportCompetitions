package competitions.domain.com.sportcompetitions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import competitions.domain.com.sportcompetitions.model.Athlet;


public class AthletAdapter extends RecyclerView.Adapter<AthletAdapter.AthletViewHolder> {
    private List<Athlet> mAthletes;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        //void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class AthletViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextViewFirstName;
        public TextView mTextViewLastDate;


        public AthletViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_for_pot);
            mTextViewFirstName = itemView.findViewById(R.id.textView_for_fisrt_name_athlet);
            mTextViewLastDate = itemView.findViewById(R.id.textView_for_last_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public AthletAdapter(List<Athlet> athletes) {
        mAthletes = athletes;
    }

    @NonNull
    @Override
    public AthletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.athlets_item, parent, false);
        return new AthletViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AthletViewHolder holder, int position) {
        Athlet currentItem = mAthletes.get(position);

        holder.mTextViewFirstName.setText(currentItem.getAthlet_first_name());
        holder.mTextViewLastDate.setText(currentItem.getAthlet_last_name());
    }

    @Override
    public int getItemCount() {
        return mAthletes.size();
    }

    //public void sortByDate(){
    // Collections.sort(mAthletes,Competition.Comparators.DATE);
    //}
    public void setAthletes(List<Athlet> athletes) {
        mAthletes = athletes;
        notifyDataSetChanged();
    }
}
