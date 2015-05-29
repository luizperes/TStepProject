package project.main.core;

/**
 * Created by Luiz on 15-05-11.
 */

public class GeoPoint
{
    private double latitude;
    private double longitude;

    public GeoPoint(double latitude, double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }
}
