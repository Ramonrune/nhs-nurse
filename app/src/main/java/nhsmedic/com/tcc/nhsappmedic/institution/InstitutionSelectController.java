package nhsmedic.com.tcc.nhsappmedic.institution;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nhsmedic.com.tcc.nhsappmedic.R;
import nhsmedic.com.tcc.nhsappmedic.institution.adapter.InstitutionAdapter;
import nhsmedic.com.tcc.nhsappmedic.institution.model.HealthInstitutionModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class InstitutionSelectController extends Fragment {

    @BindView(R.id.healthInstitutionRecyclerView)
    RecyclerView healthInstitutionRecyclerView;

    @BindView(R.id.loadingLinearLayout)
    LinearLayout loadingLinearLayout;


    public InstitutionSelectController() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_institution_select, container, false);
        ButterKnife.bind(this, view);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        healthInstitutionRecyclerView.setLayoutManager(layout);


        healthInstitutionRecyclerView.setAdapter(new InstitutionAdapter(getActivity(), healthInstitutionModelList));
        healthInstitutionRecyclerView.setVisibility(View.VISIBLE);
        loadingLinearLayout.setVisibility(View.GONE);


        // Inflate the layout for this fragment
        return view;
    }

    private List<HealthInstitutionModel> healthInstitutionModelList;


    public void showHealthInstitutions(List<HealthInstitutionModel> healthInstitutionModelList) {
        this.healthInstitutionModelList = healthInstitutionModelList;




    }
}
