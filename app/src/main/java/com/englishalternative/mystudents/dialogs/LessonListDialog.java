package com.englishalternative.mystudents.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.englishalternative.mystudents.Lesson;
import com.englishalternative.mystudents.LessonActivity;
import com.englishalternative.mystudents.R;
import com.englishalternative.mystudents.StudentData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Roman on 04.07.2017.
 */

public class LessonListDialog extends DialogFragment {

    public AlertDialog dialog;
    public List<Lesson> lessonList;
    ListView listView;
    Context mContext;
    LayoutInflater inflater;
    String title;
    int type;
    public BaseAdapter adapter;
    boolean paidButton;

    public static LessonListDialog getInstance(List<Lesson> lessonList, String title, int type) {
        LessonListDialog dialog = new LessonListDialog();
        ArrayList<Integer> lessonIDs = new ArrayList<>();
        for (Lesson l : lessonList)
            lessonIDs.add(l.getId());
        Bundle b = new Bundle();
        b.putIntegerArrayList("lessonIDs", lessonIDs);
        b.putString("title", title);
        b.putInt("type", type);
        dialog.setArguments(b);
        return dialog;
    }

    public static LessonListDialog getInstance(List<Lesson> lessonList,
                                               String title,
                                               int type,
                                               boolean paidButton) {
        LessonListDialog dialog = new LessonListDialog();
        ArrayList<Integer> lessonIDs = new ArrayList<>();
        for (Lesson l : lessonList)
            lessonIDs.add(l.getId());
        Bundle b = new Bundle();
        b.putIntegerArrayList("lessonIDs", lessonIDs);
        b.putString("title", title);
        b.putInt("type", type);
        b.putBoolean("paidButton", paidButton);
        dialog.setArguments(b);
        return dialog;
    }

    public interface LessonListDialogListener {
        void onLessonListDismiss ();
    }

