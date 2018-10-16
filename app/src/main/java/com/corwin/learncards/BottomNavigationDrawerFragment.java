package com.corwin.learncards;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;


public class BottomNavigationDrawerFragment extends BottomSheetDialogFragment {
    private Map<Integer, Integer> lessons = new HashMap<>();
    private boolean checkedItems[];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottomsheet, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        CardDataList cardDataList = activity.getCardsCollection();
        for (CardData card : cardDataList.getCards()) {
            addToLessonsList(card);
        }
        for (CardData card : cardDataList.getPhrases()) {
            addToLessonsList(card);
        }
        checkedItems = new boolean[lessons.size()];
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavigationView navigationView = getView().findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.choose_lesson:

                        List<String> items = new ArrayList<>();
                        String[] stringItems = new String[lessons.size()];
                        for (Integer lesson : lessons.keySet()) {
                            items.add(lesson.toString() + " - " + lessons.get(lesson) + " cards");
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Choose lessons");
                        builder.setMultiChoiceItems(items.toArray(stringItems), checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {

                            }
                        });


                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        builder.setNeutralButton("ClearAll", null);

                        builder.setCancelable(false);

                        final AlertDialog dialog = builder.create();
                        builder.setAdapter(new ArrayAdapter<CharSequence>(
                                getActivity(), 0, android.R.id.text1, stringItems) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                if (checkedItems != null) {
                                    ((ListView) parent).setItemChecked(position, checkedItems[position]);
                                }
                                return view;
                            }
                        }, null);

                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                                button.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        for (int i = 0; i < checkedItems.length; i++) {
                                            checkedItems[i] = false;
                                            dialog.getListView().setItemChecked(i, false);
                                        }
//                                        ((ArrayAdapter) dialog.getListView().getAdapter()).notifyDataSetInvalidated();
//                                        EditText latitude = (EditText) locationDialog.findViewById(R.id.dl_et_latitude);
//                                        EditText longitude = (EditText) locationDialog.findViewById(R.id.dl_et_longitude);
//                                        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//                                        try {
//                                            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                                            double currentLongitude = location.getLongitude();
//                                            double currentLatitude = location.getLatitude();
//                                            latitude.setText(Double.toString(currentLatitude));
//                                            longitude.setText(Double.toString(currentLongitude));
//                                            Log.d(TAG, "Latitude " + currentLatitude + "  Longitude " + currentLongitude);
//                                        } catch (SecurityException e){
//                                            Log.d(TAG, e.toString());
//                                        }
                                    }
                                });
                            }
                        });

                        dialog.show();


//                        LessonsListFragment lessonsListFragment = new LessonsListFragment();
//                        lessonsListFragment.show(getActivity().getSupportFragmentManager(), lessonsListFragment.getClass().getSimpleName());
                        dismiss();
                        return false;
                    default:
                        break;
                }
                return false;
            }
        });
    }


    private void addToLessonsList(CardData cardData) {
        Integer lesson = cardData.getLesson();
        if (!lessons.containsKey(lesson)) {
            lessons.put(cardData.getLesson(), 0);
        }
        lessons.put(lesson, lessons.get(lesson) + 1);
    }

}
