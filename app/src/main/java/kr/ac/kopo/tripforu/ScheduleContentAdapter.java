package kr.ac.kopo.tripforu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ScheduleContentAdapter extends RecyclerView.Adapter<ScheduleContentAdapter.ItemViewHolder>
implements ItemTouchHelperListener{
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.FRAMELAYOUT_RecyclerView);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_recycleviewschedule, parent, false);
        View scheduleListView = inflater.inflate(R.layout.activity_schedulelist, parent, false);
        ScheduleContentAdapter.ItemViewHolder viewHolder = new ScheduleContentAdapter.ItemViewHolder(view);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            int position = viewHolder.getAdapterPosition();
            LinearLayout chb_ScheduleList = view.findViewById(R.id.CHB_ScheduleList);
            LinearLayout chb_AllScheduleList = scheduleListView.findViewById(R.id.CHB_AllScheduleList);
            LinearLayout btn_ScheduleDelete = scheduleListView.findViewById(R.id.BTN_ScheduleDelete);
            @Override
            public boolean onLongClick(View view) {
                chb_AllScheduleList.setVisibility(View.VISIBLE);
                Log.d("chb_AllScheduleList", chb_AllScheduleList+"");
                Log.d("chb_ScheduleList", chb_ScheduleList+"");
                chb_ScheduleList.setVisibility(View.VISIBLE);
                btn_ScheduleDelete.setVisibility(View.VISIBLE);
                btn_ScheduleDelete.bringToFront();
                return false;
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = "";

                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                }
                Log.d("position", position+"");
                Log.d(data, "data: ");
            }
        });
        return viewHolder;
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
    public void onClick(View view){

    }
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView destination;
        TextView grade;
        TextView startDate;
        TextView likes;
        TextView sharedCount;
        LinearLayout layout_ScheduleText , layout_SharedContents, layout_SharedMark;
        boolean isShared;
        private int number;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_ScheduleText = itemView.findViewById(R.id.LAYOUT_SchedulText);
            name = itemView.findViewById(R.id.TEXT_Name);
            destination = itemView.findViewById(R.id.TEXT_Destination);
            grade = itemView.findViewById(R.id.TEXT_Grade);
            startDate = itemView.findViewById(R.id.TEXT_StartDate);
            likes = itemView.findViewById(R.id.TEXT_Likes);
            sharedCount = itemView.findViewById(R.id.TEXT_SharedCount);
            layout_SharedContents = itemView.findViewById(R.id.LAYOUT_SharedContents);
            layout_SharedMark = itemView.findViewById(R.id.LAYOUT_SharedMark);

        }

        public void onBind(Schedule item, int position) {
            if (item.CheckIsShared() == true){
                name.setText(item.GetName());
                destination.setText(item.GetDestination());
                grade.setText(Math.floor(item.GetRatingBar() * 10) / 10 + "");
                startDate.setText(item.GetStartDate());
                likes.setText(item.GetLikes() + "");
                sharedCount.setText(item.GetSharedCount() + "");
                item.SetNumber(position);
            }else{
                name.setText(item.GetName());
                destination.setText(item.GetDestination());
                grade.setText(Math.floor(item.GetRatingBar() * 10) / 10 + "");
                startDate.setText(item.GetStartDate());
                likes.setText(item.GetLikes() + "");
                sharedCount.setText(item.GetSharedCount() + "");
                item.SetNumber(position);
                layout_SharedMark.setVisibility(View.GONE);
                layout_SharedContents.setVisibility(View.GONE);
            }


        }
    }
}
