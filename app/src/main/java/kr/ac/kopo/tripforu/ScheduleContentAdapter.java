package kr.ac.kopo.tripforu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ScheduleContentAdapter extends RecyclerView.Adapter<ScheduleContentAdapter.ItemViewHolder>
implements ItemTouchHelperListener, PageAdepter{
    private Context mContext;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_recycleviewschedule, parent, false);
        ScheduleContentAdapter.ItemViewHolder viewHolder = new ScheduleContentAdapter.ItemViewHolder(view);
        CheckBox chb_AllScheduleList = ((LinearLayout)parent.getParent().getParent().getParent()).findViewById(R.id.CHB_AllScheduleList);
        CheckBox chb_ScheduleList = view.findViewById(R.id.CHB_ScheduleList);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int position = viewHolder.getAdapterPosition();
                TextView text_AllScheduleList = ((LinearLayout)parent.getParent().getParent().getParent()).findViewById(R.id.TEXT_AllScheduleList);
                for (Schedule schedule:ScheduleController.remainingSchedule) {
                    View v = parent.findViewWithTag("schItem: "+schedule.GetId());
                    v.findViewById(R.id.CHB_ScheduleList).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.LAYOUT_ScrollHandle).setVisibility(View.VISIBLE);
                }
                LinearLayout layout_Scroll = view.findViewById(R.id.LAYOUT_ScrollHandle);
                chb_AllScheduleList.setVisibility(View.VISIBLE);
                text_AllScheduleList.setVisibility(View.VISIBLE);
                layout_Scroll.setVisibility(View.VISIBLE);

                View v1 = pageAdepter.SetAppBarAction(2, false, "삭제");
                View v2 = pageAdepter.SetAppBarAction(0, true, "취소");
                v1.setOnClickListener(v3 -> {
                    Toast.makeText(mContext, "삭제 버튼 클릭", Toast.LENGTH_SHORT).show();
                    for (Schedule schedule:ScheduleController.remainingSchedule) {
                        View v = parent.findViewWithTag("schItem: "+schedule.GetId());
                        CheckBox check = v.findViewById(R.id.CHB_ScheduleList);
                        /*if (check.isChecked() == true){
                            ScheduleController.remainingSchedule.remove(schedule.GetId());
                        }*/
                    }
                });
                v2.setOnClickListener(v4 -> {
                    Toast.makeText(mContext, "취소 버튼 클릭", Toast.LENGTH_SHORT).show();
                    pageAdepter.ResetAppBar();
                    chb_AllScheduleList.setVisibility(View.GONE);
                    layout_Scroll.setVisibility(View.GONE);
                    text_AllScheduleList.setVisibility(View.GONE);
                    for (Schedule schedule:ScheduleController.remainingSchedule) {
                        View v = parent.findViewWithTag("schItem: "+schedule.GetId());
                        v.findViewById(R.id.CHB_ScheduleList).setVisibility(View.GONE);
                        v.findViewById(R.id.LAYOUT_ScrollHandle).setVisibility(View.GONE);
                    }

                });
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
            }
        });

        chb_ScheduleList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox check;
                check = view.findViewById(R.id.CHB_ScheduleList);
                if (chb_AllScheduleList.isChecked() == true){
                    chb_AllScheduleList.setChecked(false);
                }else {
                    for (Schedule schedule:ScheduleController.remainingSchedule) {
                        View v = parent.findViewWithTag("schItem: "+schedule.GetId());
                        check = v.findViewById(R.id.CHB_ScheduleList);
                        if (check.isChecked() == false){
                            break;
                        }
                    }
                    if (check.isChecked() == true){
                        chb_AllScheduleList.setChecked(true);
                    }
                }
            }
        });

        chb_AllScheduleList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chb_AllScheduleList.isChecked() == true){
                    for (Schedule schedule:ScheduleController.remainingSchedule) {
                        View v = parent.findViewWithTag("schItem: "+schedule.GetId());
                        CheckBox check = v.findViewById(R.id.CHB_ScheduleList);
                        check.setChecked(true);
                    }
                }else {
                    for (Schedule schedule:ScheduleController.remainingSchedule) {
                        View v = parent.findViewWithTag("schItem: "+schedule.GetId());
                        CheckBox check = v.findViewById(R.id.CHB_ScheduleList);
                        check.setChecked(false);
                    }
                }
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

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        View itemHolder;
        TextView name;
        TextView destination;
        TextView grade;
        TextView startDate;
        TextView likes;
        TextView sharedCount;
        LinearLayout layout_ScheduleText , layout_SharedContents;
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
            itemHolder = itemView;
        }

        public void onBind(Schedule item, int position) {
                name.setText(item.GetName());
                destination.setText(item.GetDestination());
                grade.setText(Math.floor(item.GetRatingBar() * 10) / 10 + "");
                startDate.setText(item.GetStartDate());
                likes.setText(item.GetLikes() + "");
                sharedCount.setText(item.GetSharedCount() + "");
                item.SetNumber(position);
                itemHolder.setTag("schItem: "+item.GetId());
        }
    }
}
