package mpdproject.gcu.me.org.assignmenttest1;

/**
 * Craig McLaren s1437087
 */

public class Incident
{
    private String title;
    private String description;
    private String link;
    private String date;

    public Incident()
    {
        title = "";
        description = "";
        link = "";
        date = "";
    }

    public Incident(String atitle, String adesc, String alink, String adate)
    {
        title = atitle;
        description = adesc;
        link = alink;
        date = adate;
    }

    public void setTitle(String title1)
    {
        title = title1;
    }
    public String getTitle()
    {
        return title;
    }

    public void setDescription(String description1)
    {
        description = description1;
    }
    public String getDescription()
    {
        return description;
    }

    public void setLink(String link1)
    {
        link = link1;
    }
    public String getLink()
    {
        return link;
    }

    public void setDate(String date1)
    {
        date = date1;
    }
    public String getDate()
    {
        return date;
    }
}