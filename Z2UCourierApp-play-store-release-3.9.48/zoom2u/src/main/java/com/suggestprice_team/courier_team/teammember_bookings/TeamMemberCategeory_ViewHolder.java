package com.suggestprice_team.courier_team.teammember_bookings;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;

/**
 * Created by Arun on 27-july-2018.
 */

public class TeamMemberCategeory_ViewHolder  extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 90f;

    private final ImageView mArrowExpandImageView;
    private TextView mcourierNameTextView;

    public TeamMemberCategeory_ViewHolder(View itemView) {
        super(itemView);
        mcourierNameTextView = (TextView) itemView.findViewById(R.id.courierName_Categeory);
        mcourierNameTextView.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
        mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.iv_arrow_expand);
    }

    public void bind(TeamCategeory teamCategeory) {
        mcourierNameTextView.setText(teamCategeory.getCourierName());
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (expanded) {
            mArrowExpandImageView.setRotation(ROTATED_POSITION);
        } else {
            mArrowExpandImageView.setRotation(INITIAL_POSITION);
        }

    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);

        RotateAnimation rotateAnimation;
        if (expanded) { // rotate clockwise
            rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        } else { // rotate counterclockwise
            rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        }

        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);
        mArrowExpandImageView.startAnimation(rotateAnimation);

    }
}