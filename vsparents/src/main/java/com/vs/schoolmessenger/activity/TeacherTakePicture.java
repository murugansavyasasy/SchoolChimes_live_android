package com.vs.schoolmessenger.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;


public class TeacherTakePicture extends AppCompatActivity {
    Uri picUri;
    Button nextbtn, changebtn;
    ImageView ivimage;

    String strSelectedImgFilePath;
    public static final String IMAGE_FOLDER_NAME = "School Messenger/image";
    public static final String IMAGE_FILE_NAME = "imageCircular.png";

    public static int IMAGE_UPLOAD_STATUS = 123;

    private File actualImage;
    private File compressedImage;
    String strCompressedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_take_picture);



        nextbtn = (Button) findViewById(R.id.Image_btnnext);
        ivimage = (ImageView) findViewById(R.id.cardImage_ivImage);
        changebtn = (Button) findViewById(R.id.cardImage_tvchangeimg);
        ImageView ivBack = (ImageView) findViewById(R.id.imagePopup_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherTakePicture.this).equals(LOGIN_TYPE_TEACHER)) {
                    Intent inStaff = new Intent(TeacherTakePicture.this, TeacherStaffImageCircular.class);
                    inStaff.putExtra("IMAGE_PATH", strCompressedImagePath);//strSelectedImgFilePath);
                    startActivityForResult(inStaff, IMAGE_UPLOAD_STATUS);
                } else {
                    Intent inStaff = new Intent(TeacherTakePicture.this, TeacherMgtImageCircular.class);
                    inStaff.putExtra("IMAGE_PATH", strCompressedImagePath);//strSelectedImgFilePath);
                    startActivityForResult(inStaff, IMAGE_UPLOAD_STATUS);
                }
            }
        });

        ivimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });

        changebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        loadingstatus = 1;
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        loadingstatus = 2;
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }

            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
//            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "temp.jpg"));
            outputFileUri = Uri.fromFile(new File(getImageFilename()));
            Log.d("outputurivalue", outputFileUri.toString());
        }
        return outputFileUri;
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    private String getImageFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File fileDir = new File(filepath, IMAGE_FOLDER_NAME);

        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File fileNamePath = new File(fileDir, IMAGE_FILE_NAME);
        strSelectedImgFilePath = fileNamePath.getPath();
        Log.d("FILE_PATH", strSelectedImgFilePath);
        return strSelectedImgFilePath; //+ System.currentTimeMillis()
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);
                try {

                    {

                        strSelectedImgFilePath = getRealPathFromURI(picUri);
                        actualImage =new File(strSelectedImgFilePath) ;//from(this, data.getData());
                        Log.d("ActualLength",String.format("Size : %s", getReadableFileSize(actualImage.length())));
                        compressedImage =new File(strSelectedImgFilePath) ;
                        compressedImage = new Compressor(this).compressToFile(actualImage);
                        Log.d("CompressedLength",String.format("Size : %s", getReadableFileSize(compressedImage.length())));
                        strCompressedImagePath=compressedImage.getPath();
                        Bitmap d = new BitmapDrawable(strCompressedImagePath).getBitmap();
                        int nh = (int) (d.getHeight() * (512.0 / d.getWidth()));
                        Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
                        scaled = getResizedBitmap(scaled, 500);
                        ivimage.setImageBitmap(scaled);

                        nextbtn.setEnabled(true);
                        changebtn.setEnabled(true);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {



                Uri imageUri = Uri.parse(strSelectedImgFilePath);
                ivimage.setImageURI(imageUri);

                nextbtn.setEnabled(true);
                changebtn.setEnabled(true);

            }

        } else if (requestCode == IMAGE_UPLOAD_STATUS) {
            String message = data.getStringExtra("MESSAGE");
            if (message.equals("SENT")) {
                finish();
            }
        }
    }

    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
