package nhsmedic.com.tcc.nhsappmedic.institution.util;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import nhsmedic.com.tcc.nhsappmedic.R;


public class ItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.institutionNameTextView)
    TextView institutionNameTextView;
    @BindView(R.id.addressTextView)
    TextView addressTextView;

    @BindView(R.id.selectHealthInstitutionButton)
    Button selectHealthInstitutionButton;

    @BindView(R.id.institutionImageImageView)
    ImageView institutionImageImageView;

    public ItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }


    public TextView getInstitutionNameTextView() {
        return institutionNameTextView;
    }

    public void setInstitutionNameTextView(TextView institutionNameTextView) {
        this.institutionNameTextView = institutionNameTextView;
    }

    public TextView getAddressTextView() {
        return addressTextView;
    }

    public void setAddressTextView(TextView addressTextView) {
        this.addressTextView = addressTextView;
    }

    public Button getSelectHealthInstitutionButton() {
        return selectHealthInstitutionButton;
    }

    public void setSelectHealthInstitutionButton(Button selectHealthInstitutionButton) {
        this.selectHealthInstitutionButton = selectHealthInstitutionButton;
    }

    public ImageView getInstitutionImageImageView() {
        return institutionImageImageView;
    }

    public void setInstitutionImageImageView(ImageView institutionImageImageView) {
        this.institutionImageImageView = institutionImageImageView;
    }
}
