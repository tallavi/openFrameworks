package cc.openframeworks;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.media.AudioManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class OFAndroidLifeCycle
{
	private static final int POP_AND_REMOVE_SELF = 1;
	private static final int POP = 2;
	private static final int PUSH = 3;
		
	private static Vector<State> m_statesStack = new Vector<State>();
	private static State m_currentState = null;
	private static Semaphore m_semaphor = new Semaphore(1, false);
	private static AtomicBoolean m_isWorkerDone = new AtomicBoolean(true);
	private static AtomicBoolean m_isInit = new AtomicBoolean(false);

	private static Activity m_activity = null;
	
	private static ArrayList<Activity> m_activities = new ArrayList<Activity>();
	private static ArrayList<Runnable> m_initializers = new ArrayList<Runnable>();
	
	private static OFGLSurfaceView mGLView = null;
	
	private static void pushState(State state)
	{
		int action = 0;
//        close
        try {
            m_semaphor.acquire();
            do {
                if (m_statesStack.isEmpty()) {
                    action = PUSH;
                    break;
                }
                State lastState = m_statesStack.lastElement();
                action = isDisableState(lastState, state);
                if (action == POP) {
                    m_statesStack.remove(lastState);
                }
            }
            while (action == POP);
            
            switch (action) {
                case POP_AND_REMOVE_SELF:
                    m_statesStack.remove(m_statesStack.size() - 1);
                    break;
                case PUSH:
                    m_statesStack.add(state);
                    break;

                default:
                    break;
            }
//        release
            m_semaphor.release();
            startWorkerThread();
        }
        catch (InterruptedException ex)
        {
            Log.e(OFAndroidLifeCycle.class.getSimpleName(), "pushState exception message: "+ex.getMessage(), ex);
            throw new RuntimeException("pushState state: "+ state +" exception message: "+ex.getMessage());
        }
	}
	
	private static int isDisableState(State lastInStack, State newState)
	{
		String errorMessage = "Illegal pushed state! Last state in stack "+ lastInStack.toString()+" new state: "+newState.toString();
		int  result = PUSH;
		switch (lastInStack) {
		case create:
			if(newState.equals(State.pause)||newState.equals(State.init)||newState.equals(State.create))
			{
				throw new IllegalStateException(errorMessage);
			} 
			else if(newState.equals(State.exit))
				result = POP;
			else if(newState.equals(State.destroy))
				result = POP_AND_REMOVE_SELF;
			break;
		case resume:
			if(newState.equals(State.resume)||newState.equals(State.create)||newState.equals(State.init))
			{
				throw new IllegalStateException(errorMessage);
			}
			else if(newState.equals(State.destroy))
				result = POP;
			else if(newState.equals(State.pause))
				result = POP_AND_REMOVE_SELF;
			break;
		case pause:
			if(newState.equals(State.exit)||newState.equals(State.create)||newState.equals(State.init)||newState.equals(State.pause))
			{
				throw new IllegalStateException(errorMessage);
			}
			else if(newState.equals(State.resume))
				result = POP_AND_REMOVE_SELF;
			break;
		case destroy:
			if(newState.equals(State.destroy)||newState.equals(State.init)||newState.equals(State.resume)||newState.equals(State.pause))
			{
				throw new IllegalStateException(errorMessage);
			}
			else if(newState.equals(State.create))
				result = POP_AND_REMOVE_SELF;
			break;
		case exit:
			throw new IllegalStateException(errorMessage);

		default:
			break;
		}
		return result;
	}
	
	private static boolean isNextStateLegal(State next)
	{
		boolean isLegal = true;
		
		switch(next)
		{
		case init:
			if(m_currentState != null)
				isLegal = false;
			break;
		case create:
			if(!(m_currentState.equals(State.init)||m_currentState.equals(State.destroy)))
				isLegal = false;
			break;
		case resume:
			if(!(m_currentState.equals(State.create)||m_currentState.equals(State.pause)))
				isLegal = false;
			break;
		case pause:
			if(!m_currentState.equals(State.resume))
				isLegal = false;
			break;
		case destroy:
			if(!(m_currentState.equals(State.pause)||m_currentState.equals(State.create)))
				isLegal = false;
			break;
		case exit:
			if(!(m_currentState.equals(State.init)||m_currentState.equals(State.destroy)))
				isLegal = false;
			break;
		}
		return isLegal;
	}
	
	private static void startWorkerThread() throws IllegalStateException
	{
		synchronized (m_isWorkerDone) 
		{
			if(!m_isWorkerDone.get())
				return;
			m_isWorkerDone.set(false);
		}
		Thread worker = new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
//close
                try {
                    m_semaphor.acquire();
                    while (!m_statesStack.isEmpty()) {
                        State next = m_statesStack.firstElement();
                        m_statesStack.removeElement(next);
//                    release
                        m_semaphor.release();
                        if (!isNextStateLegal(next))
                        {
                            throw new IllegalStateException("Illegal next state! when current state " + m_currentState.toString() + " next state: " + next.toString());
                        }

                        m_currentState = next;
                        switch (next) {
                            case init:
                                OFAndroidLifeCycleHelper.appInit(m_activity);
                                coreInitialized();
                                break;
                            case create:
                                OFAndroidLifeCycleHelper.onCreate();
                                break;
                            case resume:
                                OFAndroidLifeCycleHelper.onResume();
                                glResumed();
                                break;
                            case pause:
                                OFAndroidLifeCycleHelper.onPause();
                                glPaused();
                                break;
                            case destroy:
                                OFAndroidLifeCycleHelper.onDestroy();
                                break;
                            case exit:
                                OFAndroidLifeCycleHelper.exit();
                                m_currentState = null;
                                break;

                            default:
                                break;
                        }
                        //close
                        m_semaphor.acquire();
                    }
                }
                catch (InterruptedException ex)
                {
                    Log.e(OFAndroidLifeCycle.class.getSimpleName(), "startWorkerThread: stack size: "+m_statesStack.size() + "exception message: "+ex.getMessage(), ex);
                    m_semaphor.release();
                }
//                release
                m_semaphor.release();
				synchronized (m_isWorkerDone) {
					m_isWorkerDone.set(true);
				}
			}
		});
		worker.start();
	}
	
	private static void coreInitialized()
    {
        synchronized (m_isInit) {
        	m_isInit.set(true);
		}
        if(m_activity != null)
        	m_activity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					for (Runnable init : m_initializers)
			        {
						init.run();
			        }
			        m_initializers.clear();
				}
			});
    }
	
	private static void glResumed(){
		m_activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mGLView.setVisibility(View.VISIBLE);
			}
		});
	}
	
	private static void glPaused(){
		m_activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mGLView.setVisibility(View.INVISIBLE);
			}
		});
	}
	
	static Activity getActivity(){
		return OFAndroidLifeCycle.m_activity;
	}
	
	static OFGLSurfaceView getGLView(){
		if(mGLView == null)
		{
			mGLView = new OFGLSurfaceView(m_activity);
		}
		return mGLView;
	}
	
	public static void setActivity(Activity activity){
		m_activity = activity;
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		OFAndroidObject.setActivity(activity);
	}
	
