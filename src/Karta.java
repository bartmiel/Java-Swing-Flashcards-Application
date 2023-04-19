
import java.lang.reflect.Array;

public class Karta
{
    private String [] etykiety;
    
    public Karta(String[] etykiety)
    {
        this.etykiety = new String[2];
        for(int i = 0; i < 2; i++)
        {
            this.etykiety[i] = etykiety[i];
        }
    }
    //funkcja zwrajaca jdna z wartosci Karty
    public String GetEtykieta(int numerPola)
    {
        return this.etykiety[numerPola];
    }
    //funkcja ustalajaca jedna z wlasciwosci Karty
    public void SetEtykieta(int numerPola,String nowaWartosc)
    {
        this.etykiety[numerPola]=nowaWartosc;
    }
    public String toString()
    {
        return this.etykiety[0]+";"+this.etykiety[1];
    }
}