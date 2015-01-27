package com.example.demo.img.div;

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
 
public class TouchImageView extends ImageView { 
 
//    float x_down = 0; 
//    float y_down = 0; 
//    PointF start = new PointF(); 
//    PointF mid = new PointF(); 
//    float oldDist = 1f; 
//    float oldRotation = 0; 
//    Matrix matrix = new Matrix(); 
//    Matrix matrix1 = new Matrix(); 
 
//    boolean matrixCheck = false; 
 
    int widthScreen; 
    int heightScreen; 
    
    /**
     * 是否点击到图片上
     */
    private boolean downInViewFlag;
 
//    private TouchImageItem imageItem;
    
    private LinkedList<TouchImageItem> itemlist=new LinkedList<TouchImageItem>();
    
    /**
     * 需要做移动事件处理的ImageItem
     */
    private LinkedList<TouchImageItem> needMoveList=new LinkedList<TouchImageItem>();
    
//    Bitmap gintama; 
 
    public TouchImageView(Context context) { 
        super(context); 

        for (int i = 0; i <3; i++) {
        	 TouchImageItem imageItem=new TouchImageItem(context);
             addTouchImageItem(imageItem);
		}
       
        
        
        DisplayMetrics dm = new DisplayMetrics(); 
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm); 
        widthScreen = dm.widthPixels; 
        heightScreen = dm.heightPixels; 
    } 
 
    protected void onDraw(Canvas canvas) { 
    	
    	for (TouchImageItem imageItem : itemlist) {
    		imageItem.draw(canvas);
		}
    	
    } 
 
    
    public void addTouchImageItem(TouchImageItem imageItem){
    	itemlist.add(imageItem);
    }
    
    public boolean onTouchEvent(MotionEvent event) { 
        switch (event.getAction() & MotionEvent.ACTION_MASK) { 
        case MotionEvent.ACTION_DOWN: 
        	doActionDown(event);
            break; 
        case MotionEvent.ACTION_POINTER_DOWN: 
        	doActionPointerDown(event);
            break; 
        case MotionEvent.ACTION_MOVE:
        	for (TouchImageItem imageItem : needMoveList) {
        		imageItem.actionMove(event);
			}
            break; 
        case MotionEvent.ACTION_UP: 
        	for (TouchImageItem imageItem : needMoveList) {
        		imageItem.actionUp(event);
			}
            break; 
        case MotionEvent.ACTION_POINTER_UP: 
        	for (TouchImageItem imageItem : needMoveList) {
        		imageItem.actionPointerUp(event);
			}
            break; 
        } 
        
        invalidate();
        return true; 
    } 
 
 
    private void doActionPointerDown(MotionEvent event) {
    	needMoveList.clear();
    	PointF p1=new PointF(event.getX(0),event.getY(0));
    	PointF p2=new PointF(event.getX(1),event.getY(1));
    	
    	for (int i = itemlist.size()-1; i >=0 ; i--) {
    		TouchImageItem imageItem  =itemlist.get(i);
    		if(imageItem.isInView(p1)&&imageItem.isInView(p1)){
    			imageItem.actionPointerDown(event);
    			needMoveList.add(imageItem);
    			return;
    		}
    		else if(imageItem.isInView(p1)||imageItem.isInView(p1)){
    			imageItem.actionDown(event);
    			needMoveList.add(imageItem);
    		}
    	}
    	
    	
	}

	public void doActionDown(MotionEvent event){
    	needMoveList.clear();
    	
    	TouchImageItem touchItem=null;
    	for (int i = itemlist.size()-1; i >=0 ; i--) {
    		TouchImageItem imageItem  =itemlist.get(i);
			if(imageItem.isInView(event.getX(), event.getY())){
				touchItem=imageItem;
				imageItem.actionDown(event);
				break;
			}
		}
    	
    	if(touchItem!=null){
    		if(itemlist.remove(touchItem)){
    			itemlist.addLast(touchItem);
    			needMoveList.add(touchItem);
    		}
    	}
    }
    
} 