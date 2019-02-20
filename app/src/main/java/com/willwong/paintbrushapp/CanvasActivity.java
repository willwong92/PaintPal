package com.willwong.paintbrushapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


import com.rtugeek.android.colorseekbar.ColorSeekBar;
import com.willwong.paintbrushapp.adapter.ToolKitRecyclerViewAdapter;
import com.willwong.paintbrushapp.customview.CanvasView;

import java.io.File;


/**
 * Created by WillWong on 01/12/19
 */

public class CanvasActivity extends AppCompatActivity implements ToolKitRecyclerViewAdapter.ToolKitViewHolder.ToolKitDataTransmitter {
    private AlertDialog.Builder dialogBox;
    private ColorSeekBar mColorSeekBar;
    private CanvasView mCanvasView;
    private LayoutInflater mLayoutInflater;
    // recycler view that displays a list of tools horizontally
    private RecyclerView mToolKitRecyclerView;
    // Adapter that binds the icon images onto the recyclerview viewholder;
    private ToolKitRecyclerViewAdapter mToolKitAdapter;

    // positions for toolkit operations
    public static final int IMAGE_ID = 1;

    public static final int BRUSH_POS = 0;

    public static final int ERASER_POS = 1;

    public static final int PALETTE_POS = 2;

    public static final int UNDO_POS = 3;

    public static final int TRASH_POS = 4;

    public static final int SAVE_POS = 5;

    public static final int GALLERY_POS = 6;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);


        mCanvasView = findViewById(R.id.canvas_view);

        mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mToolKitRecyclerView = (RecyclerView) findViewById(R.id.tool_kit_layout);
        mToolKitAdapter = new ToolKitRecyclerViewAdapter(CanvasActivity.this, (ToolKitRecyclerViewAdapter.ToolKitViewHolder.ToolKitDataTransmitter) CanvasActivity.this);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(CanvasActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mToolKitRecyclerView.setLayoutManager(horizontalLayoutManager);
        mToolKitRecyclerView.setAdapter(mToolKitAdapter);


        mColorSeekBar = (ColorSeekBar) findViewById(R.id.color_seek_bar);
        mColorSeekBar.setVisibility(View.GONE);


    }


    // Delegates the position of toolkit item picked to the container activity and operates depending
    // on position.
    @Override
    public void getPos(int pos) {
        if (pos == BRUSH_POS || pos == ERASER_POS || pos == TRASH_POS || pos == SAVE_POS || pos == GALLERY_POS) {
            mColorSeekBar.setVisibility(View.GONE);
        }
        switch (pos) {
            case BRUSH_POS:
                mCanvasView.setBrushPaint();
                break;
            case ERASER_POS:
                mCanvasView.setEraserPaint();
                break;
            case PALETTE_POS:
                showColorSeekBar();
                break;
            case UNDO_POS:
                mCanvasView.undoPath();
                break;
            case TRASH_POS:
                dialogBox = new AlertDialog.Builder(this);
                dialogBox.setTitle("Start Over")
                        .setMessage("Clear the current drawing and start a new one?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mCanvasView.newCanvas();
                                dialogInterface.cancel();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                dialogBox.create();
                dialogBox.show();
                break;
            case SAVE_POS:
                saveImage();
                break;
            case GALLERY_POS:
                findImageGallery();
                break;
        }

    }
    // Color picking tool
    public void showColorSeekBar() {
        mColorSeekBar.setVisibility(View.VISIBLE);
        mColorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                mCanvasView.setColor(color);
                //colorSeekBar.getAlphaValue();
            }
        });
    }

    // Saves image to local gallery. Incomplete.
    public void saveImage() {
        dialogBox = new AlertDialog.Builder(CanvasActivity.this);
        dialogBox.setTitle("Save Image");
        dialogBox.setMessage("Do you want to save this image?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final EditText editText = new EditText(CanvasActivity.this);
                        mCanvasView.setDrawingCacheEnabled(true);
                        Bitmap bitmap = mCanvasView.getDrawingCache();
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        File f = new File("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                        Uri uri = Uri.fromFile(f);
                        mediaScanIntent.setData(uri);
                        sendBroadcast(mediaScanIntent);

                        mCanvasView.destroyDrawingCache();
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        dialogBox.create();
        dialogBox.show();
    }

    //Locates saves images from local gallery.
    public void findImageGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture") ,IMAGE_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == IMAGE_ID) {
            if (resultCode == RESULT_OK) {
                Uri uri = intent.getData();
                String[] img = new String[]{MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, img, null, null, null);
                cursor.moveToFirst();
                int colIndex = cursor.getColumnIndex(img[0]);
                String filePath = cursor.getString(colIndex);
                cursor.close();
                Bitmap background = BitmapFactory.decodeFile(filePath);
                if (background != null) {
                    mCanvasView.setBitMap(background);
                }
            }
        }

    }


}
