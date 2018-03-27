//
//
// Starter code for the Mobile Platform Development Assignment
// Seesion 2017/2018
//
//

/*
        Craig McLaren s1437087
* */

package mpdproject.gcu.me.org.assignmenttest1;

import android.content.res.Configuration;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private String url1="http://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private String url2="http://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String url3="http://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private TextView titleInput;
    private TextView descriptionInput;
    private TextView locationInput;
    private TextView dateInput;
    private TextView dataLoader;
    private EditText searchInput;
    private Button incidentButton;
    private Button roadButton;
    private Button planButton;
    private Button nextButton;
    private Button moreButton;
    private Button startButton;
    private Button searchButton;
    private Button confirmSearch;
    private String titleText;
    private String descText;
    private String dateText;
    private LinkedList<Incident> incidentList = null;
    private LinkedList<Incident> roadList = null;
    private LinkedList<Incident> planList = null;
    private LinkedList<Incident> curList = null;
    private LinkedList<Incident> searchList = null;
    private int count = 0;
    private String urlNum = "";
    private ViewFlipper viewFlipper;
    Thread netThread1 = null;
    Thread netThread2 = null;
    Thread netThread3 = null;
    Thread curThread = null;
    long startTime = System.nanoTime();
    //boolean startTimer = true;
    private TextView introText;
    private int currScreen = 0;
    private static final String STATE_COUNTER = "counter";
    private int mCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchInput = (EditText)findViewById(R.id.searchInput);
        titleInput = (TextView)findViewById(R.id.urlInput);
        locationInput = (TextView)findViewById(R.id.locInput);
        dateInput = (TextView)findViewById(R.id.dateInput);
        dataLoader = (TextView)findViewById(R.id.dataLoader);
        incidentButton = (Button)findViewById(R.id.incidentButton);
        incidentButton.setOnClickListener(this);
        roadButton = (Button)findViewById(R.id.roadButton);
        roadButton.setOnClickListener(this);
        planButton = (Button)findViewById(R.id.planButton);
        planButton.setOnClickListener(this);
        nextButton = (Button)findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
        moreButton = (Button)findViewById(R.id.moreButton);
        moreButton.setOnClickListener(this);
        startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        searchButton = (Button)findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        confirmSearch = (Button)findViewById(R.id.confirmSearch);
        confirmSearch.setOnClickListener(this);
        viewFlipper = (ViewFlipper)findViewById(R.id.flipView1);
        startProgress();
        introText = (TextView)findViewById(R.id.dataLoader);


    } // End of onCreate


    // attempted to prevent application moving back to initial screen though to no success
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now
        outState.putInt(STATE_COUNTER, mCounter);
    }


    // as per above
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (currScreen == 1)
            {
                viewFlipper = (ViewFlipper)findViewById(R.id.flipView1);
                Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
                viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.scrollView1)));
            }
            else
                if (currScreen == 2)
                {
                    viewFlipper = (ViewFlipper)findViewById(R.id.flipView1);
                    viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.scrollView2)));
                }

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            if (currScreen == 1)
            {
                viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.scrollView1)));
            }
            else
            if (currScreen == 2)
            {
                viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.scrollView2)));
            }
        }
    }

    public void onClick(View aview)
    {
        if (aview == searchButton)
        {
            viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.scrollView2)));
            currScreen = 2;
        }
        if (aview == confirmSearch)
        {
            searchForRoad();
        }
        if (aview == startButton)
        {
            if (planList != null) {
                viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.scrollView1)));
                currScreen = 1;
            }
            while (true)
            {
                if ((System.nanoTime() - startTime)/1000000 > 2000)
                {
                    //introText.setText("Data loaded please continue");
                    viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.scrollView1)));
                    currScreen = 1;
                    break;
                }
            }
        }
        if (aview == moreButton && curList != null) {
            Toast.makeText(this, curList.get(count).getDescription(), Toast.LENGTH_LONG).show();
        }
        if (aview == incidentButton) {
            count = 0;
            titleInput.setText("Current Incidents: ");
            curList = incidentList;
            updateFields();
        }
        else
        if (aview == roadButton)
        {
            count = 0;
            titleInput.setText("Current Roadworks: ");
            curList = roadList;
            updateFields();
        }
        else
        if (aview == planButton)
        {
            count = 0;
            titleInput.setText("Planned Roadworks: ");
            curList = planList;
            updateFields();
        }
        else
        if (aview == nextButton)
        {
            if (count < curList.size() - 1)
            {
                count += 1;
                //startProgress();
                updateFields();
            }
            else
            {
                count = 0;
                //startProgress();
                updateFields();
            }
        }
    }

    public void searchForRoad()
    {
        searchList = null;
        searchList = new LinkedList<Incident>();
        for (int i = 0; i < incidentList.size(); i++)
        {
            if (incidentList.get(i).getDescription().toLowerCase().contains(searchInput.getText())) {
                searchList.add(incidentList.get(i));
            }
        }
        for (int i = 0; i < roadList.size(); i++)
        {
            if (roadList.get(i).getDescription().toLowerCase().contains(searchInput.getText())) {
                searchList.add(roadList.get(i));
            }
        }
        for (int i = 0; i < planList.size(); i++)
        {
            if (planList.get(i).getDescription().toLowerCase().contains(searchInput.getText())) {
                searchList.add(planList.get(i));
            }
        }
        curList = searchList;
        count = 0;
        updateFields();
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.scrollView1)));
        currScreen = 1;
    }

    public void startProgress()
    {
        //ExecutorService executorService = Executors.newFixedThreadPool(3);
        //Future<Task> threado1 = executorService.submit(Task(url1).run());
        netThread1 = new Thread(new Task(url1), "curThread");
        netThread1.start();
        netThread2 = new Thread(new Task(url2), "curThread");
        netThread2.start();
        netThread3 = new Thread(new Task(url3), "curThread");
        netThread3.start();
    } //

    public void updateFields()
    {
        if (curList != null)
        {
            locationInput.setText(curList.get(count).getTitle());
            dateInput.setText(curList.get(count).getDate());
        }
    }



    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    class Task implements Runnable
    {
    private String url;
    private String result;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {
            LinkedList<Incident> listy1;
            URL aurl1;
            URLConnection yc1;
            URL aurl2;
            URLConnection yc2;
            URL aurl3;
            URLConnection yc3;
            BufferedReader in = null;
            String inputLine = "";
            result = "";

            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl1 = new URL(url);
                yc1 = aurl1.openConnection();
                in = new BufferedReader(new InputStreamReader(yc1.getInputStream()));

                while ((inputLine = in.readLine()) != null)
                {
                    result = result + inputLine;
                    Log.e("MyTag",inputLine);
                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }


            if (url == url1) {
                incidentList = parseData(result);
            }
            else
                if (url == url2) {
                    roadList = parseData(result);
                }
            else
                if (url == url3) {
                    planList = parseData(result);
                }
        }

    }

    private LinkedList<Incident> parseData(String dataToParse)
    {
        Incident incident = null;
        LinkedList<Incident> alist = null;
        boolean itemFound = false;

        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader( dataToParse ) );
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                // Found a start tag
                if(eventType == XmlPullParser.START_TAG)
                {
                    // Check which Tag we have
                    if (xpp.getName().equalsIgnoreCase("channel"))
                    {
                        alist = new LinkedList<Incident>();
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("item"))
                    {
                        incident = new Incident();
                        itemFound = true;
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("title") && itemFound)
                    {
                        // Now just get the associated text
                        String temp = xpp.nextText();
                        // Do something with text
                        incident.setTitle(temp);
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("description") && itemFound)
                    {
                        // Now just get the associated text
                        String temp = xpp.nextText();
                        // Do something with text
                        incident.setDescription(temp);
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("link") && itemFound)
                    {
                        // Now just get the associated text
                        String temp = xpp.nextText();
                        // Do something with text
                        incident.setLink(temp);
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("pubdate") && itemFound)
                    {
                        // Now just get the associated text
                        String temp = xpp.nextText();
                        // Do something with text
                        incident.setDate(temp);
                    }
                }
                else
                if(eventType == XmlPullParser.END_TAG)
                {
                    if (xpp.getName().equalsIgnoreCase("item"))
                    {
                        alist.add(incident);
                        itemFound = false;
                    }
                }


                // Get the next event
                eventType = xpp.next();

            } // End of while

            return alist;
        }
        catch (XmlPullParserException ae1)
        {
            Log.e("MyTag","Parsing error" + ae1.toString());
        }
        catch (IOException ae1)
        {
            Log.e("MyTag","IO error during parsing");
        }

        Log.e("MyTag","End document");

        return alist;

    }

}
