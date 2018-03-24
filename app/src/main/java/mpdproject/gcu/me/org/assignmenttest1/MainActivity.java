//
//
// Starter code for the Mobile Platform Development Assignment
// Seesion 2017/2018
//
//

package mpdproject.gcu.me.org.assignmenttest1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

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



public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private String url1="http://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private String url2="http://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String url3="http://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private TextView titleInput;
    private TextView descriptionInput;
    private TextView locationInput;
    private TextView dateInput;
    private Button incidentButton;
    private Button roadButton;
    private Button planButton;
    private Button nextButton;
    private Button moreButton;
    private String result1 = "";
    private String result2 = "";
    private String result3 = "";
    private String titleText;
    private String descText;
    private String dateText;
    private LinkedList<Incident> incidentList = null;
    private LinkedList<Incident> roadList = null;
    private LinkedList<Incident> planList = null;
    private LinkedList<Incident> curList = null;
    private int count = 0;
    private String urlNum = "";
    Thread netThread1 = null;
    Thread netThread2 = null;
    Thread netThread3 = null;
    Thread curThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleInput = (TextView)findViewById(R.id.urlInput);
        descriptionInput = (TextView)findViewById(R.id.descInput);
        locationInput = (TextView)findViewById(R.id.locInput);
        dateInput = (TextView)findViewById(R.id.dateInput);
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
        startProgress();

    } // End of onCreate

    public void onClick(View aview)
    {
        if (aview == moreButton && curList != null)
            descriptionInput.setText(curList.get(count).getDescription());
        if (aview == incidentButton) {
            count = 0;
            titleInput.setText("Current Incidents: ");
            //startProgress();
            curList = incidentList;
            updateFields();
        }
        else
        if (aview == roadButton)
        {
            count = 0;
            titleInput.setText("Current Roadworks: ");
            //startProgress();
            curList = roadList;
            updateFields();
        }
        else
        if (aview == planButton)
        {
            count = 0;
            titleInput.setText("Planned Roadworks: ");
            //startProgress();
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

    public void startProgress()
    {
        // Run network access on a separate thread;
        netThread1 = new Thread(new Task(url1), "curThread");
        netThread1.start();


    } //

    public void updateFields()
    {
        if (curList != null)
        {
            locationInput.setText(curList.get(count).getTitle());
            descriptionInput.setText("");
            dateInput.setText(curList.get(count).getDate());
        }
    }



    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    class Task implements Runnable
    {
    private String url;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {

            URL aurl1;
            URLConnection yc1;
            URL aurl2;
            URLConnection yc2;
            URL aurl3;
            URLConnection yc3;
            BufferedReader in = null;
            String inputLine = "";

            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl1 = new URL(url1);
                yc1 = aurl1.openConnection();
                in = new BufferedReader(new InputStreamReader(yc1.getInputStream()));

                while ((inputLine = in.readLine()) != null)
                {
                    result1 = result1 + inputLine;
                    Log.e("MyTag",inputLine);
                }
                in.close();

                aurl2 = new URL(url2);
                yc2 = aurl2.openConnection();
                in = new BufferedReader(new InputStreamReader(yc2.getInputStream()));
                while ((inputLine = in.readLine()) != null)
                {
                    result2 = result2 + inputLine;
                    Log.e("MyTag",inputLine);
                }
                in.close();

                aurl3 = new URL(url3);
                yc3 = aurl3.openConnection();
                in = new BufferedReader(new InputStreamReader(yc3.getInputStream()));
                while ((inputLine = in.readLine()) != null)
                {
                    result3 = result3 + inputLine;
                    Log.e("MyTag",inputLine);
                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }


            incidentList = parseData(result1);
            roadList = parseData(result2);
            planList= parseData(result3);
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
                        Log.e("MyTag","widget is " + incident.toString());
                        alist.add(incident);
                        itemFound = false;
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("channel"))
                    {
                        int size;
                        size = alist.size();
                        Log.e("MyTag","widgetcollection size is " + size);
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
