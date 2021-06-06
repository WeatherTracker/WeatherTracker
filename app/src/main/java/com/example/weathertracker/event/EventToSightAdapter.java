package com.example.weathertracker.event;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertracker.R;
import com.example.weathertracker.retrofit.Sight;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventToSightAdapter extends RecyclerView.Adapter<EventToSightAdapter.viewHolder> {
    private Context mContext;
    private List<Sight> mSights;

    public EventToSightAdapter(Context mContext, List<Sight> sights) {
        this.mContext = mContext;
        this.mSights = sights;
    }

    @NonNull
    @Override
    public EventToSightAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventToSightAdapter.viewHolder(LayoutInflater.from(mContext).inflate(R.layout.card_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventToSightAdapter.viewHolder holder, int position) {
        holder.tvSightCard.setText(mSights.get(position).getName());
    }

    @Override
    public int getItemCount() {
        try {
            return mSights.size();
        } catch (Exception e) {
            return 0;
        }
    }

    class viewHolder extends RecyclerView.ViewHolder {

        private TextView tvSightCard, tvSightName, tvSightTel, tvSightAdd, tvSightTicketInfo, tvSightDescribe, tvSightNote, tvSightClass, tvSightLastUpdateTime, btnNavigation;
        private AlertDialog.Builder dialogBuilder;
        private AlertDialog alertDialog;
        private View layoutView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvSightCard = itemView.findViewById(R.id.tvSightCard);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //create dialog
                    final int position = getAdapterPosition();
                    dialogBuilder = new AlertDialog.Builder(mContext);
                    layoutView = LayoutInflater.from(mContext).inflate(R.layout.sight_detail_layout, null);
                    tvSightName = layoutView.findViewById(R.id.sightName);
                    tvSightTel = layoutView.findViewById(R.id.sightTel);
                    tvSightAdd = layoutView.findViewById(R.id.sightAdd);
                    tvSightTicketInfo = layoutView.findViewById(R.id.sightTicketInfo);
                    tvSightDescribe = layoutView.findViewById(R.id.sightDescribe);
                    tvSightNote = layoutView.findViewById(R.id.sightNote);
                    tvSightClass = layoutView.findViewById(R.id.sightClass);
                    tvSightLastUpdateTime = layoutView.findViewById(R.id.sightLastUpdateTime);
                    btnNavigation = layoutView.findViewById(R.id.btnNavigation);
                    //init Field
                    tvSightName.setText(mSights.get(position).getName());
                    if (mSights.get(position).getTel() != null) {
                        tvSightTel.setText("電話: " + mSights.get(position).getTel());
                    } else {
                        tvSightTel.setText("電話: 無");

                    }
                    if (mSights.get(position).getAdd() != null) {
                        tvSightAdd.setText("地址: " + mSights.get(position).getAdd());
                    } else {
                        tvSightAdd.setText("地址: 無");

                    }
                    if (mSights.get(position).getTicketinfo() != null) {
                        tvSightTicketInfo.setText("門票: " + mSights.get(position).getTicketinfo());
                    } else {
                        tvSightTicketInfo.setText("門票: 無");

                    }
                    if (mSights.get(position).getDescription() != null) {
                        tvSightDescribe.setText("描述:\n\t\t\t\t" + mSights.get(position).getDescription());
                    } else {
                        tvSightDescribe.setText("描述: 無");

                    }
                    if (mSights.get(position).getRemarks() != null) {
                        tvSightNote.setText("備註:\n\t\t\t\t" + mSights.get(position).getRemarks());
                    } else {
                        tvSightNote.setText("備註: 無");

                    }
                    if (mSights.get(position).getOrgclass() != null) {
                        tvSightClass.setText("分類: " + mSights.get(position).getOrgclass());
                    } else {
                        tvSightClass.setText("分類: 無");

                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date temp = sdf.parse(mSights.get(position).getChangetime());
                        tvSightLastUpdateTime.setText("最後更新時間\n" + sdf.format(temp));
                    } catch (Exception e) {
                        tvSightLastUpdateTime.setText(mSights.get(position).getChangetime());
                    }
                    //setListener
                    btnNavigation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + mSights.get(position).getPy() + "," + mSights.get(position).getPx());
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            if (mapIntent.resolveActivity(mContext.getPackageManager()) != null) {
                                mContext.startActivity(mapIntent);
                            }
                        }
                    });
                    //show dialog
                    dialogBuilder.setView(layoutView);
                    alertDialog = dialogBuilder.create();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show();
                }
            });
        }
    }
}
