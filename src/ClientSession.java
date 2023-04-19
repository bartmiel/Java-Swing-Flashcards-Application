
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientSession extends Thread
{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private static HashSet<PrintWriter> clients = new HashSet<>();
    public ClientSession(Socket socket)
    {
        try
        {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(),true);
        clients.add(out);
        }
        catch(IOException ex)
        {
            System.out.println("Błąd: "+ex);
        }
    }
    public void run()
    {
        String linia;
        try
        {
            while((linia = in.readLine()) != null)
            {
                System.out.println(this.getName()+">"+linia);
                for(PrintWriter p : clients)
                {
                    p.println(this.getName()+">"+linia);
                }
            }
            clients.remove(out);
            in.close();
            out.close();
            socket.close();
        }
        catch(Exception ex)
        {
            System.out.println("Błąd: "+ex);
        }
    }
}