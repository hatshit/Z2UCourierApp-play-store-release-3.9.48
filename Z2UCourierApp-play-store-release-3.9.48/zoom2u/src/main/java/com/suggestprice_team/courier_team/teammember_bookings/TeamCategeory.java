package com.suggestprice_team.courier_team.teammember_bookings;

import com.zoom2u.datamodels.All_Bookings_DataModels;
import java.util.List;

/**
 * Created by Arun on 27-july-2018.
 */

public class TeamCategeory implements ParentListItem {

    private String courierName;
    private List<All_Bookings_DataModels> teamMemberBookingModel;

    public TeamCategeory (){}

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public List<All_Bookings_DataModels> getTeamMemberBookingModel() {
        return teamMemberBookingModel;
    }

    public void setTeamMemberBookingModel(List<All_Bookings_DataModels> teamMemberBookingModel) {
        this.teamMemberBookingModel = teamMemberBookingModel;
    }

    @Override
    public List<?> getChildItemList() {
        return teamMemberBookingModel;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
