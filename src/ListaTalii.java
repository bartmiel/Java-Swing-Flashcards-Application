import java.util.ArrayList;

public class ListaTalii
{
    private ArrayList<Talia> listaTalii;
    
    public ListaTalii()
    {
        this.listaTalii = new ArrayList<Talia>();
    }

    public void Dodaj(Talia t)
    {
        this.listaTalii.add(t);
    }

    public void Usun(Talia t)
    {
        if (this.listaTalii.contains(t))
        {
            this.listaTalii.remove(t);
        }
    }
    public ArrayList<Talia> GetListaTalii()
    {
        return this.listaTalii;
    }
    public Talia GetWybranaTalia(int indeksWybranejTalii)
    {
        return this.listaTalii.get(indeksWybranejTalii);
    }
}