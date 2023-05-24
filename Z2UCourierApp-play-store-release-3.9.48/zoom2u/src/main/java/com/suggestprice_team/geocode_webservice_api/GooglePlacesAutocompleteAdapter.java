package com.suggestprice_team.geocode_webservice_api;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

public class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
	
	private ArrayList<String> resultList;
	Activity activityCon;
	
	public GooglePlacesAutocompleteAdapter(Activity context, int textViewResourceId) {
		super(context, textViewResourceId);
		activityCon = context;
	}

	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public String getItem(int index) {
		return resultList.get(index);
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
						if (constraint != null) {
							// Retrieve the autocomplete results.
							resultList = GetAddressFromGoogleAPI.autocomplete(constraint.toString());
							// Assign the data to the FilterResults
							filterResults.values = resultList;
							filterResults.count = resultList.size();
						}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, final FilterResults results) {
					activityCon.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (results != null && results.count > 0) {
								notifyDataSetChanged();
							} else {
								notifyDataSetInvalidated();
							}
						}
					});
			}
		};
		return filter;
	}
}
