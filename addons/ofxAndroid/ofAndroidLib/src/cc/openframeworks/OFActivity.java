package cc.openframeworks;

import android.os.Bundle;
//import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

public abstract class OFActivity extends FragmentActivity {
	public void onGLSurfaceCreated(){}
	public void onLoadPercent(float percent){}
	public void onUnpackingResourcesDone(){}
	
	//gesture handler member
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		
		//create gesture listener
		//register the two events
		
		initView();
	}
	
	public void initView(){  
		String packageName = this.getPackageName();
        try {
        	Log.v("OF","trying to find class: "+packageName+".R$layout");
			Class<?> layout = Class.forName(packageName+".R$layout");
			View view = this.getLayoutInflater().inflate(layout.getField("main_layout").getInt(null),null);
			this.setContentView(view);
			
			Class<?> id = Class.forName(packageName+".R$id");
			OFAndroid.setGLContentView((OFGLSurfaceView)this.findViewById(id.getField("of_gl_surface").getInt(null)));
			
		} catch (Exception e) {
			Log.e("OF", "couldn't create view from layout falling back to GL only",e);
			OFAndroid.setGLContentView(new OFGLSurfaceView(this));
	        this.setContentView(OFAndroid.getGLContentView());
		}
	}
}
