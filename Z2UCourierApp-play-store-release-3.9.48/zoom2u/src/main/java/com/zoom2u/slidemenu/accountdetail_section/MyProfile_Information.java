package com.zoom2u.slidemenu.accountdetail_section;

import static com.z2u.chatview.ChatViewBookingScreen.mFirebaseRef;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.squareup.picasso.Picasso;
import com.z2u.chatview.ChatDetailActivity;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.ActiveBookingDetail_New;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.UpdatePassword;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.roundedimage.RoundedImageView;
import com.zoom2u.slidemenu.AccountDetailFragment;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.webservice.WebserviceHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import me.pushy.sdk.lib.jackson.core.type.TypeReference;
import me.pushy.sdk.lib.jackson.databind.ObjectMapper;

public class MyProfile_Information extends Activity implements View.OnClickListener {

    ImageView backFromMyProfileInfo;
    TextView titleMyProfileInfo;
    ImageView chatIconMyProfileInfo;
    TextView countChatMyProfileInfo, bikeTxtAddMember, carTxtAddMember, vanTxtAddMember;


    RoundedImageView profileImg;

    TextView courierFirstLastName, userNameMyProfile, addressTitleMyProfile, companyNameTitleMyProfile, accountDetailTitleMyProfile, abnTitleMyProfile;

    EditText accountNameEdttxt, bankNameEdtTxt, bsbEdtTxt, accountNoEdtTxt, abnTextMyProfile, companyNameTextMyProfile;
    EditText addressTextMyProfile, preferedSuburbValueTxtMyProfile;
    Button saveDetail, updatePasswordBtn;

    boolean isCourierDetailSave = false;
    boolean isProfileDataUpdated = false;

    HashMap<String, Object> hashMapCourierProfileMainOBJ;

    ProgressDialog progressForGetCourierProfile;
    ProgressBar profileImageBar;
    String jsonResponseStr, vicheleSelection;
    Bitmap uploadProfileImageBitmap = null;
    Dialog selectProfileImgDialog;
    boolean isSwitchToUpdatePass = false;
    private JSONObject courierProfileMainResponseJOBJ;
    Window window;
    private RadioGroup rgInviteTeamMember;
    private RadioButton bikeVehicleButton, carVehicleButton, vanVehicleButton;
    TextView bioTitleMyProfile,acc_name,bank_name,bsb_name,acc_no,abn;
    TextView bioTxtMyProfile;

    @Override
    public void onResume() {
        super.onResume();
        isSwitchToUpdatePass = false;
        SlideMenuZoom2u.setCourierToOnlineForChat();
        SlideMenuZoom2u.countChatBookingView = countChatMyProfileInfo;
        Model_DeliveriesToChat.showExclamationForUnreadChat(countChatMyProfileInfo);
    }

    @Override
    public void onBackPressed() {
        backFromMyInfoScreen();
    }

    private void backFromMyInfoScreen() {
        if (isProfileDataUpdated) {
            AccountDetailFragment.OPEN_YOUR_INFO_PAGE = 604;
            Intent returnUpdateddata = new Intent();
            returnUpdateddata.putExtra("ProfileDictionary", hashMapCourierProfileMainOBJ);
            setResult(Activity.RESULT_OK, returnUpdateddata);
        } else
            AccountDetailFragment.OPEN_YOUR_INFO_PAGE = 605;
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_accountdetail);


        RelativeLayout headerSummaryReportLayout = findViewById(R.id.headerMyProfileInfoLayout);
        window = MyProfile_Information.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if (MainActivity.isIsBackGroundGray()) {
            headerSummaryReportLayout.setBackgroundResource(R.color.base_color_gray);
            window.setStatusBarColor(Color.parseColor("#374350"));
        } else {
            headerSummaryReportLayout.setBackgroundResource(R.color.base_color1);
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
        }
        isCourierDetailSave = false;

