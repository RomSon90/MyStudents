package com.englishalternative.mystudents.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.englishalternative.mystudents.LessonActivity;
import com.englishalternative.mystudents.R;
import com.englishalternative.mystudents.Student;
import com.englishalternative.mystudents.StudentActivity;
import com.englishalternative.mystudents.StudentData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClassesFragment extends Fragment {

    ExpandableListView listView;
    Map<String, List<Student>> classes;
    List<Student> students;


    public ClassesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_classes, container, false);

        listView = (ExpandableListView) v.findViewById(R.id.classes_list_view);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listView != null) {
            setListView();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void setListView() {
        StudentData studentData = StudentData.getInstance(getActivity());
        students = studentData.getAllStudents();

        // Map classes (group) to Student lists (children)
        classes = new TreeMap<>();
        for (Student s : students) {
            String clss = s.getClss();
            if (clss.equals(""))
                clss = getString(R.string.default_class_name);

            if (!classes.containsKey(clss))
                classes.put(clss, new ArrayList<Student>());
            classes.get(clss).add(s);
        }


        ClassesListAdapter adapter = new ClassesListAdapter();
        listView.setAdapter(adapter);
    }

class ClassesListAdapter extends BaseExpandableListAdapter {

    String[] classesList;

    ClassesListAdapter() {
        classesList = classes.keySet().toArray(new String[classes.size()]);
    }

    @Override
    public int getGroupCount() {
        return classesList.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return classes.get(classesList[groupPosition]).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        class GroupViewHolder {
            TextView classNameView;
            TextView classCountView;

        }

        String className = classesList[groupPosition];
        GroupViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            convertView = inflater.inflate(R.layout.class_group, null);
            holder = new GroupViewHolder();
            holder.classNameView = (TextView) convertView.findViewById(R.id.class_name);
            holder.classCountView = (TextView) convertView.findViewById(R.id.class_count);

            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        holder.classNameView.setText(className);
        holder.classCountView.setText(String.valueOf(getChildrenCount(groupPosition)));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        class ChildViewHolder {
            TextView studentNameView;
            TextView studentPlaceView;
            ImageView newLessonImage;
        }

        final Student student = classes.get(classesList[groupPosition]).get(childPosition);

        String studentName = student.getName();
        String studentPlace = student.getPlace();

        ChildViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            convertView = inflater.inflate(R.layout.class_child, null);
            holder = new ChildViewHolder();
            holder.newLessonImage = (ImageView) convertView.findViewById(R.id.class_add_lesson);
            holder.studentNameView = (TextView) convertView.findViewById(R.id.class_student_name);
            holder.studentPlaceView = (TextView) convertView.findViewById(R.id.class_student_place);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        holder.studentNameView.setText(studentName);
        holder.studentPlaceView.setText(studentPlace);

        holder.newLessonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newLesson = new Intent(getActivity(), LessonActivity.class);
                newLesson.putExtra("STUDENT_ID", (student.getId()));
                startActivity(newLesson);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StudentActivity.class);
                intent.putExtra("STUDENT_ID", student.getId());
                startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
}
