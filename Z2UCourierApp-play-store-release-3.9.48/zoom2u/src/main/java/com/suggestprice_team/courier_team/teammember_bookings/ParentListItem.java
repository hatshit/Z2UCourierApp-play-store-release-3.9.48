package com.suggestprice_team.courier_team.teammember_bookings;

import java.util.List;

/**
 * Created by arun on 27/7/18.
 */

public interface ParentListItem {
    /**
     * Getter for the list of this parent list item's child list items.
     * <p>
     * If list is empty, the parent list item has no children.
     *
     * @return A {@link List} of the children of this {@link ParentListItem}
     */
    List<?> getChildItemList();

    /**
     * @return true if expanded, false if not
     */
    boolean isInitiallyExpanded();
}
