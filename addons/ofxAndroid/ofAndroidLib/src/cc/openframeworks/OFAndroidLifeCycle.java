package cc.openframeworks;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;

public class OFAndroidLifeCycle
{
	private static final int POP_AND_REMOVE_SELF = 1;
	private static final int POP = 2;
	private static final int PUSH = 3;
	
	private static Vector<State> M_STATES_STACK = new Vector<State>();
	private static State M_CURRENT_STATE = null;
	
	private static AtomicBoolean M_IS_WORKER_DONE = new AtomicBoolean(true);
	
	private static ILifeCycleCallback M_CALLBACK = null; 
	private static Activity M_ACTIVITY = null;
	
	private static void pushState(State state)
	{
		int action = 0;
		do
		{
			if(M_STATES_STACK.isEmpty())
			{
				action = PUSH;
				break;
			}
			State lastState = M_STATES_STACK.lastElement();
			action= isDisableState(lastState, state);
			if(action == POP)
			{
				M_STATES_STACK.remove(lastState);
			}
		}
		while(action == POP);
		
		switch (action)
		{
		case POP_AND_REMOVE_SELF:
			M_STATES_STACK.remove(M_STATES_STACK.size()-1);
			break;
		case PUSH:
			M_STATES_STACK.add(state);
			break;

		default:
			break;
		}
		startWorkerThread();
	}
	
	private static int isDisableState(State lastInStack, State newState)
	{
		String errorMessage = "Illegal pushed state! Last state in stack "+ lastInStack.toString()+" new state: "+newState.toString();
		int  result = PUSH;
		switch (lastInStack) {
		case create:
			if(newState.equals(State.pause)||newState.equals(State.init)||newState.equals(State.create))
				throw new IllegalStateException(errorMessage);
			else if(newState.equals(State.exit))
				result = POP;
			else if(newState.equals(State.destroy))
				result = POP_AND_REMOVE_SELF;
			break;
		case resume:
			if(newState.equals(State.resume)||newState.equals(State.create)||newState.equals(State.init))
				throw new IllegalStateException(errorMessage);
			else if(newState.equals(State.destroy))
				result = POP;
			else if(newState.equals(State.pause))
				result = POP_AND_REMOVE_SELF;
			break;
		case pause:
			if(newState.equals(State.exit)||newState.equals(State.create)||newState.equals(State.init)||newState.equals(State.pause))
				throw new IllegalStateException(errorMessage);
			else if(newState.equals(State.resume))
				result = POP_AND_REMOVE_SELF;
			break;
		case destroy:
			if(newState.equals(State.destroy)||newState.equals(State.init)||newState.equals(State.resume)||newState.equals(State.pause))
				throw new IllegalStateException(errorMessage);
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
			if(M_CURRENT_STATE != null)
				isLegal = false;
			break;
		case create:
			if(!(M_CURRENT_STATE.equals(State.init)||M_CURRENT_STATE.equals(State.destroy)))
				isLegal = false;
			break;
		case resume:
			if(!(M_CURRENT_STATE.equals(State.create)||M_CURRENT_STATE.equals(State.pause)))
				isLegal = false;
			break;
		case pause:
			if(!M_CURRENT_STATE.equals(State.resume))
				isLegal = false;
			break;
		case destroy:
			if(!(M_CURRENT_STATE.equals(State.pause)||M_CURRENT_STATE.equals(State.create)))
				isLegal = false;
			break;
		case exit:
			if(!(M_CURRENT_STATE.equals(State.init)||M_CURRENT_STATE.equals(State.destroy)))
				isLegal = false;
			break;
		}
		return isLegal;
	}
	
	private static void startWorkerThread() throws IllegalStateException
	{
		synchronized (M_IS_WORKER_DONE) 
		{
			if(!M_IS_WORKER_DONE.get())
				return;
			M_IS_WORKER_DONE.set(false);
		}
		Thread worker = new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				while(!M_STATES_STACK.isEmpty())
				{
					State next = M_STATES_STACK.firstElement();
					M_STATES_STACK.removeElement(next);
					if(!isNextStateLegal(next))
						throw new IllegalStateException("Illegal next state! when current state "+ M_CURRENT_STATE.toString()+" next state: "+next.toString());
					
					M_CURRENT_STATE = next;
					switch (next) {
					case init:
						OFAndroid.appInit(M_ACTIVITY);
						M_CALLBACK.callbackInit();
						break;
					case create:
						OFAndroid.onCreateJava();
						M_CALLBACK.callbackCreated();
						break;
					case resume:
						OFAndroid.onResume();
						M_CALLBACK.callbackResumed();
						break;
					case pause:
						OFAndroid.onPause();
						M_CALLBACK.callbackPaused();
						break;
					case destroy:
						OFAndroid.onDestroy();
						M_CALLBACK.callbackDestroed();
						break;
					case exit:
						OFAndroid.exit();
						break;

					default:
						break;
					}
				}
				synchronized (M_IS_WORKER_DONE) {
					M_IS_WORKER_DONE.set(true);
				}
			}
		});
		worker.start();
	}
	
//============================ Life Cycle Functions ===================//
	public static void init(ILifeCycleCallback callback, Activity activity)
	{
		OFAndroidLifeCycle.M_CALLBACK = callback;
		OFAndroidLifeCycle.M_ACTIVITY = activity;
		if(M_CURRENT_STATE != null)
		{
			callback.callbackInit();
			return;
		}
		pushState(State.init);
	}
	
	public static void onCreate()
	{
		pushState(State.create);
	}
	
	public static void onResume()
	{
		pushState(State.resume);
	}
	
	public static void onPause()
	{
		pushState(State.pause);
	}
	
	public static void onDestroy()
	{
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

	public interface ILifeCycleCallback
	{
		public void callbackInit();
		
		public void callbackCreated();
		
		public void callbackResumed();
		
		public void callbackPaused();
		
		public void callbackDestroed();
	}
}
