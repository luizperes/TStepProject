package project.main.core;

/**
 * Created by Luiz on 15-05-11.
 * source of calculus: http://www.sunearthtools.com/pt/tools/distance.php
 */

public class GeoCalc
{
    private static double RADIUS = 6372.795477598; // the quadratic average of the earth's radio in km


    /*
        Calculating the distance between two geographical points

        A fórmula utilizada para determinar a distância mais curta entre dois pontos no terreno (geodésicas),
        se aproxima do geóide para uma esfera de raio R = 6.372,795477598 km (média quádrica raio),
        para o cálculo poderia ter uma distância de erro de 0,3%, especialmente nos extremos polares,
        e por longas distâncias através de vários programas paralelos. Dado dois pontos A e B na esfera expressa pela latitude (lat) e longitude (lon), você terá:

        distance (A, B) = R * arccos (sin (lata) * sin (latB) + cos (lata) * cos (latB) * cos (Lona-lonB))
        All angles are in radians and to convert that to degree we need to multiply it by PI and then divide it by 180.
    */

    public static double distanceBetweenGeoPoints(GeoPoint pA, GeoPoint pB)
    {
        double latA = pA.getLatitude() * Math.PI / 180;
        double latB = pB.getLatitude() * Math.PI / 180;
        double lonA = pA.getLongitude() * Math.PI / 180;
        double lonB = pB.getLongitude() * Math.PI / 180;

        // distance in km.
        double distance = GeoCalc.RADIUS * Math.acos(Math.sin(latA) * Math.sin(latB) +
                          Math.cos(latA) * Math.cos(latB) * Math.cos(lonA - lonB));

        distance *= 1000; // distance in meters

        return  distance;
    }


    /*
        Calculation of direction between two geographical points

        To determine the direction from the starting point between two points on the earth, use the following formula:

        Δφ = ln( tan( latB / 2 + π / 4 ) / tan( latA / 2 + π / 4) )
        Δlon = abs( lonA - lonB )
        rolamento :  θ = atan2( Δlon ,  Δφ )

        Note: 1) ln = natural log      2) if Δlon > 180°  then   Δlon = Δlon (mod 180).
    */

    public static double directionOfTheta(GeoPoint pA, GeoPoint pB)
    {
        // all calculations are in radians
        double latA = pA.getLatitude() * Math.PI / 180;
        double latB = pB.getLatitude() * Math.PI / 180;

        double deltaPhi = Math.log(Math.tan(latB / 2 + Math.PI / 4) / Math.tan(latA / 2 + Math.PI / 4));
        double deltaLongitude = Math.abs(pA.getLongitude() - pB.getLongitude());

        if (deltaLongitude > 180)
        {
            deltaLongitude %= 180;
        }

        // convert the longitude to radians
        deltaLongitude = deltaLongitude * Math.PI / 180;

        // value of theta in radians
        double theta = Math.atan2(deltaLongitude, deltaPhi);

        // value of theta in degrees
        theta = theta / Math.PI * 180;

        return  theta;
    }


    /*
        Calculation of the destination point

        To determine the destination point, knowing the starting point the direction θ and the distance d, we use the following formula:

        latB = asin( sin( latA) * cos( d / R ) + cos( latA ) * sin( d / R ) * cos( θ ))
        lonB = lonA + atan2(sin( θ ) * sin( d / R ) * cos( latA ), cos( d / R ) − sin( latA ) * sin( latB ))
    */

    // implement, if needed.
}