        try {
            bikeTxtAddMember = (TextView) findViewById(R.id.bikeTxtAddMember);

            carTxtAddMember = (TextView) findViewById(R.id.carTxtAddMember);

            vanTxtAddMember = (TextView) findViewById(R.id.vanTxtAddMember);
            hashMapCourierProfileMainOBJ = (HashMap<String, Object>) getIntent().getSerializableExtra("ProfileDictionary");
            rgInviteTeamMember = (RadioGroup) findViewById(R.id.rgInviteTeamMember);
            bikeVehicleButton = (RadioButton) rgInviteTeamMember.findViewById(R.id.rbForBike);
            carVehicleButton = (RadioButton) rgInviteTeamMember.findViewById(R.id.rbForCar);
            vanVehicleButton = (RadioButton) rgInviteTeamMember.findViewById(R.id.rbForVan);
            bikeVehicleButton.setOnClickListener(this);
            carVehicleButton.setOnClickListener(this);
            vanVehicleButton.setOnClickListener(this);
            backFromMyProfileInfo = (ImageView) findViewById(R.id.backFromMyProfileInfo);
            backFromMyProfileInfo.setOnClickListener(this);

            titleMyProfileInfo = (TextView) findViewById(R.id.titleMyProfileInfo);


            chatIconMyProfileInfo = (ImageView) findViewById(R.id.chatIconMyProfileInfo);
            chatIconMyProfileInfo.setOnClickListener(this);

            countChatMyProfileInfo = (TextView) findViewById(R.id.countChatMyProfileInfo);

            SlideMenuZoom2u.countChatBookingView = countChatMyProfileInfo;
            Model_DeliveriesToChat.showExclamationForUnreadChat(countChatMyProfileInfo);

            if (profileImageBar == null)
                profileImageBar = (ProgressBar) findViewById(R.id.profileImageBar);
            profileImageBar.setVisibility(View.GONE);
            if (profileImg == null)
                profileImg = (RoundedImageView) findViewById(R.id.myProfileImg);
            if (userNameMyProfile == null)
                userNameMyProfile = (TextView) findViewById(R.id.userNameMyProfile);
            courierFirstLastName = (TextView) findViewById(R.id.courierFirstLastName);
            if (addressTitleMyProfile == null)
                addressTitleMyProfile = (TextView) findViewById(R.id.addressTitleMyProfile);
            if (addressTextMyProfile == null)
                addressTextMyProfile = (EditText) findViewById(R.id.addressTextMyProfile);

            findViewById(R.id.bioTitleMyProfile);
            if (bioTxtMyProfile == null)
                bioTxtMyProfile = (TextView) findViewById(R.id.bioTextMyProfile);
            if (companyNameTitleMyProfile == null)
                companyNameTitleMyProfile = (TextView) findViewById(R.id.companyNameTitleMyProfile);
            if (companyNameTextMyProfile == null)
                companyNameTextMyProfile = (EditText) findViewById(R.id.companyNameTextMyProfile);
            if (accountDetailTitleMyProfile == null)
                accountDetailTitleMyProfile = (TextView) findViewById(R.id.accountDetailTitleMyProfile);
            if (abnTitleMyProfile == null)
                abnTitleMyProfile = (TextView) findViewById(R.id.abnTitleMyProfile);
            if (accountNameEdttxt == null)
                accountNameEdttxt = (EditText) findViewById(R.id.accountNameEdttxt);
            if (bankNameEdtTxt == null)
                bankNameEdtTxt = (EditText) findViewById(R.id.bankNameEdtTxt);
            if (bsbEdtTxt == null)
                bsbEdtTxt = (EditText) findViewById(R.id.bsbEdtTxt);
            if (accountNoEdtTxt == null)
                accountNoEdtTxt = (EditText) findViewById(R.id.accountNoEdtTxt);
            if (abnTextMyProfile == null)
                abnTextMyProfile = (EditText) findViewById(R.id.abnTextMyProfile);
            bioTitleMyProfile = findViewById(R.id.bioTitleMyProfile);
            acc_name =findViewById(R.id.acc_name);
            bank_name=findViewById(R.id.bank_name);
            bsb_name=findViewById(R.id.bsb_name);
            acc_no=findViewById(R.id.acc_no);
            abn=findViewById(R.id.abn);
            setEdtFieldBGtoGray(addressTextMyProfile, addressTitleMyProfile);
            //setEdtFieldBGtoGray(bioTxtMyProfile, bioTitleMyProfile);
            setEdtFieldBGtoGray(companyNameTextMyProfile, companyNameTitleMyProfile);
            setEdtFieldBGtoGray(accountNameEdttxt, acc_name);
            setEdtFieldBGtoGray(bankNameEdtTxt,bank_name);
            setEdtFieldBGtoGray(bsbEdtTxt, bsb_name);
            setEdtFieldBGtoGray(accountNoEdtTxt, acc_no);
            setEdtFieldBGtoGray(abnTextMyProfile, abn);


           findViewById(R.id.bio_ll).setOnClickListener(this);

            if (saveDetail == null)
                saveDetail = (Button) findViewById(R.id.saveDetail);
            if (updatePasswordBtn == null)
                updatePasswordBtn = (Button) findViewById(R.id.updatePasswordBtn);


            setYourInfoContentsToUI();     //******** Set your info UI contents

            profileImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        try {
                           if((int) Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.TIRAMISU){
                               String[] permission = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
                               if (ContextCompat.checkSelfPermission(MyProfile_Information.this, permission[0]) == PackageManager.PERMISSION_DENIED ||
                                       ContextCompat.checkSelfPermission(MyProfile_Information.this, permission[1]) == PackageManager.PERMISSION_DENIED) {

                                   Dialog enterFieldDialog  = new Dialog(MyProfile_Information.this);

                                   enterFieldDialog.setCancelable(true);
                                   enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                   enterFieldDialog.setContentView(R.layout.permission_dailog);

                                   Window window = enterFieldDialog.getWindow();
                                   window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                   WindowManager.LayoutParams wlp = window.getAttributes();

                                   wlp.gravity = Gravity.CENTER;
                                   wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                                   window.setAttributes(wlp);
                                   TextView enterFieldDialogHEader = (TextView) enterFieldDialog.findViewById(R.id.titleDialog);

                                   enterFieldDialogHEader.setText("Permission required!");

                                   TextView enterFieldDialogMsg = (TextView) enterFieldDialog.findViewById(R.id.dialogMessageText);

                                   enterFieldDialogMsg.setText("Z2U for couriers app need to access your images for picture post.");

                                   Button enterFieldDialogDoneBtn = (Button) enterFieldDialog.findViewById(R.id.dialogDoneBtn);

                                   enterFieldDialogDoneBtn.setText("Got it!");

                                   enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           ActivityCompat.requestPermissions(MyProfile_Information.this,
                                                   new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA}, 1);
                                           enterFieldDialog.dismiss();
                                       }
                                   });
                                   enterFieldDialog.show();

                               } else
                                   selectImage();
                           }
                           else if((int) Build.VERSION.SDK_INT<android.os.Build.VERSION_CODES.TIRAMISU){
                                String[] permission = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                                if (ContextCompat.checkSelfPermission(MyProfile_Information.this, permission[0]) == PackageManager.PERMISSION_DENIED ||
                                        ContextCompat.checkSelfPermission(MyProfile_Information.this, permission[1]) == PackageManager.PERMISSION_DENIED ||
                                        ContextCompat.checkSelfPermission(MyProfile_Information.this, permission[2]) == PackageManager.PERMISSION_DENIED) {

                                    Dialog enterFieldDialog  = new Dialog(MyProfile_Information.this);

                                    enterFieldDialog.setCancelable(true);
                                    enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    enterFieldDialog.setContentView(R.layout.permission_dailog);

                                    Window window = enterFieldDialog.getWindow();
                                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                    WindowManager.LayoutParams wlp = window.getAttributes();

                                    wlp.gravity = Gravity.CENTER;
                                    wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                                    window.setAttributes(wlp);
                                    TextView enterFieldDialogHEader = (TextView) enterFieldDialog.findViewById(R.id.titleDialog);

                                    enterFieldDialogHEader.setText("Permission required!");

                                    TextView enterFieldDialogMsg = (TextView) enterFieldDialog.findViewById(R.id.dialogMessageText);

                                    enterFieldDialogMsg.setText("Z2U for couriers app need to access your images for picture post.");

                                    Button enterFieldDialogDoneBtn = (Button) enterFieldDialog.findViewById(R.id.dialogDoneBtn);

                                    enterFieldDialogDoneBtn.setText("Got it!");

                                    enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ActivityCompat.requestPermissions(MyProfile_Information.this,
                                                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                                            enterFieldDialog.dismiss();
                                        }
                                    });
                                    enterFieldDialog.show();

                                } else
                                    selectImage();
                            }
                           else
                                selectImage();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            updatePasswordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        isSwitchToUpdatePass = true;
                        Intent loginPage = new Intent(MyProfile_Information.this, UpdatePassword.class);
                        startActivity(loginPage);
                        loginPage = null;
                        MyProfile_Information.this.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        isSwitchToUpdatePass = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            saveDetail.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("unchecked")
                @Override
                public void onClick(View v) {
                    try {
                        //  ***  Update Courier detail data  ***
                        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()) {
                            ((HashMap<String, Object>) hashMapCourierProfileMainOBJ.get("Address")).put("Street1", addressTextMyProfile.getText().toString());
                            hashMapCourierProfileMainOBJ.put("Vehicle", vicheleSelection);
                            hashMapCourierProfileMainOBJ.put("AccountName", accountNameEdttxt.getText().toString());
                            hashMapCourierProfileMainOBJ.put("BankName", bankNameEdtTxt.getText().toString());
                            hashMapCourierProfileMainOBJ.put("Bsb", bsbEdtTxt.getText().toString());
                            hashMapCourierProfileMainOBJ.put("AccountNumber", accountNoEdtTxt.getText().toString());
                            hashMapCourierProfileMainOBJ.put("ABN", abnTextMyProfile.getText().toString());
                            hashMapCourierProfileMainOBJ.put("CompanyName", companyNameTextMyProfile.getText().toString());

                            hashMapCourierProfileMainOBJ.put("Bio", bioTxtMyProfile.getText().toString());

                            if (hashMapCourierProfileMainOBJ.get("AccountName").equals("") && hashMapCourierProfileMainOBJ.get("BankName").equals("")
                                    && hashMapCourierProfileMainOBJ.get("Bsb").equals("") && hashMapCourierProfileMainOBJ.get("AccountNumber").equals("")
                                    && hashMapCourierProfileMainOBJ.get("ABN").equals("") && hashMapCourierProfileMainOBJ.get("CompanyName").equals("")
                            ) {
                                DialogActivity.alertDialogView(MyProfile_Information.this, "Information!", "Please enter field first!");
                            } else {
                                LoginZoomToU.imm.hideSoftInputFromWindow(accountNameEdttxt.getWindowToken(), 0);
                                isCourierDetailSave = true;
                                isProfileDataUpdated = true;
                                callProfileDetailTask();
                            }
                        } else
                            DialogActivity.alertDialogView(MyProfile_Information.this, "No Network!", "No Network connection, Please try again later.");

                        LoginZoomToU.imm.hideSoftInputFromWindow(accountNameEdttxt.getWindowToken(), 0);

                    } catch (Exception e) {
                        e.printStackTrace();
                        DialogActivity.alertDialogView(MyProfile_Information.this, "Error!", "Something went wrong, Please try again later.");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEdtFieldBGtoGray(final EditText edtFieldAddEditMember, final TextView txtFieldAddEditMember) {
        edtFieldAddEditMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    edtFieldAddEditMember.setBackgroundResource(R.drawable.bg_transparent_bottom_graydark);

                    txtFieldAddEditMember.setVisibility(View.INVISIBLE);
                } else {
                    edtFieldAddEditMember.setBackgroundResource(R.drawable.bg_transparent_bottom_blue);
                    txtFieldAddEditMember.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void setVehicle(String vicheleSelection) {
        switch (vicheleSelection) {
            case "Car":
                carSelection();
                carVehicleButton.setChecked(true);
                break;
            case "Bike":
                bikeSelection();
                bikeVehicleButton.setChecked(true);
                break;
            case "Van":
                vanSelection();
                vanVehicleButton.setChecked(true);
                break;
            default:
                carSelection();
                carVehicleButton.setChecked(true);
                break;
        }
    }

    private void vanSelection() {
        vicheleSelection = "Van";
        bikeTxtAddMember.setTextColor(getResources().getColor(R.color.gray_dark));
        carTxtAddMember.setTextColor(getResources().getColor(R.color.gray_dark));
        vanTxtAddMember.setTextColor(getResources().getColor(R.color.loginbtn_blue));
    }

    private void carSelection() {
        vicheleSelection = "Car";
        bikeTxtAddMember.setTextColor(getResources().getColor(R.color.gray_dark));
        carTxtAddMember.setTextColor(getResources().getColor(R.color.loginbtn_blue));
        vanTxtAddMember.setTextColor(getResources().getColor(R.color.gray_dark));
    }

    private void bikeSelection() {
        vicheleSelection = "Bike";
        bikeTxtAddMember.setTextColor(getResources().getColor(R.color.loginbtn_blue));
        carTxtAddMember.setTextColor(getResources().getColor(R.color.gray_dark));
        vanTxtAddMember.setTextColor(getResources().getColor(R.color.gray_dark));
    }

    //*************   Pop-up window for select profile image  **************//
    private void selectImage() {
        if (selectProfileImgDialog != null)
            selectProfileImgDialog = null;
        selectProfileImgDialog = new Dialog(MyProfile_Information.this, R.style.OpenDialogSlideAnim);
        selectProfileImgDialog.setCancelable(false);
        selectProfileImgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        selectProfileImgDialog.setContentView(R.layout.select_profileimg_dialog);

        Window window = selectProfileImgDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        android.view.WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER | Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        Button takePicBtn = (Button) selectProfileImgDialog.findViewById(R.id.takePicBtn);
        Button galleryBtn = (Button) selectProfileImgDialog.findViewById(R.id.galleryBtn);
        Button cancelDialogBtn = (Button) selectProfileImgDialog.findViewById(R.id.cancelDialogBtn);

        selectProfileImgDialog.findViewById(R.id.rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfileImgDialog.cancel();
            }
        });

        takePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /******************** Old running *************/
//				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//				intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
//				try {
//					intent.putExtra("return-data", true);
//					startActivityForResult(intent, PICK_FROM_CAMERA);
//					selectProfileImgDialog.dismiss();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
                /*****************  Old running  ***************/
                /***************** New implemented *************/
                try {
                    PushReceiver.isCameraOpen = true;
                    selectProfileImgDialog.dismiss();
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(MyProfile_Information.this.getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = LoginZoomToU.checkInternetwithfunctionality.createImageFile();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(MyProfile_Information.this,
                                    MyProfile_Information.this.getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, ActiveBookingDetail_New.TAKE_PHOTO);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfileImgDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                try {
                    intent.putExtra("return-data", true);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), AccountDetailFragment.PICK_FROM_GALLERY);
                    selectProfileImgDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        cancelDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfileImgDialog.dismiss();
            }
        });
        selectProfileImgDialog.show();
    }

    //*********  Getting camera/gallery images		********//
    @SuppressWarnings({"static-access", "deprecation"})
    public void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        try {
            PushReceiver.isCameraOpen = false;
            if (requestCode == AccountDetailFragment.PICK_FROM_CAMERA) {
                /*************************  2nd from android developer ******************/
                // Get the dimensions of the bitmap
                try {
                    if (resultCode == RESULT_OK) {
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        bmOptions.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(Functional_Utility.mCurrentPhotoPath, bmOptions);
                        int photoW = bmOptions.outWidth;
                        int photoH = bmOptions.outHeight;
                        // Determine how much to scale down the image
                        int scaleFactor = Math.min(photoW / LoginZoomToU.width, photoH / LoginZoomToU.height);
                        // Decode the image file into a Bitmap sized to fill the View
                        bmOptions.inJustDecodeBounds = false;
                        bmOptions.inSampleSize = scaleFactor;
                        bmOptions.inPurgeable = true;

                        uploadProfileImageBitmap = BitmapFactory.decodeFile(Functional_Utility.mCurrentPhotoPath, bmOptions);
                        //			uploadProfileImageBitmap = Functional_Utility.getRotatedCameraImg(Functional_Utility.mCurrentPhotoPath, uploadProfileImageBitmap);
                        profileImg.setImageBitmap(uploadProfileImageBitmap);
                        courierFirstLastName.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == AccountDetailFragment.PICK_FROM_GALLERY) {
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        uploadProfileImageBitmap = BitmapFactory.decodeFile(filePath);
                        profileImg.setImageBitmap(uploadProfileImageBitmap);
                        courierFirstLastName.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bio_ll:
                addBioAlert();
                break;
            case R.id.backFromMyProfileInfo:
                backFromMyInfoScreen();
                break;
            case R.id.chatIconMyProfileInfo:
                Intent chatViewIntent = new Intent(MyProfile_Information.this, ChatViewBookingScreen.class);
                startActivity(chatViewIntent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                chatViewIntent = null;
                break;
            case R.id.rbForBike:
                bikeSelection();
                break;
            case R.id.rbForCar:
                carSelection();
                break;
            case R.id.rbForVan:
                vanSelection();
                break;
        }
    }

    //************* Call get profile detail async task ***************
    private void callProfileDetailTask(){
        if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            GetCourierProfileAsyncTask();
            //new GetCourierProfileAsyncTask().execute();
        else
            DialogActivity.alertDialogView(MyProfile_Information.this, "No Network!", "No Network connection, Please try again later.");
    }

    Dialog addBioAlert;

    //************ Add Bio Alert **************
    private void addBioAlert() {
        try {
            if (addBioAlert != null)
                if (addBioAlert.isShowing())
                    addBioAlert.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (addBioAlert != null)
                addBioAlert = null;
            addBioAlert = new Dialog(MyProfile_Information.this);
            addBioAlert.setCancelable(true);
            addBioAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            addBioAlert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            addBioAlert.setContentView(R.layout.add_bio_profileview);

            Window window = addBioAlert.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            android.view.WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            addBioAlert.findViewById(R.id.titleBookingDetail);

            final EditText editTextAddBio = (EditText) addBioAlert.findViewById(R.id.editTextAddBio);

            editTextAddBio.setText("");
            if (!hashMapCourierProfileMainOBJ.get("Bio").equals("") && !hashMapCourierProfileMainOBJ.get("Bio").equals("null"))
                editTextAddBio.append(hashMapCourierProfileMainOBJ.get("Bio") + "");

            TextView saveBtnAddBio = (TextView) addBioAlert.findViewById(R.id.saveBtnAddBio);
            RelativeLayout relativeLayout4=addBioAlert.findViewById(R.id.relativeLayout4);
            if (MainActivity.isIsBackGroundGray()) {
                relativeLayout4.setBackgroundResource(R.color.base_color_gray);

            } else {
                relativeLayout4.setBackgroundResource(R.color.base_color1);

            }


            saveBtnAddBio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editTextAddBio.getText().toString().equals(""))
                        DialogActivity.alertDialogView(MyProfile_Information.this, "Alert!", "Please enter bio");
                    else{
                        if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                            AsyncToUpdateBio(editTextAddBio.getText().toString());
                            // new AsyncToUpdateBio(editTextAddBio.getText().toString()).execute();
                        else
                            DialogActivity.alertDialogView(MyProfile_Information.this, "No Network!", "No Network connection, Please try again later.");
                    }
                }
            });

            ImageView cancelBtnAddBio = (ImageView) addBioAlert.findViewById(R.id.cancelBtnAddBio);

            cancelBtnAddBio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBioAlert.dismiss();
                }
            });
            addBioAlert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void AsyncToUpdateBio(String bioTxt){
        final String[] responseBioUpdateStr = {""};
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressForGetCourierProfile != null)
                        if (progressForGetCourierProfile.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressForGetCourierProfile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progressForGetCourierProfile != null)
                    progressForGetCourierProfile = null;
                progressForGetCourierProfile = new ProgressDialog(MyProfile_Information.this);
                Custom_ProgressDialogBar.inItProgressBar(progressForGetCourierProfile);
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    JSONObject jObjOfBioStr = new JSONObject();
                    jObjOfBioStr.put("bio", bioTxt);
                    responseBioUpdateStr[0] = webServiceHandler.updateBioToServer(jObjOfBioStr.toString());
                    jObjOfBioStr = null;
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    responseBioUpdateStr[0] = "";
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (progressForGetCourierProfile != null)
                        if (progressForGetCourierProfile.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressForGetCourierProfile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (new JSONObject(responseBioUpdateStr[0]).getBoolean("success")){
                        addBioAlert.dismiss();
                        isCourierDetailSave = false;
                        isProfileDataUpdated = true;
                        hashMapCourierProfileMainOBJ.put("Bio", bioTxt);
                        addBioToBioField();
                    }else
                        DialogActivity.alertDialogView(MyProfile_Information.this, "Sorry!", "Bio not updated, Please try again");
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogView(MyProfile_Information.this, "Error!", "Something went wrong, Please try again later");
                }
            }
        }.execute();

    }


    private void BackgroundTaskToSetProfileImage(){

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    profileImageBar.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    if(!hashMapCourierProfileMainOBJ.get("Photo").equals("")) {
                        uploadProfileImageBitmap = BitmapFactory.decodeStream((InputStream) new URL("" + hashMapCourierProfileMainOBJ.get("Photo")).getContent());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if(uploadProfileImageBitmap != null){
                        profileImg.setImageBitmap(uploadProfileImageBitmap);
                        courierFirstLastName.setVisibility(View.GONE);
                    }else
                        courierFirstLastName.setVisibility(View.VISIBLE);

                    profileImageBar.setVisibility(View.GONE);

                    if(isCourierDetailSave == true)
                        //DialogActivity.alertDialogView(MyProfile_Information.this, "Successfuly Updated!", "Profile detail updated successfuly.");
                        Toast.makeText(MyProfile_Information.this, "Details updated!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    profileImageBar.setVisibility(View.GONE);
                    if(isCourierDetailSave == true)
                        //DialogActivity.alertDialogView(MyProfile_Information.this, "Successfuly Updated!", "Profile detail updated successfuly.");
                        Toast.makeText(MyProfile_Information.this, "Details updated!", Toast.LENGTH_LONG).show();

                }
            }
        }.execute();
    }


    private void GetCourierProfileAsyncTask(){

        final String[] courierProfileResponseStr = {"0"};
        final JSONArray[] responseArrayOfcourierProfileData = {null};
        final String[] uploadImageResponseStr = {"0"};
        final String[] imageUploadedName = {null};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if(progressForGetCourierProfile == null)
                    progressForGetCourierProfile = new ProgressDialog(MyProfile_Information.this);
                Custom_ProgressDialogBar.inItProgressBar(progressForGetCourierProfile);

                LoginZoomToU.imm.hideSoftInputFromWindow(accountNameEdttxt.getWindowToken(), 0);
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    try {
                        if(uploadProfileImageBitmap != null){
                            uploadImageResponseStr[0] = webServiceHandler.uploadProfileImage(uploadProfileImageBitmap, "Photo.png", false);
                            uploadProfileImageBitmap = null;
                            JSONObject jObjOfProfileImg = new JSONObject(uploadImageResponseStr[0]);
                            if(jObjOfProfileImg.getBoolean("success")) {
                                JSONArray courierProfileImageResponseJArray = jObjOfProfileImg.getJSONArray("image");
                                imageUploadedName[0] = courierProfileImageResponseJArray.getString(0);
                                hashMapCourierProfileMainOBJ.put("Photo", imageUploadedName[0]);
                                courierProfileImageResponseJArray = null;
                            }
                            jObjOfProfileImg = null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        // convert map to JSON string
                        jsonResponseStr = mapper.writeValueAsString(hashMapCourierProfileMainOBJ);
                        jsonResponseStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(hashMapCourierProfileMainOBJ);
                        mapper = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    courierProfileResponseStr[0] = webServiceHandler.updateCourierProfileDetailsWithDataArray(jsonResponseStr);

                    jsonResponseStr = "";
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    try {
                        courierProfileMainResponseJOBJ = new JSONObject(courierProfileResponseStr[0]);

                        updateCourierProfileToFirebase ();

                        if(hashMapCourierProfileMainOBJ != null)
                            hashMapCourierProfileMainOBJ = null;
                        hashMapCourierProfileMainOBJ = new HashMap<String, Object>();

                        ObjectMapper mapper = new ObjectMapper();
                        String jObjStr = courierProfileMainResponseJOBJ.toString();
                        if (!isCourierDetailSave) {
                            HashMap<String, Object> tempHashMapCourierProfileMainOBJ = mapper.readValue(jObjStr, new TypeReference<HashMap<String, Object>>(){});
                            hashMapCourierProfileMainOBJ = ((HashMap<String, Object>) tempHashMapCourierProfileMainOBJ.get("courier"));
                            tempHashMapCourierProfileMainOBJ = null;
                        } else
                            hashMapCourierProfileMainOBJ = mapper.readValue(jObjStr, new TypeReference<HashMap<String, Object>>(){});

                        mapper = null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    setYourInfoContentsToUI ();     //******** Set your info UI contents

                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogView(MyProfile_Information.this, "Server Error!", "Please try later.");
                    courierProfileResponseStr[0] = null;
                    responseArrayOfcourierProfileData[0] = null;
                }

                if(progressForGetCourierProfile != null)
                    if(progressForGetCourierProfile.isShowing())
                        Custom_ProgressDialogBar.dismissProgressBar(progressForGetCourierProfile);
            }
        }.execute();
    }


    //*********** Update courier profile details to Firebase DB ***********
    private void updateCourierProfileToFirebase() {
        try {
            HashMap<String, Object> hMap = new HashMap<String, Object>();
            hMap.put("courierId", courierProfileMainResponseJOBJ.getString("CourierId"));
            hMap.put("mobile", courierProfileMainResponseJOBJ.getString("Mobile"));
            hMap.put("name", courierProfileMainResponseJOBJ.getString("FirstName") + " "
                    + courierProfileMainResponseJOBJ.getString("LastName"));
            hMap.put("photo", courierProfileMainResponseJOBJ.getString("Photo"));

            mFirebaseRef.child(ChatDetailActivity.COURIER_ADMIN_UNREADS + courierProfileMainResponseJOBJ.getString("CourierId")).updateChildren(hMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setYourInfoContentsToUI() {
        try {
            if (hashMapCourierProfileMainOBJ.size() > 0) {
                vicheleSelection = (String) hashMapCourierProfileMainOBJ.get("Vehicle");

                setVehicle(vicheleSelection);


                userNameMyProfile.setText(hashMapCourierProfileMainOBJ.get("FirstName") + " " +
                        hashMapCourierProfileMainOBJ.get("LastName"));
                companyNameTextMyProfile.setText(hashMapCourierProfileMainOBJ.get("CompanyName") + "");
                accountNameEdttxt.setText(hashMapCourierProfileMainOBJ.get("AccountName") + "");
                bankNameEdtTxt.setText(hashMapCourierProfileMainOBJ.get("BankName") + "");
                bsbEdtTxt.setText(hashMapCourierProfileMainOBJ.get("Bsb") + "");
                accountNoEdtTxt.setText(hashMapCourierProfileMainOBJ.get("AccountNumber") + "");
                abnTextMyProfile.setText(hashMapCourierProfileMainOBJ.get("ABN") + "");

              addBioToBioField();

                addressTextMyProfile.setText("");
                try {
                    if (!((HashMap<String, Object>) hashMapCourierProfileMainOBJ.get("Address")).get("Street1").equals("")) {
                        if (((String) ((HashMap<String, Object>) hashMapCourierProfileMainOBJ.get("Address")).get("Street1")).contains("Australia"))
                            addressTextMyProfile.append("" + ((HashMap<String, Object>) hashMapCourierProfileMainOBJ.get("Address")).get("Street1"));
                        else {
                            addressTextMyProfile.append("" + ((HashMap<String, Object>) hashMapCourierProfileMainOBJ.get("Address")).get("Street1"));
                            if (!((HashMap<String, Object>) hashMapCourierProfileMainOBJ.get("Address")).get("Street2").equals(""))
                                addressTextMyProfile.append(", " + ((HashMap<String, Object>) hashMapCourierProfileMainOBJ.get("Address")).get("Street2"));
                            if (!((HashMap<String, Object>) hashMapCourierProfileMainOBJ.get("Address")).get("Suburb").equals("") &&
                                    !addressTextMyProfile.getText().toString().contains((CharSequence) ((HashMap<String, Object>) hashMapCourierProfileMainOBJ.get("Address")).get("Suburb")))
                                addressTextMyProfile.append(", " + ((HashMap<String, Object>) hashMapCourierProfileMainOBJ.get("Address")).get("Suburb"));
                            if (!((HashMap<String, Object>) hashMapCourierProfileMainOBJ.get("Address")).get("Zipcode").equals("")
                                    && !addressTextMyProfile.getText().toString().contains((CharSequence) ((HashMap<String, Object>) hashMapCourierProfileMainOBJ.get("Address")).get("Zipcode")))
                                addressTextMyProfile.append(", " + ((HashMap<String, Object>) hashMapCourierProfileMainOBJ.get("Address")).get("Zipcode"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                addressTextMyProfile.setFocusableInTouchMode(true);
                addressTextMyProfile.setFocusable(true);

               BackgroundTaskToSetProfileImage();

                try {
                    try {
                        courierFirstLastName.setText(((String) ((((String) hashMapCourierProfileMainOBJ.get("FirstName")).charAt(0) + "" + ((String) hashMapCourierProfileMainOBJ.get("LastName")).charAt(0)))).toUpperCase());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    LoginZoomToU.courierImage = (String) hashMapCourierProfileMainOBJ.get("Photo");
                    Picasso.with(MyProfile_Information.this).load(LoginZoomToU.courierImage).into(SlideMenuZoom2u.courierProfileImg_Menu);
                    SlideMenuZoom2u.courierFirstLastName.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                LoginZoomToU.courierCompany = (String) hashMapCourierProfileMainOBJ.get("CompanyName");
                if (SlideMenuZoom2u.courierCompanyNameTxt_Menu != null)
                    SlideMenuZoom2u.courierCompanyNameTxt_Menu.setText(LoginZoomToU.courierCompany);
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogActivity.alertDialogView(MyProfile_Information.this, "Sorry!", "Something went wrong, Please try again.");
        }
    }

    private void addBioToBioField() {
        try {
            if (!hashMapCourierProfileMainOBJ.get("Bio").equals("") && !hashMapCourierProfileMainOBJ.get("Bio").equals("null")) {
                bioTxtMyProfile.setText(hashMapCourierProfileMainOBJ.get("Bio") + "");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
