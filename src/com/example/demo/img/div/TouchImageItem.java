package com.example.demo.img.div;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;

public class TouchImageItem {

	private Bitmap bitmap;
	
	
	private PointF dpoint = new PointF(); // 偏移位置
	
	/**
	 * 中心点坐标
	 */
	private PointF centerP=new PointF();
	
	/**
	 * aciton_down 记录的点击点
	 */
	private  PointF startP = new PointF();
	
	private PointF begMoveP=new PointF();
	
	/**
	 * 缩放旋转的中心点
	 */
	private PointF mid = new PointF(); 
	
	/**
	 * 图片显示的矩阵
	 */
	public Matrix matrix = new Matrix();

	/**
	 * 最初两点的距离     用来计算缩放比  除改变后的距离就是缩放比
	 */
	float oldDist = 1f;
	
	/**
	 * 拖动中的缩放
	 */
	float moveDist = 1f;
	
	/**
	 * 总体缩放值
	 */
	float dscale=1;
	
	/**
	 * 最初的角度
	 */
    float oldRotation = 0; 
    
    /**
     * 旋转中角度
     */
    float moveRotation = 0; 
    
	private  int NONE = 0; 
	private  int DRAG = 1; 
	private  int ZOOM = 2; 
    int mode = NONE; 
	
    
    float imgRotation=0;
    
	/**
	 * 图片操作临时矩阵
	 */
	public Matrix matrix1 = new Matrix(); 
	
	/**
	 * 记录拖动前的矩阵
	 */
	Matrix savedMatrix = new Matrix(); 
	
	public  void draw(Canvas canvas) {
		canvas.save();
		canvas.drawBitmap(bitmap, matrix, null);
		canvas.restore();
	}
	
