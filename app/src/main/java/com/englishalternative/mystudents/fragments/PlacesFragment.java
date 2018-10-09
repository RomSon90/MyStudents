package com.englishalternative.mystudents.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
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
public class PlacesFragment extends Fragment {


    ExpandableListView listView;
    Map<String, List<Student>> places;
    List<Student> students;


    public PlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_places, container, false);

        listView = (ExpandableListView) v.findViewById(R.id.places_list_view);
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

        // Map places (group) to Student lists (children)
        places = new TreeMap<>();
        for (Student s : students) {
            String place = s.getPlace();
            if (place.equals(""))
                place = getString(R.string.default_place_name);

            if (!places.containsKey(place))
                places.put(place, new ArrayList<Student>());
            places.get(place).add(s);
        }


        PlacesListAdapter adapter = new PlacesListAdapter();
        listView.setAdapter(adapter);
    }

    class PlacesListAdapter extends BaseExpandableListAdapter {

        String[] placesList;

        PlacesListAdapter() {
            placesList = places.keySet().toArray(new String[places.size()]);
        }

        @Override
        public int getGroupCount() {
            return placesList.length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return places.get(placesList[groupPosition]).size();
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
                TextView placeNameView;
                TextView placeCountView;

            }

            String placeName = placesList[groupPosition];
            GroupViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                convertView = inflater.inflate(R.layout.place_group, null);
                holder = new GroupViewHolder();
                holder.placeNameView = (TextView) convertView.findViewById(R.id.place_name);
                holder.placeCountView = (TextView) convertView.findViewById(R.id.place_count);

                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }

            holder.placeNameView.setText(placeName);
            holder.placeCountView.setText(String.valueOf(getChildrenCount(groupPosition)));

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            class ChildViewHolder {
                TextView studentNameView;
                TextView studentClassView;
                ImageView newLessonImage;
            }

            final Student student = places.get(placesList[groupPosition]).get(childPosition);

            String studentName = student.getName();
            String studentClass = student.getClss();
            if (studentClass == null)
                studentClass = "";

            ChildViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                convertView = inflater.inflate(R.layout.place_child, null);
                holder = new ChildViewHolder();
                holder.newLessonImage = (ImageView) convertView.findViewById(R.id.places_add_lesson);
                holder.studentNameView = (TextView) convertView.findViewById(R.id.places_student_name);
                holder.studentClassView = (TextView) convertView.findViewById(R.id.places_student_class);
                convertView.setTag(holder);
            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }

            holder.studentNameView.setText(studentName);
            holder.studentClassView.setText(studentClass);

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
