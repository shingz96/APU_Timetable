package com.shing.aputimetable.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shing.aputimetable.R;
import com.shing.aputimetable.entity.ApuClass;

import java.util.List;

/**
 * Created by Shing on 30-Jul-17.
 */

public class ClassDetailsAdapter extends RecyclerView.Adapter<ClassDetailsAdapter.ClassDetailsHolder> {

    private List<ApuClass> mClassDataset;

    public ClassDetailsAdapter(@NonNull List<ApuClass> dataset) {
        mClassDataset = dataset;
    }

    public void setDataset(List dataset) {
        mClassDataset = dataset;
    }

    @Override
    public ClassDetailsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_class_item, parent, false);
        return new ClassDetailsHolder(view);
    }

    @Override
    public void onBindViewHolder(ClassDetailsHolder holder, int position) {
        if (mClassDataset != null) {
            holder.mTextViewSubject.setText(mClassDataset.get(position).getSubject());
            holder.mTextViewTime.setText(mClassDataset.get(position).getTime());
            holder.mTextViewLocation.setText(mClassDataset.get(position).getRoom() + " - " + mClassDataset.get(position).getLocation());
            holder.mTextViewLecturer.setText(mClassDataset.get(position).getLecturer());
        }

    }

    @Override
    public int getItemCount() {
        return mClassDataset == null ? 0 : mClassDataset.size();
    }

    class ClassDetailsHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewSubject, mTextViewTime, mTextViewLocation, mTextViewLecturer;

        private ClassDetailsHolder(View itemView) {
            super(itemView);
            mTextViewSubject = itemView.findViewById(R.id.txt_view_subject);
            mTextViewTime = itemView.findViewById(R.id.txt_view_time);
            mTextViewLocation = itemView.findViewById(R.id.txt_view_location);
            mTextViewLecturer = itemView.findViewById(R.id.txt_view_lecturer);
        }
    }
}
