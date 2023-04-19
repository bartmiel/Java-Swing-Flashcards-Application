
import java.net.*;

public class Server
{
    public static final int serverPort = 2020;
    ServerSocket s;
    
    //Tworzę gniazdo konstruktorem
    public Server()
    {
        try
        {
            s = new ServerSocket(serverPort);
            System.out.println("Gniazdo utworzone - serwer działa!");
        }
        catch(Exception e)
        {
            System.out.println("Nie można utworzyć gniazda");
            System.exit(1);
        }
    }
    public void uruchom()
    {
        try
        {
            while(true)
            {
                Socket socket = s.accept();
                new ClientSession(socket).start();
            }
        }
        catch(Exception ex)
        {
            System.out.println("Błąd: "+ex);
        }
    }
    public static void main(String[] args)
    {
        try
        {
        Server server = new Server();
        server.uruchom();
        server.s.close();
        }
        catch(Exception ex)
        {
            System.out.println("Błąd: "+ex);
        }
    }
}
