package nhsmedic.com.tcc.nhsappmedic.attendance.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import nhsmedic.com.tcc.nhsappmedic.R;


public class ItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.userImageImageView)
    ImageView userImageImageView;
    @BindView(R.id.userNameTextView)
    TextView userNameTextView;
    @BindView(R.id.dateDiagnosisTextView)
    TextView dateDiagnosisTextView;
    @BindView(R.id.attendanceButton)
    Button continueButton;

    public ItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);



    }

    public ImageView getUserImageImageView() {
        return userImageImageView;
    }

    public void setUserImageImageView(ImageView userImageImageView) {
        this.userImageImageView = userImageImageView;
    }

    public TextView getUserNameTextView() {
        return userNameTextView;
    }

    public void setUserNameTextView(TextView userNameTextView) {
        this.userNameTextView = userNameTextView;
    }

    public TextView getDateDiagnosisTextView() {
        return dateDiagnosisTextView;
    }

    public void setDateDiagnosisTextView(TextView dateDiagnosisTextView) {
        this.dateDiagnosisTextView = dateDiagnosisTextView;
    }

    public Button getContinueButton() {
        return continueButton;
    }

    public void setContinueButton(Button continueButton) {
        this.continueButton = continueButton;
    }
}