package kr.ac.kopo.tripforu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class ScheduleContentAdapter extends RecyclerView.Adapter<ScheduleContentAdapter.ItemViewHolder>
implements ItemTouchHelperListener{
    private Context mContext;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_recycleviewschedule, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position){
        holder.onBind(ScheduleController.remainingSchedule.get(position),position);
    }

    @Override
    public int getItemCount() {
        return ScheduleController.remainingSchedule.size();
    }

    public void setItems(){
        notifyDataSetChanged();
    }

    public boolean onItemMove(int from_position, int to_position) {
        Schedule item = ScheduleController.remainingSchedule.get(from_position);
        ScheduleController.remainingSchedule.remove(from_position);
        ScheduleController.remainingSchedule.add(to_position,item);
        item.SetNumber(to_position);
        notifyItemMoved(from_position, to_position);
        return true;
    }

    public void onItemSwipe(int position) {
        ScheduleController.remainingSchedule.remove(position);
        notifyItemRemoved(position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView destination;
        TextView memberGroupId;
        RatingBar ratingBar;
        TextView grade;
        TextView startDate;
        TextView likes;
        TextView sharedCount;
        LinearLayout layout_ScheduleText;
        boolean isShared;
        private int number;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_ScheduleText = itemView.findViewById(R.id.LAYOUT_SchedulText);
            name = itemView.findViewById(R.id.TEXT_Name);
            destination = itemView.findViewById(R.id.TEXT_Destination);
            memberGroupId = itemView.findViewById(R.id.TEXT_MemberGroupId);
            ratingBar = itemView.findViewById(R.id.RATINGBAR);
            grade = itemView.findViewById(R.id.TEXT_Grade);
            startDate = itemView.findViewById(R.id.TEXT_StartDate);
            likes = itemView.findViewById(R.id.TEXT_Likes);
            sharedCount = itemView.findViewById(R.id.TEXT_SharedCount);
        }

        public void onBind(Schedule item, int position) {
            name.setText(item.GetName());
            destination.setText(item.GetDestination());
            memberGroupId.setText(item.GetMemberGroupId() + "");
            ratingBar.setRating((float) item.GetRatingBar());
            grade.setText(Math.floor(item.GetRatingBar() * 10) / 10 + "");
            startDate.setText(item.GetStartDate());
            likes.setText(item.GetLikes() + "");
            sharedCount.setText(item.GetSharedCount() + "");
            
            item.SetNumber(position);
        }
    }
}
