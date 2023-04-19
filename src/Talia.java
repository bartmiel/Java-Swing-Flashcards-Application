
import java.io.*;
import java.util.*;

public class Talia
{
    private Random random = new Random();
    private int indeksRandomowejKarty;
    private ArrayList <Karta> listaKart;
    private ArrayList <Karta> kartyDoNauki;
    private int doPowtorki,nauczone;
    
    private String nazwaTalii;
    //Pola potrzebne do eksportu zawartosci pliku do tali
    private BufferedWriter out;
    private File file;
    //Pola potrzebne do importu zawartosci pliku do tali
    private BufferedReader in;
    public Talia(String nazwaTalii)
    {
        this.doPowtorki=0;
        this.nauczone=0;
        this.listaKart = new ArrayList<>();
        this.kartyDoNauki = new ArrayList<>();
        this.nazwaTalii=nazwaTalii;
        this.file = new File(".\\Talie\\"+nazwaTalii+".txt");
    }

    public int getDoPowtorki()
    {
        return doPowtorki;
    }

    public void setDoPowtorki(int doPowtorki)
    {
        this.doPowtorki = doPowtorki;
    }

    public int getNauczone()
    {
        return nauczone;
    }

    public void setNauczone(int nauczone)
    {
        this.nauczone = nauczone;
    }
    
    public Karta LosujKarte()
    {
        indeksRandomowejKarty = random.nextInt(kartyDoNauki.size());
        return this.kartyDoNauki.get(indeksRandomowejKarty);
    }
    public ArrayList<Karta> GetKartyDoNauki()
    {
        return this.kartyDoNauki;
    }
    public ArrayList<Karta> GetListaKart()
    {
        return this.listaKart;
    }
    public int GetIloscKart()
    {
        return this.listaKart.size();
    }
    public String GetNazwaTalii()
    {
        return this.nazwaTalii;
    }
    public void SetNazwaTalii(String nazwaTalii)
    {
        this.nazwaTalii=nazwaTalii;
    }
     //Metoda do odczytu zawartości pliku i umieszczenia jej w obiekcie talia
    public void ImportTalii()
    {
        try
        {
            if(this.file.exists() && !this.file.isDirectory())
            {
                String linia;
                this.in = new BufferedReader(new InputStreamReader(new FileInputStream(this.file)));
                while(true)
                {
                    linia = this.in.readLine();
                    if(linia == null)
                        break;
                    this.listaKart.add(new Karta(linia.split(";")));
                }
                this.kartyDoNauki.addAll(listaKart);
                
                this.in.close();
            }
        }
        catch(IOException ex)
        {
            System.out.println("Błąd: "+ex);
        }
    }
    //Metoda do zapisu zawartości kontenera (listaKart w obiekcieTalia) i zapisania jej do pliku
    public void EksportTalii()
    {
        try
        {
            if(!this.file.exists() && !this.file.isDirectory())
            {
                this.out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file)));
                for(Karta karta : this.listaKart)
                {
                        this.out.write(karta.GetEtykieta(0)+";"+karta.GetEtykieta(1));
                    this.out.write("\n");
                }
                this.out.close();
            }
            else if(this.file.exists() && !this.file.isDirectory())
            {
                System.out.println("Tutaj plik istnieje wrzucić okienko z pytaniem czy nadpisać");
            }
            else
            {
                System.out.println("Błąd taki plik już istnieje");
            }
        }
        catch(IOException ex)
        {
            System.out.println("Błąd: "+ex);
        }
    }
    public void SetFile(File file)
    {
        this.file = file;
    }
    public File GetFile()
    {
        return this.file;
    }
    public ArrayList<Karta> GetKarty()
    {
       return listaKart;
    }
    public void DodajKarte(String przod,String tyl)
    {
        this.listaKart.add(new Karta(new String[]{przod,tyl}));
    }
    public void UsunKarte(int indeks)
    {
        listaKart.remove(indeks);
    }
    @Override
    public String toString()
    {
        return this.nazwaTalii;
    }
}