	public TouchImageItem(Context context){
		bitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.tt); 
		initCenterPoint();
	}
	
	/**
	 * 拖动开始点
	 * @param x
	 * @param y
	 */
	public void setStartP(float x,float y){
		
		
	}
	
	public void actionUp(MotionEvent event){
		mode = NONE;
	}
	
	public void actionPointerUp(MotionEvent event){
		mode = NONE;
	}

	public void actionDown(MotionEvent event){
		mode = DRAG;
		startP.set( event.getX(), event.getY());
		begMoveP.set( event.getX(), event.getY());
		
		savedMatrix.set(matrix);
	}
	
	public void actionPointerDown(MotionEvent event){
		mode = ZOOM; 
        oldDist = spacing(event); 
        moveDist = spacing(event); 
        
        oldRotation = rotation(event); 
        moveRotation=oldRotation;
        
        Log.e("imgRotation", "imgRotation:"+imgRotation);
        Log.e("oldRotation", "oldRotation:"+oldRotation);
        savedMatrix.set(matrix); 
        midPoint(mid, event); 
	}
	
	public void actionMove(MotionEvent event){
		 if (mode == ZOOM) { 
             matrix1.set(savedMatrix); 
             
             //缩放处理
             float newDist = spacing(event); 
             float scale = newDist / oldDist; 
             float scale2= newDist /moveDist ;
             dscale*=newDist/moveDist;
             moveDist=newDist;
             
             //缩放时候x y的偏移量
             dpoint.x=dpoint.x*scale2+mid.x*(1-scale2);
			 dpoint.y=dpoint.y*scale2+mid.y*(1-scale2);
			 matrix1.postScale(scale, scale, mid.x, mid.y);// 縮放
			 
             //旋转角度处理
             float nowRotation=rotation(event);
             float rotation = nowRotation - oldRotation; 
             float rotation2 = nowRotation - moveRotation; 
//             Log.e("", "rotation2:"+rotation2+"  imgRotation:"+imgRotation);
             imgRotation+=rotation2;
             moveRotation=nowRotation;
             
			 //旋转的时候 x y的偏移量
			 rotateDpoint(mid,rotation2);
			 matrix1.postRotate(rotation, mid.x, mid.y);// 旋轉 

             matrix.set(matrix1); 
         } else if (mode == DRAG) { 
             matrix1.set(savedMatrix); 
             
             dpoint.x+=event.getX()-begMoveP.x;
             dpoint.y+=event.getY()-begMoveP.y;
             
             matrix1.postTranslate(event.getX() - startP.x, event.getY() - startP.y);// 平移 
             matrix.set(matrix1);
             
             begMoveP.set(event.getX(), event.getY());
         } 
	}
	
	
	private PointF tePointF=new PointF();
	
	private void rotateDpoint(PointF mid,float rotation2) {
		initCenterPoint();
		double length=Math.sqrt(Math.pow((centerP.x-mid.x),2)+Math.pow((centerP.y-mid.y),2));
		float rotation=rotation(centerP,mid);
		float pRotation=(rotation+rotation2)%360;
		
//		Log.e("", "centerP.x:"+centerP.x+"   centerP.y:"+centerP.y);
//		Log.e("rotateDpoint", "   mid.x："+mid.x+"   mid.y:"+mid.y+  "   length:"+length);
//		Log.e("rotateDpoint", "rotation:"+rotation+"   rotation2:"+rotation2);
		
		double oldh=sin(rotation)*length;
		double oldw=cos(rotation)*length;
		
		double h=sin(pRotation)*length;
		double w=cos(pRotation)*length;
		
		
		double dh=h-oldh;
		double dw=w-oldw;
		
		Log.e("", "oldh:"+oldh+"   oldw:"+oldw);
		Log.e("", "h:"+h+"   w:"+w);
		Log.e("", "dh:"+dh+"   dw:"+dw);
		
		dpoint.x+=dw;
		dpoint.y+=dh;
		initCenterPoint();
		Log.e("initCenterPoint2", "centerP.x:"+centerP.x+"   centerP.y:"+centerP.y);
	}

	public void doMove(){
		
	}
	
	
	public void doRotate(){
		
	}
	
	
	public boolean isInView(float x,float y){
		
		initCenterPoint();
		
		double length=Math.sqrt(Math.pow((centerP.x-x),2)+Math.pow((centerP.y-y),2));
		
		float rotation=rotation(new PointF(x, y), centerP);
 
		float pRotation=(rotation-imgRotation)%360;
 
		double h=Math.abs(sin(pRotation)*length);
		double w=Math.abs(cos(pRotation)*length);
		
		if(h<getHeight()/2&&w<getWidth()/2){
			
			return true;
		}
 
		return false;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
	}
	
	
	public boolean isInView(PointF pointF){
		return isInView(pointF.x,pointF.y);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
	}
	
	
    // 触碰两点间距离 
    private float spacing(MotionEvent event) { 
    	if (event.getPointerCount() > 1) {
    		 float x = event.getX(0) - event.getX(1); 
    	        float y = event.getY(0) - event.getY(1); 
    	        return FloatMath.sqrt(x * x + y * y); 
    	}
    	return 0;
       
    } 
     
    // 取手势中心点 
    private void midPoint(PointF point, MotionEvent event) { 
    	if (event.getPointerCount() > 1) {
    		float x = event.getX(0) + event.getX(1); 
            float y = event.getY(0) + event.getY(1); 
            point.set(x / 2, y / 2); 
    	}
    } 
 
    // 取旋转角度 
    private float rotation(MotionEvent event) { 
    	return rotation(new PointF(event.getX(0),event.getY(0)),new PointF(event.getX(1),event.getY(1)));
    } 
	
    private void initCenterPoint(){
    	centerP.set(dpoint.x+getWidth()/2, dpoint.y+getHeight()/2);
    	Log.e("", "centerP.x:"+centerP.x+"   centerP.y:"+centerP.y);
//    	Log.e("", "tePointF.x:"+tePointF.x+"   tePointF.y:"+tePointF.y);
    }
    
    private float rotation(PointF p1,PointF p2){
    	double delta_x = (p1.x - p2.x); 
        double delta_y = (p1.y - p2.y); 
        double radians = Math.atan2(delta_y, delta_x); 
        return (float) Math.toDegrees(radians); 
    }
    
    private double toRadians(double angdeg){
    	
    	double d=Math.toRadians(angdeg);
    	
    	return d;
    }

    
    private float getWidth(){
    	return bitmap.getWidth()*dscale;
    }
    
    private float getHeight(){
    	return bitmap.getHeight()*dscale;
    }
    
    private double sin(double angdeg){
    	
    	return Math.sin(toRadians(angdeg));
    }
    
    private double cos(double angdeg){
    	return Math.cos(toRadians(angdeg));
    }
}
