
public class Lekcja extends Thread
{
    private double dlugoscLekcji;
    private double czasOdpowiedzi;
    private double sumaCzasuOdpowiedzi;
    
    public Lekcja(double dlugoscLekcji)
    {
        this.dlugoscLekcji=dlugoscLekcji;
        this.czasOdpowiedzi=0;
        this.sumaCzasuOdpowiedzi=0;
    }
    public double GetSumaCzasuOdpowiedzi()
    {
        return this.sumaCzasuOdpowiedzi;
    }
    public void SetSumaCzasuOdpowiedzi(double czasOdpowiedzi)
    {
        this.sumaCzasuOdpowiedzi+=czasOdpowiedzi;
    }
    public double GetDlugoscLekcji()
    {
        return this.dlugoscLekcji;
    }
    public void SetDlugoscLekcji(double dlugoscLekcji)
    {
        this.dlugoscLekcji=dlugoscLekcji;
    }
    public double GetCzasOdpowiedzi()
    {
        return this.czasOdpowiedzi;
    }    
    public void SetCzasOdpowiedzi(double czasOdpowiedzi)
    {
        this.czasOdpowiedzi=czasOdpowiedzi;
    }
}