    public LessonListDialog() {
        // Empty constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Get lessons through IDs and show it
        if (getParentFragment() != null)
            mContext = getParentFragment().getActivity();
        else
            mContext = getActivity();
        inflater = LayoutInflater.from(mContext);
        final StudentData sData = StudentData.getInstance(mContext);
        lessonList = new ArrayList<>();
        Bundle b = getArguments();
        ArrayList<Integer> lessonIDs = b.getIntegerArrayList("lessonIDs");
        for (Integer id : lessonIDs) {
            Lesson l = sData.getLesson(id);
            lessonList.add(l);
        }
        title = b.getString("title");
        type = b.getInt("type");
        paidButton = b.getBoolean("paidButton");

        View view = inflater.inflate(R.layout.dialog_lessons, null);
        listView = (ListView) view.findViewById(R.id.lesson_list);

        switch (type) {
            default:
            case 0:
                adapter = new RegularLessonAdapter();
                break;
            case 1:
                adapter = new NamesLessonAdapter();
                break;
        }

        listView.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view).setTitle(title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        if (paidButton) {
            builder.setNegativeButton(R.string.all_paid, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    for (Lesson l : lessonList) {
                        sData.updateLesson(new Lesson(l.getId(),
                                sData.getStudent(l.getStudentID()),
                                l.getHomework(),
                                l.isHwDone(),
                                l.getTopic(),
                                l.getPrice(),
                                true,
                                l.getDuration(),
                                l.getDateUnix()));
                    }
                    LessonListDialogListener listener =
                            (LessonListDialogListener) getParentFragment();
                    listener.onLessonListDismiss();
                }
            });
        }
        dialog = builder.create();
        return dialog;

    }

    class RegularLessonAdapter extends BaseAdapter {

        private int red;
        private int lessonImageColor;

        RegularLessonAdapter() {
            red = ContextCompat.getColor(mContext, R.color.red_warning);
            lessonImageColor = ContextCompat.getColor(mContext, R.color.lesson_image_color);
        }


        @Override
        public int getCount() {
            return lessonList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get lesson from sData, inflate layout and populate with data


            final Lesson l = lessonList.get(position);
            String lessonDate = l.getDate();
            String topic = l.getTopic();
            String homeWork = l.getHomework();
            String duration = l.getDurationConverted();

            LessonViewHolder lessonViewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.expandable_lesson, null);

                lessonViewHolder = new LessonViewHolder();

                lessonViewHolder.lessonDateView = (TextView) convertView.findViewById
                        (R.id.expandable_lesson_date);
                lessonViewHolder.lessonDurationView = (TextView) convertView.findViewById
                        (R.id.expandable_lesson_duration);
                lessonViewHolder.topicView = (TextView) convertView.findViewById(
                        R.id.expandable_lesson_topic);
                lessonViewHolder.lessonHomeworkView = (TextView) convertView.findViewById
                        (R.id.expandable_lesson_homework);
                lessonViewHolder.durationIcon = (ImageView) convertView.findViewById(
                        R.id.expandable_lesson_duration_image);

                convertView.setTag(lessonViewHolder);



            } else {
                lessonViewHolder = (LessonViewHolder) convertView.getTag();
            }

            lessonViewHolder.lessonDateView.setText(lessonDate);
            lessonViewHolder.lessonDurationView.setText(duration);
            lessonViewHolder.topicView.setText(topic);
            lessonViewHolder.lessonHomeworkView.setText(homeWork);

            // Set duration icon to red if lesson not paid for
            if (l.isPaid())
                lessonViewHolder.durationIcon.setColorFilter(lessonImageColor);
            else
                lessonViewHolder.durationIcon.setColorFilter(red);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Start LessonActivity
                    Intent lessonDetails = new Intent(getActivity(), LessonActivity.class);
                    int studentId = l.getStudentID();
                    int lessonId = l.getId();
                    lessonDetails.putExtra("STUDENT_ID", studentId);
                    lessonDetails.putExtra("LESSON_ID", lessonId);
                    startActivity(lessonDetails);
                }
            });

            return convertView;
        }

        class LessonViewHolder {
            TextView lessonDateView;
            TextView lessonDurationView;
            TextView topicView;
            TextView lessonHomeworkView;
            ImageView durationIcon;
        }
    }

    class NamesLessonAdapter extends BaseAdapter {

        private int red;
        private int lessonImageColor;

        NamesLessonAdapter() {
            red = ContextCompat.getColor(mContext, R.color.red_warning);
            lessonImageColor = ContextCompat.getColor(mContext, R.color.lesson_image_color);
        }


        @Override
        public int getCount() {
            return lessonList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get lesson from sData, inflate layout and populate with data
            final Lesson l = lessonList.get(position);
            String lessonDate = l.getDate();
            int money = l.getPrice();
            String name = l.getStudent();

            NamesLessonAdapter.LessonViewHolder lessonViewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.stats_lesson, null);

                lessonViewHolder = new NamesLessonAdapter.LessonViewHolder();

                lessonViewHolder.lessonDateView = (TextView) convertView.findViewById
                        (R.id.expandable_lesson_date);
                lessonViewHolder.moneyTextView = (TextView) convertView.findViewById
                        (R.id.stats_money_text);
                lessonViewHolder.nameView = (TextView) convertView.findViewById(
                        R.id.stats_lesson_name);
                lessonViewHolder.moneyIcon = (ImageView) convertView.findViewById(
                        R.id.stats_money_image);

                convertView.setTag(lessonViewHolder);



            } else {
                lessonViewHolder = (NamesLessonAdapter.LessonViewHolder) convertView.getTag();
            }

            lessonViewHolder.lessonDateView.setText(lessonDate);
            lessonViewHolder.moneyTextView.setText(String.format(Locale.US, "%d", money));
            lessonViewHolder.nameView.setText(name);


            // Set duration icon to red if lesson not paid for
            if (l.isPaid())
                lessonViewHolder.moneyIcon.setColorFilter(lessonImageColor);
            else
                lessonViewHolder.moneyIcon.setColorFilter(red);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Start LessonActivity
                    Intent lessonDetails = new Intent(getActivity(), LessonActivity.class);
                    int studentId = l.getStudentID();
                    int lessonId = l.getId();
                    lessonDetails.putExtra("STUDENT_ID", studentId);
                    lessonDetails.putExtra("LESSON_ID", lessonId);
                    startActivity(lessonDetails);
                }
            });

            return convertView;
        }

        class LessonViewHolder {
            TextView lessonDateView;
            TextView moneyTextView;
            TextView nameView;
            ImageView moneyIcon;
        }
    }

}

