package com.z2u.chat;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class FirebaseListAdapter<T> extends BaseAdapter {
    private Query mRef;
    private Class<T> mModelClass;
    private int mLayout;
    private LayoutInflater mInflater;
    public List<T> mModels;
    public static List<String> mKeys = new ArrayList<String>();
    public static ChildEventListener mListener;

    public FirebaseListAdapter(Query mRef, Class<T> mModelClass, int mLayout, Activity activity) {
        this.mRef = mRef;
        this.mModelClass = mModelClass;
        this.mLayout = mLayout;
        mInflater = activity.getLayoutInflater();
        mModels = new ArrayList<T>();
        // Look for all child events. We will then map them to our own internal ArrayList, which backs ListView
        mListener = this.mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                try {
					T model = dataSnapshot.getValue(FirebaseListAdapter.this.mModelClass);
					String key = dataSnapshot.getKey();
					// Insert into the correct location, based on previousChildName
					if(!mKeys.contains(key)){
						if (previousChildName == null){
						    mModels.add(0, model);
						    mKeys.add(0, key);
						} else {
						    int previousIndex = mKeys.indexOf(previousChildName);
						    int nextIndex = previousIndex + 1;
						    if (nextIndex == mModels.size()){
						        mModels.add(model);
						        mKeys.add(key);
						    }else{
						        mModels.add(nextIndex, model);
							    mKeys.add(nextIndex, key);
							}
						}
					}
					notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // One of the mModels changed. Replace it in our list and name mapping
                try {
                    String key = dataSnapshot.getKey();
                    T newModel = dataSnapshot.getValue(FirebaseListAdapter.this.mModelClass);
                    int index = mKeys.indexOf(key);
                    mModels.set(index, newModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                try {
//                	String key = dataSnapshot.getKey();
//                	Log.e("", key+"   ============    ");
//					int index = mKeys.indexOf(key);
//					mKeys.remove(index);
//					mModels.remove(index);
				} catch (Exception e) {
					e.printStackTrace();
				}
                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                // A model changed position in the list. Update our list accordingly
                String key = dataSnapshot.getKey();
                T newModel = dataSnapshot.getValue(FirebaseListAdapter.this.mModelClass);
                int index = mKeys.indexOf(key);
                mModels.remove(index);
                mKeys.remove(index);
                if (previousChildName == null) {
                    mModels.add(0, newModel);
                    mKeys.add(0, key);
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mModels.size()) {
                        mModels.add(newModel);
                        mKeys.add(key);
                    } else {
                        mModels.add(nextIndex, newModel);
                        mKeys.add(nextIndex, key);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");
            }
        });
    }

    public void cleanup() {
        // We're being destroyed, let go of our mListener and forget about all of the mModels
        mRef.removeEventListener(mListener);
        mModels.clear();
        mKeys.clear();
    }

    @Override
    public int getCount() {
        return mModels.size();
    }

    @Override
    public Object getItem(int i) {
        return mModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(mLayout, viewGroup, false);
        }
        T model = mModels.get(i);
        // Call out to subclass to marshall this model into the provided view
        populateView(view, model, i);
        return view;
    }

    /**
     * Each time the data at the given Firebase location changes, this method will be called for each item that needs
     * to be displayed. The arguments correspond to the mLayout and mModelClass given to the constructor of this class.
     * <p/>
     * Your implementation should populate the view using the data contained in the model.
     *
     * @param v     The view to populate
     * @param model The object containing the data used to populate the view
     */
    protected abstract void populateView(View v, T model, int i);
    
    public void addItems(DataSnapshot dataSnapshot, String previousChildName){
    	 try {
				T model = dataSnapshot.getValue(FirebaseListAdapter.this.mModelClass);
				String key = dataSnapshot.getKey();
				// Insert into the correct location, based on previousChildName
				if(!mKeys.contains(key)){
					if (previousChildName == null){
					    mModels.add(0, model);
					    mKeys.add(0, key);
					} else {
					    int previousIndex = mKeys.indexOf(previousChildName);
					    int nextIndex = previousIndex + 1;
					    if (nextIndex == mModels.size()){
					        mModels.add(model);
					        mKeys.add(key);
					    }else{
					    	mModels.add(nextIndex, model);
						    mKeys.add(nextIndex, key);
						}
					}
				}
				notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	 }
    
    public String checkSenderIsAdmin(int position){
		 String senderIsAdminStr = "";
		 senderIsAdminStr = ((Chat)mModels.get(position)).getSender();
		 return senderIsAdminStr;
    }
     
}
