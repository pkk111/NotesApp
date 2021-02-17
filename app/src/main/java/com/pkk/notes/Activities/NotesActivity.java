package com.pkk.notes.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.pkk.notes.DBHelpers.DBHelper;
import com.pkk.notes.Models.NotesModel;
import com.pkk.notes.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class NotesActivity extends AppCompatActivity {

    int id = -1;
    DBHelper dbHelper;
    NotesModel model;
    EditText titleFeild;
    EditText bodyFeild;
    ImageButton imgButton;
    boolean isImageSet = false;
    private static final int RESULT_LOAD_IMAGE = 101;
    Bitmap grayScaleBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(NotesActivity.this, MainActivity.class));
                }
            });
        }
        id = getIntent().getIntExtra("id", -1);
        dbHelper = new DBHelper(this);
        initialize();
    }

    private void initialize() {
        titleFeild = findViewById(R.id.note_title);
        bodyFeild = findViewById(R.id.note_body);
        imgButton = findViewById(R.id.note_image);

        model = new NotesModel();

        if (id == -1) {
            imgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isImageSet) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, RESULT_LOAD_IMAGE);
                    }
                }
            });
        } else {
            model = dbHelper.getNote(id);
            byte[] byteImg = model.getImage();
            if (byteImg != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(byteImg, 0, byteImg.length);
                ViewTreeObserver vto = imgButton.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        imgButton.setImageBitmap(Bitmap.createScaledBitmap(bmp, imgButton.getWidth(), imgButton.getHeight(), false));
                        isImageSet = true;
                    }
                });
            }
        }

        titleFeild.setText(model.getTitle());
        bodyFeild.setText(model.getBody());

        byte[] byteImg = model.getImage();
        if (byteImg != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteImg, 0, byteImg.length);
            addGrayScaleListner(imgButton, bitmap);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save) {
            saveOrDeleteNote();
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        if (id == R.id.delete) {
            if (id != 0) {
                dbHelper.deleteNote(model.getId());
                model = null;
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        saveOrDeleteNote();
        startActivity(new Intent(this, MainActivity.class));
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] img = stream.toByteArray();
                model.setImage(img);
                imgButton.setImageBitmap(Bitmap.createScaledBitmap(bitmap, imgButton.getWidth(), imgButton.getHeight(), false));
                addGrayScaleListner(imgButton, bitmap);
                isImageSet = true;
            } catch (IOException e) {
                Log.e("Bitmap", "Error loading bitmap");
            }
        }
    }

    void saveOrDeleteNote() {
        if (model == null)
            return;
        String title = titleFeild.getText().toString();
        String body = bodyFeild.getText().toString();
        Date time = Calendar.getInstance().getTime();
        if (!(title.isEmpty() || body.isEmpty())) {
            if (id == -1) {
                NotesModel notesModel = new NotesModel(title, body, time);
                if (model.getImage() != null)
                    notesModel.setImage(model.getImage());
                dbHelper.addNote(notesModel);
            } else {
                NotesModel notesModel = new NotesModel(model.getId(), title, body, time);
                notesModel.setImage(model.getImage());
                dbHelper.updateNote(notesModel);
            }
        } else if (id != -1) {
            if (title.isEmpty())
                Toast.makeText(this, "Updation failed because of empty title", Toast.LENGTH_SHORT);
            if (body.isEmpty())
                Toast.makeText(this, "Updation failed because of empty Body", Toast.LENGTH_SHORT);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    void addGrayScaleListner(ImageButton imageButton, Bitmap bitmap) {

        grayScaleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                int p = bitmap.getPixel(i, j);
                int r = (int) (Color.red(p) * .299);
                int g = (int) (Color.green(p) * 0.587);
                int b = (int) (Color.blue(p) * 0.114);
                grayScaleBitmap.setPixel(i, j, Color.rgb(r + g + b, r + g + b, r + g + b));
            }
        }
        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imageButton.setImageBitmap(Bitmap.createScaledBitmap(grayScaleBitmap, imgButton.getWidth(), imgButton.getHeight(), false));
                        break;
                    case MotionEvent.ACTION_UP:
                        imageButton.setImageBitmap(Bitmap.createScaledBitmap(bitmap, imgButton.getWidth(), imgButton.getHeight(), false));
                        break;
                }
                return false;
            }
        });
    }

}