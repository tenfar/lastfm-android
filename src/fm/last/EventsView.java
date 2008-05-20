package fm.last;

import android.os.Bundle;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

import fm.last.R; 

public class EventsView extends ListActivity implements OnItemClickListener
{
	private ProgressDialog m_progressDialog = null;
	private EventsAdapter m_eventsAdapter;

	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_OFF);
		getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_INDETERMINATE_OFF);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView( R.layout.events_view );
		
		m_eventsAdapter = new EventsAdapter( this );
		setListAdapter( m_eventsAdapter );
		getListView().setOnItemClickListener(this);
		
		m_eventsAdapter.registerDataSetObserver( new DataSetObserver(){
			public void onChanged()
			{
				loadingComplete();
				Log.i( "Retrieved events" );
			}
		} );
			
		Log.i( "Downloading event information.." );

		m_eventsAdapter.getPagesByLocation("E5 0ES");
		showLoading();

		setupAnimation();
	}
	
	void setupAnimation()
	{
        AnimationSet set = new AnimationSet( true );

        Animation animation = new AlphaAnimation( 0.0f, 1.0f );
        animation.setDuration( 400 );
        set.addAnimation( animation );

        animation = new TranslateAnimation( Animation.RELATIVE_TO_SELF, 
        									0.0f,
        									Animation.RELATIVE_TO_SELF, 
        									0.0f,
        									Animation.RELATIVE_TO_SELF,
        									-1.0f,
        									Animation.RELATIVE_TO_SELF, 
        									0.0f );
        animation.setDuration( 100 );
        set.addAnimation( animation );

        LayoutAnimationController controller = new LayoutAnimationController( set, 0.5f );
        getListView().setLayoutAnimation( controller );
	}

	
	public void showLoading() {
		m_progressDialog = 
			ProgressDialog.show(this, 
								getResources().getString(R.string.eventsProgressTitle), 
								getResources().getString(R.string.eventsProgressMessage));
	}
	
	public void loadingComplete() {
		runOnUIThread(new Runnable(){
			public void run()
			{
				if(m_progressDialog != null)
				{
					m_progressDialog.dismiss();
					m_progressDialog = null;
				}
			}
		});
	}
	
	public void startRadio( Event evt )
	{
		Intent intent = new Intent("RADIOCLIENT");
		intent.putExtra( "eventXml", evt.xml() );
		startActivity( intent );
	}	

	public void scrollPageNext() {
		Log.i( "Scrolling to next page." );
	}
	
	public void onItemClick(AdapterView parent, View v, int position, long id) {
		startRadio((Event)m_eventsAdapter.getItem(position));
		return;
	}

}
