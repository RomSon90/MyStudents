package com.englishalternative.mystudents.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.englishalternative.mystudents.R;
import com.englishalternative.mystudents.Student;
import com.englishalternative.mystudents.StudentData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Roman on 16.05.2017.
 *
 * This dialog lets user select students for various purposes
 */

public class StudentPickerDialog extends DialogFragment {

    List<Student> selectedStudents;
    List<Student> students;
    boolean[] selectedPlaces;
    boolean[] selectedClasses;
    LayoutInflater inflater;
    ShowList selectedButton;
    ListView listView;

    TextView studentsButton;
    TextView placesButton;
    TextView classesButton;

    private static int checkedColor;

    public enum ShowList {
        STUDENTS, CLASSES, PLACES
    }


    public interface StudentSelectedListener {
        void getStudents(List<Student> selectedStudents, ShowList studentListState);
    }

    public static StudentPickerDialog getInstance(List<Student> newSelectedStudents,
                                                  ShowList selectedButton) {
        // Get IDs of students who had already been selected and put in a bundle
        ArrayList<Integer> selectedStudentsIDS = new ArrayList<>();
        for (Student s : newSelectedStudents)
            selectedStudentsIDS.add(s.getId());

        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList("selected_ids", selectedStudentsIDS);
        bundle.putString("selected_button", selectedButton.name());
        StudentPickerDialog dialog = new StudentPickerDialog();
        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        checkedColor = ContextCompat.getColor(getParentFragment().getActivity(), R.color.colorAccent);

        // Get all students and show them
        inflater = getParentFragment().getActivity().getLayoutInflater();
        StudentData studentData = StudentData.getInstance(getParentFragment().getActivity());
        students = studentData.getAllStudents();
        Bundle bundle = getArguments();
        //List<Integer> selStudents = bundle.getIntegerArrayList("selected_ids");

        View view = inflater.inflate(R.layout.dialog_student_picker, null);
        listView = (ListView) view.findViewById(R.id.student_picker);

        studentsButton = (TextView) view.findViewById(R.id.students_button);
        placesButton = (TextView) view.findViewById(R.id.places_button);
        classesButton = (TextView) view.findViewById(R.id.classes_button);

        studentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeList(ShowList.STUDENTS);
            }
        });

        placesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeList(ShowList.PLACES);
            }
        });

        classesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeList(ShowList.CLASSES);
            }
        });

        selectedStudents = new ArrayList<>();
        selectedButton = ShowList.valueOf(bundle.getString("selected_button"));
        changeList(selectedButton);

        /* Now students must be selected again from scratch
        // Put previously selected students into working array
        selectedStudents = new ArrayList<>();
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            for (Integer id : selStudents)
                if (s.getId() == id) {
                    selectedStudents.add(s);
                    listView.setItemChecked(i, true);
                }
        }
*/
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StudentSelectedListener listener = (StudentSelectedListener) getParentFragment();
                listener.getStudents(selectedStudents, selectedButton);
            }
        });
        return builder.create();


    }

    void changeList(ShowList button) {
        selectedStudents.clear();
        switch (button) {
            case STUDENTS:
                setStudentsList();
                selectedButton = ShowList.STUDENTS;
                studentsButton.setTextColor(checkedColor);
                placesButton.setTextColor(Color.BLACK);
                classesButton.setTextColor(Color.BLACK);
                break;
            case PLACES:
                setPlacesList();
                selectedButton = ShowList.PLACES;
                studentsButton.setTextColor(Color.BLACK);
                placesButton.setTextColor(checkedColor);
                classesButton.setTextColor(Color.BLACK);
                break;
            case CLASSES:
                setClassesList();
                selectedButton = ShowList.CLASSES;
                studentsButton.setTextColor(Color.BLACK);
                placesButton.setTextColor(Color.BLACK);
                classesButton.setTextColor(checkedColor);
                break;
        }
    }

    void setStudentsList() {
        listView.setAdapter(new StudentAdapter());
        // Add or remove students from working array
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student s = students.get(position);

                if (selectedStudents.contains(s)) {
                    selectedStudents.remove(s);
                    listView.setItemChecked(position, false);
                } else {
                    selectedStudents.add(s);
                    listView.setItemChecked(position, true);
                }
            }
        });
    }

    void setPlacesList() {
        final String TAG = "setPlacesList";
        listView.setAdapter(new PlaceAdapter(students));
        selectedPlaces = new boolean[listView.getCount()];
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                for (Student s : students) {
                    String sListPlace = (String) adapterView.getAdapter().getItem(position);
                    String sPlace = s.getPlace();
                    if (sListPlace.equals("")) {
                        sListPlace = getString(R.string.default_place_name);
                    }

                    if (sPlace.equals("")) {
                        sPlace = getString(R.string.default_place_name);
                    }

                    if (sPlace.equals(sListPlace)) {
                        if (selectedPlaces[position]) {
                            selectedStudents.remove(s);
                        } else {
                            selectedStudents.add(s);
                        }
                    }
                }
                if (selectedPlaces[position]) {
                    listView.setItemChecked(position, false);
                    selectedPlaces[position] = false;
                } else {
                    listView.setItemChecked(position, true);
                    selectedPlaces[position] = true;
                }
            }
        });
    }

    void setClassesList() {
        listView.setAdapter(new ClassAdapter(students));
        selectedClasses = new boolean[listView.getCount()];
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                for (Student s : students) {
                    String sListClass = (String) adapterView.getAdapter().getItem(position);
                    String sClass = s.getClss();
                    if (sListClass.equals("")) {
                        sListClass = getString(R.string.default_class_name);
                    }

                    if (sClass.equals("")) {
                        sClass = getString(R.string.default_class_name);
                    }

                    if (sClass.equals(sListClass)) {
                        if (selectedClasses[position]) {
                            selectedStudents.remove(s);

                        } else {
                            selectedStudents.add(s);

                        }
                    }
                }
                if (selectedClasses[position]) {
                    listView.setItemChecked(position, false);
                    selectedClasses[position] = false;

                } else {
                    listView.setItemChecked(position, true);
                    selectedClasses[position] = true;
                }
            }
        });
    }

    // Adapter for student views in the list
    private class StudentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return students.size();
        }

        @Override
        public Object getItem(int position) {
            return students.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Student student = students.get(position);
            ViewHolder vHolder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.dialog_student_picker_list_item, null);

                vHolder = new ViewHolder();
                vHolder.name = (TextView) convertView.findViewById(R.id.list_student_name);
                //vHolder.place = (TextView) convertView.findViewById(R.id.list_student_place);

                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }

            vHolder.name.setText(student.getName());
            //vHolder.place.setText(student.getPlace());

            return convertView;
        }

        private class ViewHolder {
            TextView name;
            //TextView place;
        }
    }

    // Adapter for student views in the list
    private class PlaceAdapter extends BaseAdapter {

        Map<String, List<Student>> places;
        String[] placesList;

        PlaceAdapter(List<Student> students) {
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
            placesList = places.keySet().toArray(new String[places.size()]);
        }

        @Override
        public int getCount() {
            return placesList.length;
        }

        @Override
        public Object getItem(int position) {
            return placesList[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            class GroupViewHolder {
                TextView placeNameView;
                //TextView placeCountView;

            }

            String placeName = placesList[position];
            GroupViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                convertView = inflater.inflate(R.layout.dialog_student_picker_list_item, null);
                holder = new GroupViewHolder();
                holder.placeNameView = (TextView) convertView.findViewById(R.id.list_student_name);
                //holder.placeCountView = (TextView) convertView.findViewById(R.id.place_count);

                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }

            holder.placeNameView.setText(placeName);
            //holder.placeCountView.setText("");  We don't need the count of students here

            return convertView;
        }
    }

    private class ClassAdapter extends BaseAdapter {

        Map<String, List<Student>> classes;
        String[] classesList;

        ClassAdapter(List<Student> students) {
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
            classesList = classes.keySet().toArray(new String[classes.size()]);
        }

        @Override
        public int getCount() {
            return classesList.length;
        }

        @Override
        public Object getItem(int i) {
            return classesList[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            class GroupViewHolder {
                TextView classNameView;
                //TextView classCountView;

            }

            String className = classesList[position];
            GroupViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                convertView = inflater.inflate(R.layout.dialog_student_picker_list_item, null);
                holder = new GroupViewHolder();
                holder.classNameView = (TextView) convertView.findViewById(R.id.list_student_name);
                //holder.classCountView = (TextView) convertView.findViewById(R.id.class_count);

                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }

            holder.classNameView.setText(className);

            return convertView;
        }
    }
}