//============================ Life Cycle Functions ===================//
	
	public static void addPostInit(Runnable runnable)
	{
		if(isInit())
			runnable.run();
        else
        {
            m_initializers.add(runnable);
        }
	}
	
	public static void clearPostInit(){
		m_initializers.clear();
	}
	
	public static boolean isInit()
	{
		synchronized (m_isInit) {
			return m_isInit.get();
		}
	}

	
	public static void init()
	{
		if(m_currentState != null)
		{
			return;
		}
		Log.i("OF","OFAndroid init...");
		pushState(State.init);
	}
	
	public static void glCreate(Activity activity)
	{
		getGLView();
		if(m_activities.isEmpty())
			pushState(State.create);
		m_activities.add(activity);
	}
	
	public static void glResume(ViewGroup glConteiner)
	{
		View glView = getGLView();
		ViewGroup parent = (ViewGroup)glView.getParent();
		if(parent != glConteiner){
			if(parent != null)
				parent.removeView(glView);
			glConteiner.addView(glView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		pushState(State.resume);
	}
	
	public static void glPause()
	{
		pushState(State.pause);
	}
	
	public static void glDestroy(Activity activity)
	{
		m_activities.remove(activity);
		if(m_activities.isEmpty())
			pushState(State.destroy);
	}
	
	public static void exit()
	{
		pushState(State.exit);
	}
	
	public enum State 
	{
		init, create, resume, pause, destroy, exit;
	}
}
