package competitions.domain.com.sportcompetitions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import competitions.domain.com.sportcompetitions.model.Team;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {
    private List<Team> mTeams;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
        //void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    public static class TeamViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextViewTeamName;
        public TextView mTextViewCoachName;



        public TeamViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_for_pot);
            mTextViewTeamName = itemView.findViewById(R.id.textView_for_name_team);
            mTextViewCoachName = itemView.findViewById(R.id.textView_for_coach);

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

    public TeamAdapter(List<Team> teams) {
        mTeams = teams;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.teams_item, parent, false);
        return new TeamViewHolder(v,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        Team currentItem = mTeams.get(position);

        holder.mTextViewTeamName.setText(currentItem.getName_of_team());
        holder.mTextViewCoachName.setText(currentItem.getCoach());
    }

    @Override
    public int getItemCount() {
        return mTeams.size();
    }

    //public void sortByDate(){
    // Collections.sort(mTeams,Competition.Comparators.DATE);
    //}
    public void setTeams(List<Team> teams){
        mTeams = teams;
        notifyDataSetChanged();
    }
}
