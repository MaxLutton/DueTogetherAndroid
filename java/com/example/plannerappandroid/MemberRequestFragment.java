package com.example.plannerappandroid;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemberRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemberRequestFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "RequestList";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private List<Integer> mParam1;
    private String mParam2;

    public MemberRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MemberRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemberRequestFragment newInstance(List<Integer> param1) {
        MemberRequestFragment fragment = new MemberRequestFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList(ARG_PARAM1, (ArrayList<Integer>)param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getIntegerArrayList(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_member_request, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        String pendingMembers = "";
        for (int memberId : mParam1) {
            pendingMembers += memberId + "; ";
        }
        TextView temporaryText = getView().findViewById(R.id.fragment_temp_text);
        temporaryText.setText(pendingMembers);
    }

}