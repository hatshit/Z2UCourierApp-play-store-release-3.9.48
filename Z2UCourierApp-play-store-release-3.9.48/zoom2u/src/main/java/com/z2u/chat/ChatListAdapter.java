package com.z2u.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.Query;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.z2u.chatview.ChatDetailActivity;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.slidemenu.offerrequesthandlr.TouchImageView;
import com.zoom2u.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.z2u.chatview.ChatViewBookingScreen.mFirebaseRef;

public class ChatListAdapter extends FirebaseListAdapter<Chat> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    Model_DeliveriesToChat modelOfDeliveryChat;
	String bidRequestNode;
	Activity activity;

	RelativeLayout chatViewFullImage;

	private MediaPlayer mPlayer;
	boolean isPlaying = false;

	//**************** Init ChatListAdapter for Booking and Admin chat ***************
    public ChatListAdapter(Query ref, Activity activity, int layout, String mUsername, Model_DeliveriesToChat modelOfDeliveryChat, RelativeLayout chatViewFullImage) {
        super(ref, Chat.class, layout, activity);
        this.modelOfDeliveryChat = modelOfDeliveryChat;
        this.activity = activity;
        this.chatViewFullImage = chatViewFullImage;
    }

	//**************** Init ChatListAdapter for Bid chat ***************
	public ChatListAdapter(Query ref, Activity activity, int layout, String bidRequestNode) {
		super(ref, Chat.class, layout, activity);
		this.bidRequestNode = bidRequestNode;
		this.activity = activity;
	}

    @Override
    protected void populateView(View view, final Chat chat, int i) {
        // Map a Chat object to an entry in our listview
		String author = chat.getSender();
		String messageType = chat.getType();

		//*********** Message layout **************
        RelativeLayout layoutChatListItem = (RelativeLayout) view.findViewById(R.id.chatItemLayout);
		layoutChatListItem.setVisibility(View.VISIBLE);

		//*********** Image layout **************
		RelativeLayout imageLayoutForChat = (RelativeLayout) view.findViewById(R.id.imageLayoutForChat);
		imageLayoutForChat.setVisibility(View.GONE);

		//*********** Audio layout **************
		RelativeLayout voiceMessageChatLayout = (RelativeLayout) view.findViewById(R.id.voiceMessageChatLayout);
		voiceMessageChatLayout.setVisibility(View.GONE);

		RelativeLayout.LayoutParams paramForMsgLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		TextView timeStampTxtInChat = null;
		ImageView readRecieptImg = null;
		final Chronometer chronometerTimer_VoiceMessageChat, chronometerTimer_VoiceMessageChatStartTime;
		TextView dashTxt_VoiceChat;

        // If the message was sent by this user, color it differently
        if(author != null && !author.equals("courier")) {

			paramForMsgLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			paramForMsgLayout.rightMargin = 25;

			try {
				if (messageType.equals("photo")) {

					imageLayoutForChat.setBackgroundResource(R.drawable.admin_chat);
					imageLayoutForChat.setLayoutParams(paramForMsgLayout);
					layoutChatListItem.setVisibility(View.GONE);
					imageLayoutForChat.setVisibility(View.VISIBLE);
					voiceMessageChatLayout.setVisibility(View.GONE);

					final ImageView chatImageView = (ImageView) view.findViewById(R.id.chatImageView);
					chatImageView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							zoomImageFromThumb(chatImageView, chat.getMessage());
						}
					});
					readRecieptImg = (ImageView) view.findViewById(R.id.readRecieptImgChat);
					timeStampTxtInChat = (TextView) view.findViewById(R.id.timeStampTxtInImgChat);

					Picasso.with(activity).load(chat.getThumbnail()).centerInside().placeholder(R.drawable.chat_placeholder).error(R.drawable.chat_placeholder) // will be displayed if the image cannot be loaded
							.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
							.networkPolicy(NetworkPolicy.NO_CACHE).noFade().into(chatImageView);

				} else if (messageType.equals("audio")) {

					voiceMessageChatLayout.setBackgroundResource(R.drawable.admin_chat);
					voiceMessageChatLayout.setLayoutParams(paramForMsgLayout);
					layoutChatListItem.setVisibility(View.GONE);
					imageLayoutForChat.setVisibility(View.GONE);
					voiceMessageChatLayout.setVisibility(View.VISIBLE);

					readRecieptImg = (ImageView) view.findViewById(R.id.readRecieptVoiceChat);
					timeStampTxtInChat = (TextView) view.findViewById(R.id.timeStampTxtInVoiceChat);

					voiceMessageChatLayout.setPadding(10, 10, 10, 0);

					voiceMessageChatLayout.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

							showAudioRecordingDialog(chat);
						}
					});

				} else {

					imageLayoutForChat.setVisibility(View.GONE);
					voiceMessageChatLayout.setVisibility(View.GONE);

					TextView messageText = (TextView) view.findViewById(R.id.messageTxtInChat);
					timeStampTxtInChat = (TextView) view.findViewById(R.id.timeStampTxtInChat);
					readRecieptImg = (ImageView) view.findViewById(R.id.readRecieptImg);

					textMessageViewLoadForAdminChat(layoutChatListItem, paramForMsgLayout, timeStampTxtInChat,
							messageText, view, chat);
				}
			} catch (Exception e) {
				e.printStackTrace();

				imageLayoutForChat.setVisibility(View.GONE);
				voiceMessageChatLayout.setVisibility(View.GONE);

				TextView messageText = (TextView) view.findViewById(R.id.messageTxtInChat);
				timeStampTxtInChat = (TextView) view.findViewById(R.id.timeStampTxtInChat);
				readRecieptImg = (ImageView) view.findViewById(R.id.readRecieptImg);

				textMessageViewLoadForAdminChat(layoutChatListItem, paramForMsgLayout, timeStampTxtInChat,
						messageText, view, chat);
			}

			try {
				if (chat.getUser() != null && !chat.getUser().equals("null") && !chat.getUser().equals("")) {
					if (modelOfDeliveryChat != null)
						timeStampTxtInChat.setText(chat.getUser() + "\n" + getTimeToLocalTimeStamp(chat.getTimestamp()));
					else
						timeStampTxtInChat.setText(getTimeToLocalTimeStamp(chat.getTimestamp()));
				}else
					timeStampTxtInChat.setText(getTimeToLocalTimeStamp(chat.getTimestamp()));
			} catch (Exception e) {
				e.printStackTrace();
				timeStampTxtInChat.setText(getTimeToLocalTimeStamp(chat.getTimestamp()));
			}
			timeStampTxtInChat.setTextColor(Color.parseColor("#1C7EBB"));
			timeStampTxtInChat.setPadding(5, 5, 5, 5);
			readRecieptImg.setVisibility(View.GONE);

        	if(chat.getReceipt() == 0) {
				HashMap<String, Object> hMap = new HashMap<String, Object>();
				hMap.put("message", chat.getMessage());
				hMap.put("receipt", 1);
				hMap.put("sender", chat.getSender());
				hMap.put("timestamp", chat.getTimestamp());
				hMap.put("user", chat.getUser());
				hMap.put("type", chat.getType());
				try {
					if (messageType.equals("photo"))
						hMap.put("thumbnail", chat.getThumbnail());
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (modelOfDeliveryChat != null) {
					if (modelOfDeliveryChat.getCustomer().equals("Zoom2u-Admin")) {
						mFirebaseRef.child(ChatDetailActivity.COURIER_ADMIN_MESSAGE_CHAT + modelOfDeliveryChat.getCourierId() + "/message/" + FirebaseListAdapter.mKeys.get(i)).updateChildren(hMap);
						setUnreadUrlToSetUnreadCount(ChatDetailActivity.COURIER_ADMIN_UNREADS + modelOfDeliveryChat.getCourierId() + "/status/admin/unread");
					} else {
						mFirebaseRef.child(ChatDetailActivity.CUSTOMER_COURIER_BOOKINGCHAT + modelOfDeliveryChat.getBookingId() + "_" + modelOfDeliveryChat.getCourierId()
								+ "/message/" + FirebaseListAdapter.mKeys.get(i)).updateChildren(hMap);
						setUnreadUrlToSetUnreadCount(ChatDetailActivity.CUSTOMER_COURIER_BOOKINGCHAT + modelOfDeliveryChat.getBookingId() + "_" +
								modelOfDeliveryChat.getCourierId() + "/status/customer/unread");
					}
				} else {
					mFirebaseRef.child(bidRequestNode+ "/message/" + FirebaseListAdapter.mKeys.get(i)).updateChildren(hMap);
					setUnreadUrlToSetUnreadCount(bidRequestNode+ "/status/customer/unread");
				}
			}

        } else {

			paramForMsgLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			paramForMsgLayout.leftMargin = 25;

			try {
				if (messageType.equals("photo")) {

					imageLayoutForChat.setBackgroundResource(R.drawable.courier_chat);
					imageLayoutForChat.setLayoutParams(paramForMsgLayout);
					layoutChatListItem.setVisibility(View.GONE);
					imageLayoutForChat.setVisibility(View.VISIBLE);
					voiceMessageChatLayout.setVisibility(View.GONE);

					final ImageView chatImageView = (ImageView) view.findViewById(R.id.chatImageView);
					chatImageView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							zoomImageFromThumb(chatImageView, chat.getMessage());
						}
					});
					readRecieptImg = (ImageView) view.findViewById(R.id.readRecieptImgChat);
					timeStampTxtInChat = (TextView) view.findViewById(R.id.timeStampTxtInImgChat);

					Picasso.with(activity).load(chat.getThumbnail()).into(chatImageView);

				} else if (messageType.equals("audio")) {

					voiceMessageChatLayout.setBackgroundResource(R.drawable.courier_chat);
					voiceMessageChatLayout.setLayoutParams(paramForMsgLayout);
					layoutChatListItem.setVisibility(View.GONE);
					imageLayoutForChat.setVisibility(View.GONE);
					voiceMessageChatLayout.setVisibility(View.VISIBLE);

					readRecieptImg = (ImageView) view.findViewById(R.id.readRecieptVoiceChat);
					timeStampTxtInChat = (TextView) view.findViewById(R.id.timeStampTxtInVoiceChat);

					paramForMsgLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					layoutChatListItem.setLayoutParams(paramForMsgLayout);
					paramForMsgLayout.leftMargin = 25;
					layoutChatListItem.setBackgroundResource(R.drawable.courier_chat);

					voiceMessageChatLayout.setPadding(10, 10, 10, 0);

                    voiceMessageChatLayout.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

                            showAudioRecordingDialog(chat);
						}
					});
				} else {

					imageLayoutForChat.setVisibility(View.GONE);
					voiceMessageChatLayout.setVisibility(View.GONE);

					TextView messageText = (TextView) view.findViewById(R.id.messageTxtInChat);
					timeStampTxtInChat = (TextView) view.findViewById(R.id.timeStampTxtInChat);
					readRecieptImg = (ImageView) view.findViewById(R.id.readRecieptImg);

					textMessageViewLoadForCourierChat (layoutChatListItem, paramForMsgLayout, timeStampTxtInChat,
							messageText, view, chat);
				}
			} catch (Exception e) {
				e.printStackTrace();

				imageLayoutForChat.setVisibility(View.GONE);
				voiceMessageChatLayout.setVisibility(View.GONE);

				TextView messageText = (TextView) view.findViewById(R.id.messageTxtInChat);
				timeStampTxtInChat = (TextView) view.findViewById(R.id.timeStampTxtInChat);
				readRecieptImg = (ImageView) view.findViewById(R.id.readRecieptImg);

				textMessageViewLoadForCourierChat(layoutChatListItem, paramForMsgLayout, timeStampTxtInChat,
						messageText, view, chat);
			}


			timeStampTxtInChat.setVisibility(View.VISIBLE);
			timeStampTxtInChat.setTextColor(Color.parseColor("#374350"));
			timeStampTxtInChat.setText(getTimeToLocalTimeStamp(chat.getTimestamp()));
			readRecieptImg.setVisibility(View.VISIBLE);
			if (chat.getReceipt() == 1)
				readRecieptImg.setImageResource(R.drawable.receipt_read);
			else
				readRecieptImg.setImageResource(R.drawable.receipt_unread);
        }
    }

	private void textMessageViewLoadForCourierChat(RelativeLayout layoutChatListItem, RelativeLayout.LayoutParams paramForMsgLayout,
												   TextView timeStampTxtInChat, TextView messageText, View view, Chat chat) {
		layoutChatListItem.setBackgroundResource(R.drawable.courier_chat);
		layoutChatListItem.setLayoutParams(paramForMsgLayout);
		layoutChatListItem.setVisibility(View.VISIBLE);

		try {
			messageText.setText(Html.fromHtml(chat.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		messageText.setTextSize(15f);
		messageText.setTextColor(Color.parseColor("#FFFFFF"));
	}

	private void textMessageViewLoadForAdminChat(RelativeLayout layoutChatListItem, RelativeLayout.LayoutParams paramForMsgLayout,
									 TextView timeStampTxtInChat, TextView messageText, View view, Chat chat) {
		layoutChatListItem.setBackgroundResource(R.drawable.admin_chat);
		layoutChatListItem.setLayoutParams(paramForMsgLayout);
		layoutChatListItem.setVisibility(View.VISIBLE);


		try {
			messageText.setText(Html.fromHtml(chat.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();
			messageText.setText("");
		}
		messageText.setTextSize(15f);

		timeStampTxtInChat.setVisibility(View.VISIBLE);
		messageText.setTextColor(Color.parseColor("#374350"));
    }

	//*********** Set unread message count to server when readed ***************//
	void setUnreadUrlToSetUnreadCount(String unreadUrl){
		ChatViewBookingScreen.mFirebaseRef.child(unreadUrl).setValue(0);
	}

	//************* Show converted time from server to chat list **************//
    @SuppressLint("SimpleDateFormat")
	String getTimeToLocalTimeStamp(String timeStamp){
    	String dateStr = "";
    	try {
			if(!timeStamp.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			    //getting GMT timezone, you can get any timezone e.g. UTC
			    converter.setTimeZone(TimeZone.getTimeZone("IST"));
			    Date convertedDate = new Date();
			    try {
			    	convertedDate = converter.parse(timeStamp);
				    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
				    dateStr = dateFormatter.format(convertedDate);
				    converter = null;
				    dateFormatter = null;
				    convertedDate = null;
				    
				    return dateStr;
			    } catch (Exception e) {
				//	e.printStackTrace();
				}
			}
		} catch (Exception e) {
		//	e.printStackTrace();
		}
    	return dateStr;
    }

	private Animator mCurrentAnimator;
	private int mShortAnimationDuration;

	TouchImageView expandedImageView;
	Rect startBounds;
	float startScaleFinal;
	View thumbView;

	/*****************
	 * Image Zoom effect
	 ***************/
	private void zoomImageFromThumb(final View thumbView, String imageUrl) {
		// If there's an animation in progress, cancel it
		// immediately and proceed with this one.
		this.thumbView = thumbView;
		if (mCurrentAnimator != null) {
			mCurrentAnimator.cancel();
		}

		chatViewFullImage.setVisibility(View.VISIBLE);

		if (expandedImageView != null)
			expandedImageView = null;
		expandedImageView = new TouchImageView(activity);
		expandedImageView.setBackgroundResource(R.drawable.splash);
		expandedImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		RelativeLayout.LayoutParams paramImg = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		paramImg.addRule(RelativeLayout.CENTER_HORIZONTAL);
		paramImg.addRule(RelativeLayout.CENTER_VERTICAL);
		expandedImageView.setLayoutParams(paramImg);
		chatViewFullImage.addView(expandedImageView);
		Picasso.with(activity).load(imageUrl).into(expandedImageView);

		// Calculate the starting and ending bounds for the zoomed-in image.
		// This step involves lots of math. Yay, math.
		startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();

		// The start bounds are the global visible rectangle of the thumbnail,
		// and the final bounds are the global visible rectangle of the container
		// view. Also set the container view's offset as the origin for the
		// bounds, since that's the origin for the positioning animation
		// properties (X, Y).
		thumbView.getGlobalVisibleRect(startBounds);
		chatViewFullImage.getGlobalVisibleRect(finalBounds, globalOffset);
		startBounds.offset(-globalOffset.x, -globalOffset.y);
		finalBounds.offset(-globalOffset.x, -globalOffset.y);
		// Adjust the start bounds to be the same aspect ratio as the final
		// bounds using the "center crop" technique. This prevents undesirable
		// stretching during the animation. Also calculate the start scaling
		// factor (the end scaling factor is always 1.0).
		float startScale;
		if ((float) finalBounds.width() / finalBounds.height()
				> (float) startBounds.width() / startBounds.height()) {
			// Extend start bounds horizontally
			startScale = (float) startBounds.height() / finalBounds.height();
			float startWidth = startScale * finalBounds.width();
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		} else {
			// Extend start bounds vertically
			if (finalBounds.width() > 0)
				startScale = (float) startBounds.width() / finalBounds.width();
			else
				startScale = (float) startBounds.width();
			float startHeight = startScale * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}

		// Hide the thumbnail and show the zoomed-in view. When the animation
		// begins, it will position the zoomed-in view in the place of the
		// thumbnail.
	//	thumbView.setAlpha(0f);
		expandedImageView.setVisibility(View.VISIBLE);

		// Set the pivot point for SCALE_X and SCALE_Y transformations
		// to the top-left corner of the zoomed-in view (the default
		// is the center of the view).
		expandedImageView.setPivotX(0f);
		expandedImageView.setPivotY(0f);

		// Construct and run the parallel animation of the four translation and
		// scale properties (X, Y, SCALE_X, and SCALE_Y).
		AnimatorSet set = new AnimatorSet();
		set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, (float) startBounds.left, (float) finalBounds.left))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.Y, (float) startBounds.top, (float) finalBounds.top))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
		set.setDuration(mShortAnimationDuration);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mCurrentAnimator = null;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				mCurrentAnimator = null;
			}
		});
		set.start();
		mCurrentAnimator = set;

		// Upon clicking the zoomed-in image, it should zoom back down
		// to the original bounds and show the thumbnail instead of
		// the expanded image.
		startScaleFinal = startScale;
		expandedImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				hideExpandedImageView();
			}
		});
	}

	void hideExpandedImageView(){
		if (mCurrentAnimator != null) {
			mCurrentAnimator.cancel();
		}
		// Animate the four positioning/sizing properties in parallel,
		// back to their original values.
		AnimatorSet set = new AnimatorSet();
		set.play(ObjectAnimator
				.ofFloat(expandedImageView, View.X, startBounds.left))
				.with(ObjectAnimator.ofFloat(expandedImageView,View.Y, startBounds.top))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
		set.setDuration(mShortAnimationDuration);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				thumbView.setAlpha(1f);
				chatViewFullImage.removeView(expandedImageView);
				chatViewFullImage.setVisibility(View.GONE);
				mCurrentAnimator = null;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				thumbView.setAlpha(1f);
				chatViewFullImage.removeView(expandedImageView);
				chatViewFullImage.setVisibility(View.GONE);
				mCurrentAnimator = null;
			}
		});
		set.start();
		mCurrentAnimator = set;
	}

	long timeWhenStopped = 0;
	Dialog audioPlayDialog;
    private void showAudioRecordingDialog(Chat chat) {

		try {
			if (audioPlayDialog != null)
				if(audioPlayDialog.isShowing())
					audioPlayDialog.dismiss();

			audioPlayDialog = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		audioPlayDialog = new Dialog(activity);
		audioPlayDialog.setCancelable(false);
		audioPlayDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#29000000")));
		audioPlayDialog.setContentView(R.layout.dialog_to_play_chat_audio);

        Window window = audioPlayDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		android.view.WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.CENTER;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);

        final ImageView playVoiceMessageChat_Dialog = (ImageView) audioPlayDialog.findViewById(R.id.playVoiceMessageChat_Dialog);
        final Chronometer chrono_VoiceMessageChatStartTime_Dialog = (Chronometer) audioPlayDialog.findViewById(R.id.chrono_VoiceMessageChatStartTime_Dialog);

        Chronometer chrono_VoiceMessageChat_Dialog = (Chronometer) audioPlayDialog.findViewById(R.id.chrono_VoiceMessageChat_Dialog);

        TextView dashTxt_VoiceChat_Dialog = (TextView) audioPlayDialog.findViewById(R.id.dashTxt_VoiceChat_Dialog);


        String audioUrl = chat.getMessage();
        // Initialize a new media player instance
        mPlayer = new MediaPlayer();

        // Set the media player audio stream type
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try{
            // Set the audio data source
            mPlayer.setDataSource(audioUrl);

            // Prepare the media player
            mPlayer.prepare();

            long mediaFileLengthInMilliseconds = mPlayer.getDuration();

            String getTimeOfAudio = getTimeForAudioToPlay(mediaFileLengthInMilliseconds);

            chrono_VoiceMessageChat_Dialog.setText(getTimeOfAudio);

			timeWhenStopped = 0;
			isPlaying = true;

			playVoiceMessageChat_Dialog.setImageResource(R.drawable.stopplayer);
			try {
				chrono_VoiceMessageChatStartTime_Dialog.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
				chrono_VoiceMessageChatStartTime_Dialog.start();
				mPlayer.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }catch (Exception e){
            // Catch the exception
            e.printStackTrace();
        }

		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
				isPlaying = !isPlaying;
				timeWhenStopped = 0;
				chrono_VoiceMessageChatStartTime_Dialog.setBase(SystemClock.elapsedRealtime());
				chrono_VoiceMessageChatStartTime_Dialog.stop();
				playVoiceMessageChat_Dialog.setImageResource(R.drawable.play_audio);
			}
		});

        ImageView close_audioDialogInChat = (ImageView)audioPlayDialog.findViewById(R.id.close_audioDialogInChat);
        close_audioDialogInChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				audioPlayDialog.dismiss();
				if (isPlaying)
					mPlayer.stop();
                isPlaying = false;
				mPlayer = null;
            }
        });

        playVoiceMessageChat_Dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    isPlaying = !isPlaying;
                    playVoiceMessageChat_Dialog.setImageResource(R.drawable.play_audio);
                    try {
						timeWhenStopped = chrono_VoiceMessageChatStartTime_Dialog.getBase() - SystemClock.elapsedRealtime();
                        chrono_VoiceMessageChatStartTime_Dialog.stop();
                        mPlayer.pause();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
					isPlaying = !isPlaying;
                    playVoiceMessageChat_Dialog.setImageResource(R.drawable.stopplayer);
                    try {
						chrono_VoiceMessageChatStartTime_Dialog.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                        chrono_VoiceMessageChatStartTime_Dialog.start();
                        mPlayer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

		audioPlayDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){
					if (isPlaying)
						mPlayer.stop();
                    isPlaying = false;
					mPlayer = null;

					audioPlayDialog.dismiss();

                    return true;
                }
                return false;
            }
        });

		audioPlayDialog.show();
    }

	private String getTimeForAudioToPlay(long mediaFileLengthInMilliseconds) {
		long minutes = TimeUnit.MILLISECONDS.toMinutes(mediaFileLengthInMilliseconds);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(mediaFileLengthInMilliseconds);

		String minsStr, secondStr;
		if (minutes < 10)
			minsStr = "0"+minutes;
		else
			minsStr = String.valueOf(minutes);

		if (seconds < 10)
			secondStr = "0"+seconds;
		else
			secondStr = String.valueOf(seconds);

		return minsStr+":"+secondStr;
	}

}
