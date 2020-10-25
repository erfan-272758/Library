package com.google.library.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.library.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import static com.theartofdev.edmodo.cropper.CropImageView.CropShape;
import static com.theartofdev.edmodo.cropper.CropImageView.Guidelines;

public class ChooseData {
    public static final int VIDEO_REQUEST_CODE = 1111;
    private static final int CHANGE_MB = 999937;
    public static void startCroppingRequest(FragmentActivity act, @Nullable Uri uri, boolean circle){
        CropImage.ActivityBuilder builder;
        if (uri != null){
            builder = CropImage.activity(uri);
        }else {
            builder = CropImage.activity();
        }
        builder
                .setActivityTitle(act.getString(R.string.crop_title))
                .setCropMenuCropButtonTitle(act.getString(R.string.crop_button))
                .setGuidelines(Guidelines.ON_TOUCH)
                .setFixAspectRatio(false)
                .setOutputCompressQuality(20)
                .setOutputUri(Uri.fromFile(new File(act.getFilesDir(),"image")));
        if (circle)
            builder.setCropShape(CropShape.OVAL);
        else
            builder.setCropShape(CropShape.RECTANGLE);
        builder.start(act);
    }
    public static Uri handleResult(@Nullable Uri lastUri, AppCompatActivity activity,int requestCode,
                                   int resultCode, @Nullable Intent data, ImageView iv) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                assert result != null;
                boolean bitt = result.getBitmap() == null;
                boolean bitOrg = result.getOriginalBitmap() == null;
                boolean uri = result.getUri() == null;
                boolean uriOrg = result.getOriginalUri() == null;
                Log.i("result_result"," bit "+bitt+" bitOrg "+bitOrg+" uri "+uri+" uriOrg "+uriOrg);
                Bitmap bit = getBitmapFromUri(activity,result.getUri());
                DisplayMetrics dm = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                bit = decreaseSize(iv.getWidth(), bit);
//                boolean circle = (boolean) iv.getTag();
//                if (circle){
//                    bit = Circle.change(bit);
//                }
                iv.setImageBitmap(bit);
                iv.setTag(result.getUri());
                return result.getUri();
            }  else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                assert result != null;
                Exception error = result.getError();
                Log.i("result_res", Objects.requireNonNull(error.getMessage()));
            } else {

                Toast.makeText(activity, activity.getResources().getString(R.string.result_cancel_crop), Toast.LENGTH_SHORT).show();
                return lastUri;
            }
        }
        if (requestCode == VIDEO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    try {
                        FileInputStream in = new FileInputStream(getRealPathFromURI(activity, uri));
                        int size = (int) (in.getChannel().size() / CHANGE_MB);
                        if (size <= 16) {
                            return uri;
                        } else {
                            Toast.makeText(activity, activity.getResources().getString(R.string.video_long_size), Toast.LENGTH_SHORT).show();
                            return null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(activity, activity.getResources().getString(R.string.video_selected_cancel), Toast.LENGTH_SHORT).show();
            }
        }
        return lastUri;
    }
    private static Bitmap decreaseSize(float max,@Nullable Bitmap bit) {
        if (bit == null)
            return null;
        if (max > 512)
            max = 512;
        bit = Bitmap.createScaledBitmap(bit,(int)max, (int) ((max / bit.getWidth()) * bit.getHeight()),true);
        Bitmap scaledBitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        options.inBitmap = bit;
        options.outWidth = bit.getWidth();
        options.outHeight = bit.getHeight();
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;


        float imgRatio = (float) actualWidth / actualHeight;
        float maxRatio = max / max;


        if (actualHeight > max || actualWidth > max) {
            if (imgRatio < maxRatio) {
                imgRatio = max / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) max;
            } else if (imgRatio > maxRatio) {
                imgRatio = max / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) max;
            } else {
                actualHeight = (int) max;
                actualWidth = (int) max;

            }
        }


        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        options.inJustDecodeBounds = false;

        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
        try{
            scaledBitmap = Bitmap.createBitmap(actualWidth,actualHeight, Bitmap.Config.ARGB_8888);
        }catch (OutOfMemoryError e){
            e.printStackTrace();
            return null;
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bit, middleX - ((float) bit.getWidth() / 2), middleY - ((float) bit.getHeight() / 2), new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
    private static Bitmap getBitmapFromUri(AppCompatActivity act,Uri uri){
        try {
            assert uri != null;
            InputStream is = act.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static boolean saveImageFromBitmap(Bitmap bit,String filePath){
        try {
            FileOutputStream out = new FileOutputStream(filePath);
            boolean b = bit.compress(Bitmap.CompressFormat.JPEG,80,out);
            out.close();
            return b;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void startRequestVideo(FragmentActivity act){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setType("video/*");
        act.startActivityForResult(intent,VIDEO_REQUEST_CODE);
    }
    private static String getRealPathFromURI(AppCompatActivity act,Uri uri) {
        @SuppressLint("Recycle")
        Cursor cursor = act.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
            return cursor.getString(index);
        }
    }
}
