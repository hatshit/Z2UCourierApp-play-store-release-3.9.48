package com.suggestprice_team.courier_team.teammember_bookings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.R;
import java.util.List;

/**
 * Created by Arun on 27-july-2018.
 */

public class TeamCategeoryAdapter  extends ExpandableRecyclerAdapter<TeamMemberCategeory_ViewHolder, BookingViewHolder> {

    private LayoutInflater mInflator;
    Context context;

    public TeamCategeoryAdapter(Context context, List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        this.context = context;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public TeamMemberCategeory_ViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View teammember_categeory_items = mInflator.inflate(R.layout.teammember_categeory_items, parentViewGroup, false);
        return new TeamMemberCategeory_ViewHolder(teammember_categeory_items);
    }

    @Override
    public BookingViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View bookingView = mInflator.inflate(R.layout.team_booking_items, childViewGroup, false);
        return new BookingViewHolder(bookingView);
    }

    @Override
    public void onBindParentViewHolder(TeamMemberCategeory_ViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        TeamCategeory teamCategory = (TeamCategeory) parentListItem;
        parentViewHolder.bind(teamCategory);
    }

    @Override
    public void onBindChildViewHolder(BookingViewHolder childViewHolder, int position, Object childListItem) {
        All_Bookings_DataModels bookingsModel = (All_Bookings_DataModels) childListItem;
        childViewHolder.bind(context, bookingsModel);
    }
}