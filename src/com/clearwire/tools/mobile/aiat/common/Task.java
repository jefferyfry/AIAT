package com.clearwire.tools.mobile.aiat.common;

import java.util.Iterator;
import java.util.Vector;


public abstract class Task extends Thread implements ProgressListener {
	
	protected Vector<TaskListener> mListeners = new Vector<TaskListener>();
	protected boolean mCancelled=false;
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public abstract void run();

	/* (non-Javadoc)
	 * @see com.trackaroo.apps.mobile.android.Trackmaster.ui.ProgressListener#incrementProgress()
	 */
	public void incrementProgress() {
		for(Iterator<TaskListener> iter = mListeners.iterator();iter.hasNext();)
			iter.next().incrementProgress();
	}
	
	public void cancelTask(){
		mCancelled=true;
	}
	
	/* (non-Javadoc)
	 * @see com.trackaroo.apps.mobile.android.Trackmaster.ui.ProgressListener#setMaxValue(int)
	 */
	public void setProgressTotal(int value) {
		//does nothing
	}

	/* (non-Javadoc)
	 * @see com.trackaroo.apps.mobile.android.Trackmaster.ui.ProgressListener#isCancelled()
	 */
	public boolean isCancelled() {
		return mCancelled;
	}
	
	protected void fireStarted(int progressCount){
		for(Iterator<TaskListener> iter = mListeners.iterator();iter.hasNext();)
			iter.next().started(progressCount);
	}

	protected void fireExceptionOccurred(Exception e){
		for(Iterator<TaskListener> iter = mListeners.iterator();iter.hasNext();)
			iter.next().exceptionOccurred(e);
	}
	
	protected void fireOutOfMemoryErrorOccurred(){
		for(Iterator<TaskListener> iter = mListeners.iterator();iter.hasNext();)
			iter.next().outOfMemoryErrorOccurred();
	}
	
	protected void fireFinished(){
		for(Iterator<TaskListener> iter = mListeners.iterator();iter.hasNext();)
			iter.next().taskFinished();
	}
	
	protected void fireCancelled(){
		for(Iterator<TaskListener> iter = mListeners.iterator();iter.hasNext();)
			iter.next().taskCancelled();
	}
	
	public void addListener(TaskListener listener){
		mListeners.add(listener);
	}
	
	public void removeListener(TaskListener listener){
		mListeners.remove(listener);
	}
}
