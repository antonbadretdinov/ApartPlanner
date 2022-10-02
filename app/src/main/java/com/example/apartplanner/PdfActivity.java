package com.example.apartplanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apartplanner.model.Address;
import com.example.apartplanner.model.Studio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class PdfActivity extends AppCompatActivity {
    private final Context context;
    private final DatabaseReference dbRef;
    private final Address addressItem;

    public PdfActivity(Context context, DatabaseReference dbRef, Address addressItem) {
        this.context = context;
        this.dbRef = dbRef;
        this.addressItem = addressItem;
    }


    private void getStudiosFromDB(ArrayList<Studio> studios) {
        dbRef.child("studioList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Studio studio = dataSnapshot.getValue(Studio.class);
                    assert studio != null;
                    studios.add(studio);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    void generatePDF() {
        ArrayList<Studio> studios = new ArrayList<>();
        getStudiosFromDB(studios);
        Handler handler = new Handler();
        handler.postDelayed(() -> {

            String text = addressItem.getName();

            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1240, 1754, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            Paint title = new Paint();//title
            title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            title.setTextSize(40);
            title.setColor(Color.BLACK);
            title.setTextAlign(Paint.Align.CENTER);
            int xPos = (canvas.getWidth() / 2);
            canvas.drawText(text, xPos, 100, title);
            Bitmap imageBitmap = null;
            try {
                URL url = new URL(addressItem.getImageUrl());
                imageBitmap = BitmapFactory.decodeStream(url.openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert imageBitmap != null;
            imageBitmap = compressImage(imageBitmap);

            Paint paint = new Paint();//image
            paint.setTextAlign(Paint.Align.CENTER);
            int xImage = ((canvas.getWidth() / 2) - (imageBitmap.getWidth() / 2));
            canvas.drawBitmap(imageBitmap, xImage, 140, paint);

            Paint fillPaint = new Paint();//table
            fillPaint.setStyle(Paint.Style.FILL);

            Paint strokePaint = new Paint();
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setColor(Color.BLACK);
            strokePaint.setStrokeWidth(2);

            if(studios.size()<19) {
                int plusConst = 0;

                RectF rectCell = new RectF(20, 780, page.getInfo().getPageWidth() - 20, 830);

                canvas.drawRect(rectCell, strokePaint);
                canvas.drawLine(400, 780, 400, 830, strokePaint);
                canvas.drawLine(800, 780, 800, 830, strokePaint);
                title.setTextAlign(Paint.Align.LEFT);
                title.setTextSize(30);
                canvas.drawText("Студия №", 40, 815, title);
                canvas.drawText("Размер, м^2", 440, 815, title);
                canvas.drawText("Цена, тыс. руб", 840, 815, title);
                for (int i = 0; i < studios.size(); i++) {
                    RectF rect = new RectF(20, 830 + plusConst, page.getInfo().getPageWidth() - 20, 880 + plusConst);
                    if (studios.get(i).getState().equals("продано")) {
                        fillPaint.setColor(Color.parseColor("#87AEE4"));
                        canvas.drawRect(rect, fillPaint);
                        canvas.drawRect(rect, strokePaint);
                    } else if (studios.get(i).getState().equals("бронь")) {
                        fillPaint.setColor(Color.parseColor("#ffcf5c"));
                        canvas.drawRect(rect, fillPaint);
                        canvas.drawRect(rect, strokePaint);
                    } else {
                        canvas.drawRect(rect, strokePaint);
                    }
                    canvas.drawText(studios.get(i).getName(), 40, 865 + plusConst, title);
                    canvas.drawText(studios.get(i).getSize(), 440, 865 + plusConst, title);
                    canvas.drawText(studios.get(i).getState(), 840, 865 + plusConst, title);
                    canvas.drawLine(400, 830 + plusConst, 400, 880 + plusConst, strokePaint);
                    canvas.drawLine(800, 830 + plusConst, 800, 880 + plusConst, strokePaint);
                    plusConst += 50;
                }
            }else {
                RectF rectCell= new RectF(20,780,(int)(page.getInfo().getPageWidth()/2)-20,830);
                RectF rectCell2= new RectF((int)(page.getInfo().getPageWidth()/2)+20,780,page.getInfo().getPageWidth()-20,830);
                canvas.drawRect(rectCell,strokePaint);
                canvas.drawRect(rectCell2,strokePaint);
                canvas.drawLine(190,780,190,830,strokePaint);
                canvas.drawLine(380,780,380,830,strokePaint);
                canvas.drawLine(810,780,810,830,strokePaint);
                canvas.drawLine(1000,780,1000,1000,strokePaint);

                title.setTextAlign(Paint.Align.LEFT);
                title.setTextSize(26);
                canvas.drawText("Студия №",40,815,title);
                canvas.drawText("Размер, м^2",210,815,title);
                canvas.drawText("Цена, тыс. руб",400,815,title);
                canvas.drawText("Студия №",660,815,title);
                canvas.drawText("Размер, м^2",830,815,title);
                canvas.drawText("Цена, тыс. руб",1020,815,title);
                int plusConstLeft = 0;
                int plusConstRight = 0;
                for (int i = 0; i < studios.size(); i++) {
                    RectF rectRight = new RectF((int)(page.getInfo().getPageWidth()/2)+20,830+plusConstRight,
                            page.getInfo().getPageWidth()-20,880+plusConstRight);
                    RectF rectLeft = new RectF(20,830+plusConstLeft,
                            (int)(page.getInfo().getPageWidth()/2)-20,880+plusConstLeft);
                    if(i<18){
                        if(studios.get(i).getState().equals("продано")){
                            fillPaint.setColor(Color.parseColor("#87AEE4"));
                            canvas.drawRect(rectLeft,fillPaint);
                            canvas.drawRect(rectLeft,strokePaint);
                            canvas.drawText("продано",400,865+plusConstLeft,title);
                        }else if(studios.get(i).getState().equals("бронь")){
                            fillPaint.setColor(Color.parseColor("#ffcf5c"));
                            canvas.drawRect(rectLeft,fillPaint);
                            canvas.drawRect(rectLeft,strokePaint);
                            canvas.drawText("бронь",400,865+plusConstLeft,title);
                        }else{
                            canvas.drawRect(rectLeft,strokePaint);
                            canvas.drawText(studios.get(i).getState(),400,865+plusConstLeft,title);
                        }
                        canvas.drawText(studios.get(i).getName(),40,865+plusConstLeft,title);
                        canvas.drawLine(190,830+plusConstLeft,190,880+plusConstLeft,strokePaint);
                        canvas.drawLine(380,830+plusConstLeft,380,880+plusConstLeft,strokePaint);
                        canvas.drawText(studios.get(i).getSize(),210,865+plusConstLeft,title);
                        plusConstLeft+=50;
                    }else{
                        if(studios.get(i).getState().equals("продано")){
                            fillPaint.setColor(Color.parseColor("#87AEE4"));
                            canvas.drawRect(rectRight,fillPaint);
                            canvas.drawRect(rectRight,strokePaint);
                            canvas.drawText("продано",1020,865+plusConstRight,title);
                        }else if(studios.get(i).getState().equals("бронь")){
                            fillPaint.setColor(Color.parseColor("#ffcf5c"));
                            canvas.drawRect(rectRight,fillPaint);
                            canvas.drawRect(rectRight,strokePaint);
                            canvas.drawText("бронь",1020,865+plusConstRight,title);
                        }else{
                            canvas.drawRect(rectRight,strokePaint);
                            canvas.drawText(studios.get(i).getState(),1020,865+plusConstRight,title);
                        }
                        canvas.drawText(studios.get(i).getName(),660,865+plusConstRight,title);
                        canvas.drawLine(810,830+plusConstRight,810,880+plusConstRight,strokePaint);
                        canvas.drawLine(1000,830+plusConstRight,1000,880+plusConstRight,strokePaint);
                        canvas.drawText(studios.get(i).getSize(),830,865+plusConstRight,title);
                        plusConstRight+=50;
                    }
                }
            }
            pdfDocument.finishPage(page);
            Calendar calendar = Calendar.getInstance();
            String fileName = addressItem.getName()+calendar.getTime();
            if(fileName.contains("/")){
                fileName = fileName.replace("/","-");
            }
            if(fileName.contains(":")){
                fileName = fileName.replace(":",",");
            }
            File file = new File(Environment.getExternalStoragePublicDirectory("Download"), fileName+".pdf");
            try {
                pdfDocument.writeTo(new FileOutputStream(file));
                Toast.makeText(context, "Файл создан " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Произошла ошибка в создании файла " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        }, 1000);
    }




    private Bitmap compressImage(Bitmap bitmap){
        Bitmap scaledBitmap;
        double width = bitmap.getWidth();
        double height = bitmap.getHeight();
        double xFactor;
        if(width>height){
            xFactor = 1240/width;
        }else{
            xFactor = 1754/height;
        }
        width*=xFactor;
        height*=xFactor;
        if(height>600){
            xFactor = 600/height;
            width*=xFactor;
            height*=xFactor;
        }
        scaledBitmap = Bitmap.createScaledBitmap(bitmap,(int) width,(int) height,false);
        return scaledBitmap;
    }

}
