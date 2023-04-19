
import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import static javax.swing.JFileChooser.APPROVE_OPTION;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class MainWindow extends javax.swing.JFrame implements Runnable
{

    //kontener z listaTalii
    private ListaTalii listaTalii = new ListaTalii();
    private int indeksWybranejTalii;
    private Lekcja lekcja;
    private Thread watekLekcji;
    private Talia wybranaTalia;    
    private Karta wylosowanaKarta;
    private int licznikKart=0;

    /**
     * Creates new form MainWindow
     */
    public MainWindow()
    {
        initComponents();
    }
    
    private void Importuj()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("./Talie"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Tekst oddzielony srednikami", "txt"));
        int wybor = fileChooser.showOpenDialog(this);
        if (wybor == APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            //Obcinam końcówkę .txt żeby nazwy talii podawać bez rozszerzenia
            String nazwaTalii = file.getName().substring(0, file.getName().length() - 4);
            Talia talia = new Talia(nazwaTalii);
            talia.ImportTalii();
            this.listaTalii.Dodaj(talia);
            DefaultListModel model = new DefaultListModel();
            model.addAll(this.listaTalii.GetListaTalii());
            this.ListaTaliiJList.setModel(model);
        }
    }
//dodawanie talii w oknie dodajJDialog

    public void DodajKarteDoTalii()
    {
        indeksWybranejTalii
                = this.listaTalii.GetListaTalii().indexOf(this.dodajTalieComboBox.getSelectedItem());
        this.listaTalii.GetWybranaTalia(indeksWybranejTalii).
                DodajKarte(this.przodTextField.getText(), this.tylTextField.getText());
    }
    
    private void Eksportuj()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("./Talie"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Tekst oddzielony srednikami", "txt"));
        int wybor = fileChooser.showSaveDialog(this);
        if (wybor == APPROVE_OPTION)
        {
            var talia = listaTalii.GetWybranaTalia(ListaTaliiJList.getSelectedIndex());
            talia.SetFile(new File(fileChooser.getSelectedFile().getPath()));
            talia.EksportTalii();
        }
    }
//utowrzTalieJDialog "ok" ButtonLsitener

    public void UtworzNowaTalie()
    {
        Vector temp = new Vector();
        if (utworzTalieNazwaTaliiTextField.getText().isBlank())
        {
            System.out.println("Nie podano nazwy");
            return;
        }
        this.listaTalii.Dodaj(new Talia(utworzTalieNazwaTaliiTextField.getText()));
        temp.addAll(listaTalii.GetListaTalii());
        this.ListaTaliiJList.setListData(temp);
        this.utworzTalieJDialog.dispose();
        this.utworzTalieNazwaTaliiTextField.setText("");
    }
    
    public void ZmienNazweTalii()
    {
        indeksWybranejTalii
                = this.listaTalii.GetListaTalii().indexOf(this.ListaTaliiJList.getSelectedValue());
        Talia wybranaTalia = this.listaTalii.GetWybranaTalia(indeksWybranejTalii);
        String nowaNazwaTalii = JOptionPane.showInputDialog(null, "Podaj nową nazwe", "Zmien nazwe", JOptionPane.OK_CANCEL_OPTION);
        if (nowaNazwaTalii.isBlank())
        {
            JOptionPane.showMessageDialog(null, "Nazwa nie może być pusta!");
            return;
        }
        wybranaTalia.SetNazwaTalii(nowaNazwaTalii);
        DefaultListModel model = new DefaultListModel();
        model.addAll(listaTalii.GetListaTalii());
        this.ListaTaliiJList.setModel(model);
    }

    //Odswiezanie widoku kart - rysowanie widoku od nowa po usunieciu dodaniu itp
    private void OdswiezPrzegladaj()
    {
        DefaultListModel model = new DefaultListModel();
        model.addAll(this.listaTalii.GetWybranaTalia(indeksWybranejTalii).GetListaKart());
        this.listaKartJList.setModel(model);
        
        this.iloscKartJLabel.setText(""+this.listaTalii.GetWybranaTalia(indeksWybranejTalii).GetIloscKart());
    }

    //metoda wizualizujaca okno przegladaj talie
    private void PrzegladajTalie()
    {
        PrzegladajWyczyscPodglad();

        try{
        indeksWybranejTalii
                = this.listaTalii.GetListaTalii().indexOf(this.ListaTaliiJList.getSelectedValue());
        this.nazwaTaliiJLabel.setText(this.listaTalii.GetWybranaTalia(indeksWybranejTalii).toString());
        
        OdswiezPrzegladaj();
        
        this.przegladajJDialog.pack();
        this.przegladajJDialog.setVisible(true);
        }catch (IndexOutOfBoundsException e)

        {
            System.out.println("Wyjatek :" + e);
            return;
        }
    }
    
    private void PrzegladajWyczyscPodglad()
    {
        przegladajPrzodJTextField.setText("");
        przegladajTylJTextField.setText("");
    }

    //metoda do edycji karty w oknie przeglądaj - wyświetla etykiete przednią i tylnią karty
    private void PrzegladajKarte()
    {
        try
        {
            int indeksKarty = listaKartJList.getSelectedIndex();
            List<Karta> karty = listaTalii.GetWybranaTalia(indeksWybranejTalii).GetKarty();
            przegladajPrzodJTextField.setText(karty.get(indeksKarty).GetEtykieta(0));
            przegladajTylJTextField.setText(karty.get(indeksKarty).GetEtykieta(1));
        } catch (Exception ex)
        {
            System.out.println("Błąd: " + ex);
        }
    }
    public void KoniecLekcji()
    {
        this.naukaJDialog.dispose();
        this.czasOdpowiedziTextField.setText("0.0");
        this.czasDoKoncaTextField.setText("0.0");
        this.StatystykiJLabel.setText(
                "Przejrzano dziś "+licznikKart+" kart w "+Math.round(this.lekcja.GetSumaCzasuOdpowiedzi())
                +" sekund ("+Math.round(this.lekcja.GetSumaCzasuOdpowiedzi())/licznikKart+"s/karte)");
        JOptionPane.showMessageDialog(null, "Koniec lekcji","Gratulacje",JOptionPane.INFORMATION_MESSAGE);
    }
    private void PrzegladajUsunKarte()
    {
        try
        {
            int indeksKarty = listaKartJList.getSelectedIndex();
            listaTalii.GetWybranaTalia(indeksWybranejTalii).UsunKarte(indeksKarty);
            OdswiezPrzegladaj();
            przegladajPrzodJTextField.setText("");
            przegladajTylJTextField.setText("");
        } catch (Exception ex)
        {
            System.out.println("Błąd: " + ex);
        }
    }
    
    private void PrzegladajZapiszZmiany()
    {
        try
        {
            int indeksKarty = listaKartJList.getSelectedIndex();
            List<Karta> karty = listaTalii.GetWybranaTalia(indeksWybranejTalii).GetKarty();
            karty.get(indeksKarty).SetEtykieta(0, przegladajPrzodJTextField.getText());
            karty.get(indeksKarty).SetEtykieta(1, przegladajTylJTextField.getText());
            OdswiezPrzegladaj();
        } catch (Exception ex)
        {
            System.out.println("Błąd: " + ex);
        }
    }
    private void WyslijChat()
    {
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        dodajJDialog = new javax.swing.JDialog();
        jLabel4 = new javax.swing.JLabel();
        przodTextField = new javax.swing.JTextField();
        Przód = new javax.swing.JLabel();
        tylTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        zamknijButton = new javax.swing.JButton();
        dodajButton = new javax.swing.JButton();
        dodajTalieComboBox = new javax.swing.JComboBox<>();
        poradnikJDialog = new javax.swing.JDialog();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        zamknijPoradnikButton = new javax.swing.JButton();
        utworzTalieJDialog = new javax.swing.JDialog();
        utworzTalieNazwaTaliiTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        utworzTalieAnulujJButton = new javax.swing.JButton();
        utworzTalieOkJButton = new javax.swing.JButton();
        przegladajJDialog = new javax.swing.JDialog();
        jScrollPane2 = new javax.swing.JScrollPane();
        listaKartJList = new javax.swing.JList<>();
        przegladajTylJTextField = new javax.swing.JTextField();
        przegladajPrzodJTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        iloscKartJLabel11 = new javax.swing.JLabel();
        nazwaTaliiJLabel = new javax.swing.JLabel();
        nazwaTaliiJLabel1 = new javax.swing.JLabel();
        iloscKartJLabel = new javax.swing.JLabel();
        ZapiszZmianyJButton = new javax.swing.JButton();
        ZamknijJButton = new javax.swing.JButton();
        UsunJButton = new javax.swing.JButton();
        rozpocznijNaukeJDialog = new javax.swing.JDialog();
        rozpocznijNaukeNauczoneJLabel = new javax.swing.JLabel();
        startJButton = new javax.swing.JButton();
        rozpocznijNaukeNazwaTalii2 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        noweJLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        dlugoscLekcjiTextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        naukaJDialog = new javax.swing.JDialog();
        naukaTylJLabel = new javax.swing.JLabel();
        naukaPrzodJLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        pokazOdpowiedzJButton = new javax.swing.JButton();
        doNaukuJLabel = new javax.swing.JLabel();
        nauczoneJLabel = new javax.swing.JLabel();
        dobrzeJButton = new javax.swing.JButton();
        zleJButton = new javax.swing.JButton();
        czasOdpowiedziTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        naukaNauczoneJLabel = new javax.swing.JLabel();
        naukaNoweJLabel = new javax.swing.JLabel();
        czasDoKoncaTextField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        zakonczButton = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        chatJDialog = new javax.swing.JDialog();
        ChatJTextField = new javax.swing.JTextField();
        WyslijJButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        ChatJTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        PrzegladajTaliieJButton = new javax.swing.JButton();
        DodajJButton = new javax.swing.JButton();
        StatystykiJButton = new javax.swing.JButton();
        ChatOnlineJButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        talieJButton = new javax.swing.JButton();
        TalieJLabel = new javax.swing.JLabel();
        ImportujPlikJButton = new javax.swing.JButton();
        UtworzTalieJButton = new javax.swing.JButton();
        rozpocznijNaukeJButton = new javax.swing.JButton();
        StatystykiJLabel = new javax.swing.JLabel();
        ListaTaliiJScrollPane = new javax.swing.JScrollPane();
        ListaTaliiJList = new javax.swing.JList<>();
        MenuGorneJMenuBar = new javax.swing.JMenuBar();
        PlikJMenu = new javax.swing.JMenu();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        ImportujJMenuItem = new javax.swing.JMenuItem();
        EksportujJMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        ZakonczJMenuItem = new javax.swing.JMenuItem();
        EdytujJMenu = new javax.swing.JMenu();
        ZmienNazweJMenuItem = new javax.swing.JMenuItem();
        UsunJMenuItem = new javax.swing.JMenuItem();
        PomocJMenu = new javax.swing.JMenu();
        PordanikJMenuItem = new javax.swing.JMenuItem();
        OProgramieJMenuItem = new javax.swing.JMenuItem();

        dodajJDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dodajJDialog.setTitle("Dodaj talie");
        dodajJDialog.setAlwaysOnTop(true);
        dodajJDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        dodajJDialog.setModal(true);
        dodajJDialog.setResizable(false);

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel4.setText("Tył");

        przodTextField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        przodTextField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                przodTextFieldActionPerformed(evt);
            }
        });

        Przód.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        Przód.setText("Przód");

        tylTextField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel6.setText("Talia");

        zamknijButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        zamknijButton.setText("Zamknij");
        zamknijButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                zamknijButtonActionPerformed(evt);
            }
        });

        dodajButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        dodajButton.setText("Dodaj");
        dodajButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                dodajButtonActionPerformed(evt);
            }
        });

        dodajTalieComboBox.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        dodajTalieComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                dodajTalieComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dodajJDialogLayout = new javax.swing.GroupLayout(dodajJDialog.getContentPane());
        dodajJDialog.getContentPane().setLayout(dodajJDialogLayout);
        dodajJDialogLayout.setHorizontalGroup(
            dodajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dodajJDialogLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(dodajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dodajJDialogLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dodajJDialogLayout.createSequentialGroup()
                        .addGroup(dodajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(dodajJDialogLayout.createSequentialGroup()
                                .addGap(0, 112, Short.MAX_VALUE)
                                .addComponent(dodajButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(zamknijButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tylTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(przodTextField)
                            .addGroup(dodajJDialogLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(dodajTalieComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(39, 39, 39))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dodajJDialogLayout.createSequentialGroup()
                        .addComponent(Przód)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        dodajJDialogLayout.setVerticalGroup(
            dodajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dodajJDialogLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(dodajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(dodajTalieComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(Przód)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(przodTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tylTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(dodajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dodajButton)
                    .addComponent(zamknijButton))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        poradnikJDialog.setTitle("Poradnik");
        poradnikJDialog.setModal(true);
        poradnikJDialog.setResizable(false);

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("*******************************************************************************\nSkróty klawiszowe:\n\nCtrl+Shift+P -przelacz profil\nCtrl+Shift+I -importuj\nCtrl+E -eksportuj\nCtrl+Q -zakoncz\nCtrl+Z -edytuj\n\n*******************************************************************************\nPodstawowe funkcje programu:\n\n-dodawanie talii\n-usuwanie talii\n-przegladanie talii\n-dodawanie słowek do talii\n-usuwanie słówek z talii\n-nauka słówek\n-importowanie wlasnych plików tekstowych\n-eksportowanie plików tekstowych\n");
        jTextArea1.setEnabled(false);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel2.setText("Poradnik");

        zamknijPoradnikButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        zamknijPoradnikButton.setText("Zamknij");
        zamknijPoradnikButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                zamknijPoradnikButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout poradnikJDialogLayout = new javax.swing.GroupLayout(poradnikJDialog.getContentPane());
        poradnikJDialog.getContentPane().setLayout(poradnikJDialogLayout);
        poradnikJDialogLayout.setHorizontalGroup(
            poradnikJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(poradnikJDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(poradnikJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(poradnikJDialogLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 142, Short.MAX_VALUE)
                        .addComponent(zamknijPoradnikButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        poradnikJDialogLayout.setVerticalGroup(
            poradnikJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(poradnikJDialogLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(poradnikJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(zamknijPoradnikButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                .addContainerGap())
        );

        utworzTalieJDialog.setTitle("Utworz talie");
        utworzTalieJDialog.setModal(true);
        utworzTalieJDialog.setResizable(false);

        utworzTalieNazwaTaliiTextField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        utworzTalieNazwaTaliiTextField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                utworzTalieNazwaTaliiTextFieldActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel5.setText("Nazwa talii:");

        utworzTalieAnulujJButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        utworzTalieAnulujJButton.setText("Anuluj");
        utworzTalieAnulujJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                utworzTalieAnulujJButtonActionPerformed(evt);
            }
        });

        utworzTalieOkJButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        utworzTalieOkJButton.setText("Ok");
        utworzTalieOkJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                utworzTalieOkJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout utworzTalieJDialogLayout = new javax.swing.GroupLayout(utworzTalieJDialog.getContentPane());
        utworzTalieJDialog.getContentPane().setLayout(utworzTalieJDialogLayout);
        utworzTalieJDialogLayout.setHorizontalGroup(
            utworzTalieJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(utworzTalieJDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(utworzTalieJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(utworzTalieNazwaTaliiTextField)
                    .addGroup(utworzTalieJDialogLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, utworzTalieJDialogLayout.createSequentialGroup()
                        .addGap(0, 237, Short.MAX_VALUE)
                        .addComponent(utworzTalieOkJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(utworzTalieAnulujJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        utworzTalieJDialogLayout.setVerticalGroup(
            utworzTalieJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(utworzTalieJDialogLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(utworzTalieNazwaTaliiTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(utworzTalieJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(utworzTalieAnulujJButton)
                    .addComponent(utworzTalieOkJButton))
                .addContainerGap())
        );

        przegladajJDialog.setTitle("Przeglad kart");
        przegladajJDialog.setModal(true);
        przegladajJDialog.setResizable(false);

        listaKartJList.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        listaKartJList.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                listaKartJListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(listaKartJList);

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel3.setText("Tyl");

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel8.setText("Przod");

        iloscKartJLabel11.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        iloscKartJLabel11.setText("Ilość kart :");

        nazwaTaliiJLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        nazwaTaliiJLabel.setText("brak");

        nazwaTaliiJLabel1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        nazwaTaliiJLabel1.setText("Nazwa talii :");

        iloscKartJLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        iloscKartJLabel.setText("brak");

        ZapiszZmianyJButton.setText("Zapisz zmiany");
        ZapiszZmianyJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ZapiszZmianyJButtonActionPerformed(evt);
            }
        });

        ZamknijJButton.setText("Zamknij");
        ZamknijJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ZamknijJButtonActionPerformed(evt);
            }
        });

        UsunJButton.setText("Usuń");
        UsunJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                UsunJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout przegladajJDialogLayout = new javax.swing.GroupLayout(przegladajJDialog.getContentPane());
        przegladajJDialog.getContentPane().setLayout(przegladajJDialogLayout);
        przegladajJDialogLayout.setHorizontalGroup(
            przegladajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(przegladajJDialogLayout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addGroup(przegladajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(przegladajJDialogLayout.createSequentialGroup()
                        .addGroup(przegladajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(przegladajJDialogLayout.createSequentialGroup()
                                .addComponent(iloscKartJLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(iloscKartJLabel))
                            .addGroup(przegladajJDialogLayout.createSequentialGroup()
                                .addComponent(nazwaTaliiJLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nazwaTaliiJLabel)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(przegladajJDialogLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(przegladajJDialogLayout.createSequentialGroup()
                        .addGroup(przegladajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, przegladajJDialogLayout.createSequentialGroup()
                                .addGroup(przegladajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(przegladajPrzodJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(48, 48, 48)
                                .addGroup(przegladajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(przegladajJDialogLayout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 103, Short.MAX_VALUE))
                                    .addComponent(przegladajTylJTextField)))
                            .addGroup(przegladajJDialogLayout.createSequentialGroup()
                                .addComponent(ZapiszZmianyJButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(UsunJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(ZamknijJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(50, 50, 50))))
        );
        przegladajJDialogLayout.setVerticalGroup(
            przegladajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(przegladajJDialogLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(przegladajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nazwaTaliiJLabel)
                    .addComponent(nazwaTaliiJLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(przegladajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iloscKartJLabel11)
                    .addComponent(iloscKartJLabel))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(przegladajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(przegladajJDialogLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(przegladajPrzodJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(przegladajJDialogLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(przegladajTylJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(przegladajJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ZapiszZmianyJButton)
                    .addComponent(ZamknijJButton)
                    .addComponent(UsunJButton))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        rozpocznijNaukeJDialog.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosed(java.awt.event.WindowEvent evt)
            {
                rozpocznijNaukeJDialogWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                rozpocznijNaukeJDialogWindowClosing(evt);
            }
        });

        rozpocznijNaukeNauczoneJLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        rozpocznijNaukeNauczoneJLabel.setForeground(new java.awt.Color(50, 205, 30));
        rozpocznijNaukeNauczoneJLabel.setText("0");

        startJButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        startJButton.setText("Start");
        startJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startJButtonActionPerformed(evt);
            }
        });

        rozpocznijNaukeNazwaTalii2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        rozpocznijNaukeNazwaTalii2.setText("Nazwa talii");

        jLabel21.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel21.setText("Do nauki :");

        jLabel22.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel22.setText("Nauczone :");

        noweJLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        noweJLabel.setText("0");

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel7.setText("Dlugość lekcji:");

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel9.setText("min");

        javax.swing.GroupLayout rozpocznijNaukeJDialogLayout = new javax.swing.GroupLayout(rozpocznijNaukeJDialog.getContentPane());
        rozpocznijNaukeJDialog.getContentPane().setLayout(rozpocznijNaukeJDialogLayout);
        rozpocznijNaukeJDialogLayout.setHorizontalGroup(
            rozpocznijNaukeJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rozpocznijNaukeJDialogLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(rozpocznijNaukeJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rozpocznijNaukeJDialogLayout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(rozpocznijNaukeNazwaTalii2))
                    .addGroup(rozpocznijNaukeJDialogLayout.createSequentialGroup()
                        .addGroup(rozpocznijNaukeJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(startJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(rozpocznijNaukeJDialogLayout.createSequentialGroup()
                                .addGroup(rozpocznijNaukeJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel21)
                                    .addComponent(jLabel22)
                                    .addComponent(jLabel7))
                                .addGap(33, 33, 33)
                                .addGroup(rozpocznijNaukeJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(rozpocznijNaukeJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(rozpocznijNaukeNauczoneJLabel)
                                        .addComponent(noweJLabel))
                                    .addComponent(dlugoscLekcjiTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        rozpocznijNaukeJDialogLayout.setVerticalGroup(
            rozpocznijNaukeJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rozpocznijNaukeJDialogLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(rozpocznijNaukeNazwaTalii2)
                .addGap(38, 38, 38)
                .addGroup(rozpocznijNaukeJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(noweJLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(rozpocznijNaukeJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(rozpocznijNaukeNauczoneJLabel))
                .addGap(36, 36, 36)
                .addGroup(rozpocznijNaukeJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addGroup(rozpocznijNaukeJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(dlugoscLekcjiTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)))
                .addGap(18, 18, 18)
                .addComponent(startJButton)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        naukaJDialog.setModal(true);
        naukaJDialog.setResizable(false);
        naukaJDialog.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosed(java.awt.event.WindowEvent evt)
            {
                naukaJDialogWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                naukaJDialogWindowClosing(evt);
            }
        });

        naukaTylJLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        naukaTylJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        naukaTylJLabel.setText("...");

        naukaPrzodJLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        naukaPrzodJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        naukaPrzodJLabel.setText("...");

        pokazOdpowiedzJButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        pokazOdpowiedzJButton.setText("Pokaz odpowiedź");
        pokazOdpowiedzJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                pokazOdpowiedzJButtonActionPerformed(evt);
            }
        });

        doNaukuJLabel.setText("Pozostało do nauki:");

        nauczoneJLabel.setText("Już umiesz:");

        dobrzeJButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        dobrzeJButton.setText("Dobrze");
        dobrzeJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                dobrzeJButtonActionPerformed(evt);
            }
        });

        zleJButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        zleJButton.setText("Źle");
        zleJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                zleJButtonActionPerformed(evt);
            }
        });

        czasOdpowiedziTextField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        czasOdpowiedziTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        czasOdpowiedziTextField.setText("0.0");
        czasOdpowiedziTextField.setEnabled(false);

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel11.setText("Czas odpowiedzi");

        naukaNauczoneJLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        naukaNauczoneJLabel.setForeground(new java.awt.Color(0, 255, 0));
        naukaNauczoneJLabel.setText("0");

        naukaNoweJLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        naukaNoweJLabel.setForeground(new java.awt.Color(204, 102, 0));
        naukaNoweJLabel.setText("0");

        czasDoKoncaTextField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        czasDoKoncaTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        czasDoKoncaTextField.setText("0.0");
        czasDoKoncaTextField.setEnabled(false);

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel13.setText("Czas do końca");

        zakonczButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        zakonczButton.setText("Zakończ");
        zakonczButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                zakonczButtonActionPerformed(evt);
            }
        });

        jLabel10.setText("s");

        jLabel12.setText("s");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(czasOdpowiedziTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel11)
                    .addComponent(jLabel13)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(czasDoKoncaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(43, 43, 43)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(dobrzeJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(zleJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(pokazOdpowiedzJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(doNaukuJLabel)
                            .addComponent(nauczoneJLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(naukaNauczoneJLabel)
                            .addComponent(naukaNoweJLabel))
                        .addGap(28, 28, 28))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(zakonczButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(naukaNoweJLabel)
                            .addComponent(doNaukuJLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(naukaNauczoneJLabel)
                            .addComponent(nauczoneJLabel)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(czasOdpowiedziTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(czasDoKoncaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(dobrzeJButton)
                            .addComponent(zleJButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pokazOdpowiedzJButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(zakonczButton)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout naukaJDialogLayout = new javax.swing.GroupLayout(naukaJDialog.getContentPane());
        naukaJDialog.getContentPane().setLayout(naukaJDialogLayout);
        naukaJDialogLayout.setHorizontalGroup(
            naukaJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(naukaJDialogLayout.createSequentialGroup()
                .addGroup(naukaJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(naukaJDialogLayout.createSequentialGroup()
                        .addGap(285, 285, 285)
                        .addComponent(naukaPrzodJLabel))
                    .addGroup(naukaJDialogLayout.createSequentialGroup()
                        .addGap(283, 283, 283)
                        .addComponent(naukaTylJLabel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        naukaJDialogLayout.setVerticalGroup(
            naukaJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(naukaJDialogLayout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(naukaPrzodJLabel)
                .addGap(32, 32, 32)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(naukaTylJLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        WyslijJButton.setText("Wyślij");
        WyslijJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                WyslijJButtonActionPerformed(evt);
            }
        });

        ChatJTextArea.setColumns(20);
        ChatJTextArea.setRows(5);
        jScrollPane3.setViewportView(ChatJTextArea);

        javax.swing.GroupLayout chatJDialogLayout = new javax.swing.GroupLayout(chatJDialog.getContentPane());
        chatJDialog.getContentPane().setLayout(chatJDialogLayout);
        chatJDialogLayout.setHorizontalGroup(
            chatJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatJDialogLayout.createSequentialGroup()
                .addGroup(chatJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(chatJDialogLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(ChatJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(WyslijJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(chatJDialogLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        chatJDialogLayout.setVerticalGroup(
            chatJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, chatJDialogLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(chatJDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ChatJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(WyslijJButton))
                .addGap(19, 19, 19))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MackiBartki");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt)
            {
                formWindowOpened(evt);
            }
        });

        PrzegladajTaliieJButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        PrzegladajTaliieJButton.setText("Przeglądaj");
        PrzegladajTaliieJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                PrzegladajTaliieJButtonActionPerformed(evt);
            }
        });

        DodajJButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        DodajJButton.setText("Dodaj");
        DodajJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                DodajJButtonActionPerformed(evt);
            }
        });

        StatystykiJButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        StatystykiJButton.setText("Statystyki");
        StatystykiJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                StatystykiJButtonActionPerformed(evt);
            }
        });

        ChatOnlineJButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        ChatOnlineJButton.setForeground(new java.awt.Color(204, 102, 0));
        ChatOnlineJButton.setText("Chat on-line");
        ChatOnlineJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ChatOnlineJButtonActionPerformed(evt);
            }
        });

        talieJButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        talieJButton.setText("Talie");
        talieJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                talieJButtonActionPerformed(evt);
            }
        });

        TalieJLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        TalieJLabel.setText("Talie");

        ImportujPlikJButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        ImportujPlikJButton.setText("Importuj plik");
        ImportujPlikJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ImportujPlikJButtonActionPerformed(evt);
            }
        });

        UtworzTalieJButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        UtworzTalieJButton.setText("Utwórz talię");
        UtworzTalieJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                UtworzTalieJButtonActionPerformed(evt);
            }
        });

        rozpocznijNaukeJButton.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        rozpocznijNaukeJButton.setText("Rozpocznij nauke");
        rozpocznijNaukeJButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                rozpocznijNaukeJButtonActionPerformed(evt);
            }
        });

        StatystykiJLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        StatystykiJLabel.setText("Przejrzano dziś 0 kart w 0 sekund (0s/kartę)");

        ListaTaliiJList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                ListaTaliiJListValueChanged(evt);
            }
        });
        ListaTaliiJScrollPane.setViewportView(ListaTaliiJList);

        PlikJMenu.setText("Plik");
        PlikJMenu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        PlikJMenu.add(jSeparator3);

        ImportujJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        ImportujJMenuItem.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        ImportujJMenuItem.setText("Importuj");
        ImportujJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ImportujJMenuItemActionPerformed(evt);
            }
        });
        PlikJMenu.add(ImportujJMenuItem);

        EksportujJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        EksportujJMenuItem.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        EksportujJMenuItem.setText("Eksportuj");
        EksportujJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                EksportujJMenuItemActionPerformed(evt);
            }
        });
        PlikJMenu.add(EksportujJMenuItem);
        PlikJMenu.add(jSeparator4);

        ZakonczJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        ZakonczJMenuItem.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        ZakonczJMenuItem.setText("Zakoncz");
        ZakonczJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ZakonczJMenuItemActionPerformed(evt);
            }
        });
        PlikJMenu.add(ZakonczJMenuItem);

        MenuGorneJMenuBar.add(PlikJMenu);

        EdytujJMenu.setText("Edytuj");
        EdytujJMenu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        ZmienNazweJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        ZmienNazweJMenuItem.setText("Zmień nazwe");
        ZmienNazweJMenuItem.setEnabled(false);
        ZmienNazweJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ZmienNazweJMenuItemActionPerformed(evt);
            }
        });
        EdytujJMenu.add(ZmienNazweJMenuItem);

        UsunJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        UsunJMenuItem.setText("Usun");
        UsunJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                UsunJMenuItemActionPerformed(evt);
            }
        });
        EdytujJMenu.add(UsunJMenuItem);

        MenuGorneJMenuBar.add(EdytujJMenu);

        PomocJMenu.setText("Pomoc");
        PomocJMenu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        PordanikJMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        PordanikJMenuItem.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        PordanikJMenuItem.setText("Poradnik");
        PordanikJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                PordanikJMenuItemActionPerformed(evt);
            }
        });
        PomocJMenu.add(PordanikJMenuItem);

        OProgramieJMenuItem.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        OProgramieJMenuItem.setText("O programie");
        OProgramieJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                OProgramieJMenuItemActionPerformed(evt);
            }
        });
        PomocJMenu.add(OProgramieJMenuItem);

        MenuGorneJMenuBar.add(PomocJMenu);

        setJMenuBar(MenuGorneJMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1))
            .addGroup(layout.createSequentialGroup()
                .addGap(90, 90, 90)
                .addComponent(jLabel1)
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(TalieJLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rozpocznijNaukeJButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(UtworzTalieJButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ImportujPlikJButton))
                    .addComponent(StatystykiJLabel)
                    .addComponent(ListaTaliiJScrollPane))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(56, Short.MAX_VALUE)
                .addComponent(talieJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PrzegladajTaliieJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DodajJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(StatystykiJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ChatOnlineJButton)
                .addGap(32, 32, 32))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PrzegladajTaliieJButton)
                    .addComponent(DodajJButton)
                    .addComponent(StatystykiJButton)
                    .addComponent(ChatOnlineJButton)
                    .addComponent(talieJButton))
                .addGap(26, 26, 26)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TalieJLabel)
                        .addGap(15, 15, 15)
                        .addComponent(ListaTaliiJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(StatystykiJLabel)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ImportujPlikJButton)
                            .addComponent(UtworzTalieJButton)
                            .addComponent(rozpocznijNaukeJButton))))
                .addContainerGap(44, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ZakonczJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ZakonczJMenuItemActionPerformed
    {//GEN-HEADEREND:event_ZakonczJMenuItemActionPerformed
          System.exit(0);
    }//GEN-LAST:event_ZakonczJMenuItemActionPerformed

    private void PordanikJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_PordanikJMenuItemActionPerformed
    {//GEN-HEADEREND:event_PordanikJMenuItemActionPerformed
        this.poradnikJDialog.pack();
        this.poradnikJDialog.setVisible(true);
    }//GEN-LAST:event_PordanikJMenuItemActionPerformed
    private void OProgramieJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_OProgramieJMenuItemActionPerformed
    {//GEN-HEADEREND:event_OProgramieJMenuItemActionPerformed
        String message = "Copyright by Bartłomiej Mielniczek";
        JOptionPane.showMessageDialog(null, message, "About", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_OProgramieJMenuItemActionPerformed
    private void ImportujJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ImportujJMenuItemActionPerformed
    {//GEN-HEADEREND:event_ImportujJMenuItemActionPerformed
        this.Importuj();
    }//GEN-LAST:event_ImportujJMenuItemActionPerformed
    //dodajTalieComboBox acction listener
    private void DodajJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_DodajJButtonActionPerformed
    {//GEN-HEADEREND:event_DodajJButtonActionPerformed
        if (this.ListaTaliiJList.isSelectionEmpty())
        {
            return;
        }
        this.dodajJDialog.pack();
        DefaultComboBoxModel dcm = new DefaultComboBoxModel(listaTalii.GetListaTalii().toArray());
        this.dodajTalieComboBox.setModel(dcm);
        this.dodajJDialog.setVisible(true);
    }//GEN-LAST:event_DodajJButtonActionPerformed

    private void StatystykiJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_StatystykiJButtonActionPerformed
    {//GEN-HEADEREND:event_StatystykiJButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_StatystykiJButtonActionPerformed

    private void ChatOnlineJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ChatOnlineJButtonActionPerformed
    {//GEN-HEADEREND:event_ChatOnlineJButtonActionPerformed
        // TODO add your handling code here:
        this.chatJDialog.pack();
        this.chatJDialog.setVisible(true);
    }//GEN-LAST:event_ChatOnlineJButtonActionPerformed

    private void PrzegladajTaliieJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_PrzegladajTaliieJButtonActionPerformed
    {//GEN-HEADEREND:event_PrzegladajTaliieJButtonActionPerformed
        // TODO add your handling code here:
        this.PrzegladajTalie();
    }//GEN-LAST:event_PrzegladajTaliieJButtonActionPerformed
    //dodajDialog zamknijButton
    private void zamknijButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zamknijButtonActionPerformed
    {//GEN-HEADEREND:event_zamknijButtonActionPerformed
        this.dodajJDialog.dispose();
    }//GEN-LAST:event_zamknijButtonActionPerformed

    private void dodajButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_dodajButtonActionPerformed
    {//GEN-HEADEREND:event_dodajButtonActionPerformed
        // TODO add your handling code here:
        this.DodajKarteDoTalii();
        this.przodTextField.setText("");
        this.tylTextField.setText("");
        this.dodajJDialog.dispose();
    }//GEN-LAST:event_dodajButtonActionPerformed
    private void zamknijPoradnikButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zamknijPoradnikButtonActionPerformed
    {//GEN-HEADEREND:event_zamknijPoradnikButtonActionPerformed
        // TODO add your handling code here:
        this.przodTextField.setText("");
        this.tylTextField.setText("");
        this.poradnikJDialog.dispose();
    }//GEN-LAST:event_zamknijPoradnikButtonActionPerformed

    private void EksportujJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_EksportujJMenuItemActionPerformed
    {//GEN-HEADEREND:event_EksportujJMenuItemActionPerformed
        // TODO add your handling code here:
        this.Eksportuj();
    }//GEN-LAST:event_EksportujJMenuItemActionPerformed

    private void utworzTalieOkJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_utworzTalieOkJButtonActionPerformed
    {//GEN-HEADEREND:event_utworzTalieOkJButtonActionPerformed
        // TODO add your handling code here:
        this.UtworzNowaTalie();
    }//GEN-LAST:event_utworzTalieOkJButtonActionPerformed

    private void utworzTalieAnulujJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_utworzTalieAnulujJButtonActionPerformed
    {//GEN-HEADEREND:event_utworzTalieAnulujJButtonActionPerformed
        // TODO add your handling code here:
        this.utworzTalieJDialog.dispose();
        this.utworzTalieNazwaTaliiTextField.setText("");
    }//GEN-LAST:event_utworzTalieAnulujJButtonActionPerformed

    private void przodTextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_przodTextFieldActionPerformed
    {//GEN-HEADEREND:event_przodTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_przodTextFieldActionPerformed

    private void dodajTalieComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_dodajTalieComboBoxActionPerformed
    {//GEN-HEADEREND:event_dodajTalieComboBoxActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_dodajTalieComboBoxActionPerformed

    private void utworzTalieNazwaTaliiTextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_utworzTalieNazwaTaliiTextFieldActionPerformed
    {//GEN-HEADEREND:event_utworzTalieNazwaTaliiTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_utworzTalieNazwaTaliiTextFieldActionPerformed
//metoda zmien nazwe, zmienNazwButton
    private void ZmienNazweJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ZmienNazweJMenuItemActionPerformed
    {//GEN-HEADEREND:event_ZmienNazweJMenuItemActionPerformed
        // TODO add your handling code here:
        try
        {
            ZmienNazweTalii();
        } catch (IndexOutOfBoundsException e)
        {
            System.out.println("Wyjatek :" + e);
            return;
        }
    }//GEN-LAST:event_ZmienNazweJMenuItemActionPerformed
//metoda usuwa talie z klekcji
    private void UsunJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_UsunJMenuItemActionPerformed
    {//GEN-HEADEREND:event_UsunJMenuItemActionPerformed
        // TODO add your handling code here:
        try
        {
            indeksWybranejTalii
                    = this.listaTalii.GetListaTalii().indexOf(this.ListaTaliiJList.getSelectedValue());
            Talia wybranaTalia = this.listaTalii.GetWybranaTalia(indeksWybranejTalii);
            this.listaTalii.GetListaTalii().remove(wybranaTalia);
            DefaultListModel model = new DefaultListModel();
            model.addAll(listaTalii.GetListaTalii());
            this.ListaTaliiJList.setModel(model);
        } catch (IndexOutOfBoundsException e)
        {
            System.out.println("Wyjatek : " + e);
        }
    }//GEN-LAST:event_UsunJMenuItemActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowOpened
    {//GEN-HEADEREND:event_formWindowOpened
        // TODO add your handling code here:
        File katalog = new File(".\\Talie");
        //if(directory.isDirectory())
        File[] pliki = katalog.listFiles();
        for (File plik : pliki)
        {
            String nazwaTalii = plik.getName().substring(0, plik.getName().length() - 4);
            Talia talia = new Talia(nazwaTalii);
            talia.ImportTalii();
            this.listaTalii.Dodaj(talia);
            DefaultListModel model = new DefaultListModel();
            ListaTaliiJList.setModel(model);
            for (Talia t : listaTalii.GetListaTalii())
            {
                model.addElement(t);
            }
        }
    }//GEN-LAST:event_formWindowOpened

    private void talieJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_talieJButtonActionPerformed
    {//GEN-HEADEREND:event_talieJButtonActionPerformed
        // TODO add your handling code here:
        this.setVisible(true);
    }//GEN-LAST:event_talieJButtonActionPerformed

    private void ListaTaliiJListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_ListaTaliiJListValueChanged
    {//GEN-HEADEREND:event_ListaTaliiJListValueChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_ListaTaliiJListValueChanged

    private void rozpocznijNaukeJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rozpocznijNaukeJButtonActionPerformed
    {//GEN-HEADEREND:event_rozpocznijNaukeJButtonActionPerformed
        // TODO add your handling code here:
        try
        {
            indeksWybranejTalii
                    = this.listaTalii.GetListaTalii().indexOf(this.ListaTaliiJList.getSelectedValue());
            wybranaTalia = this.listaTalii.GetWybranaTalia(indeksWybranejTalii);
            this.noweJLabel.setText(wybranaTalia.GetIloscKart()-wybranaTalia.getNauczone() + "");
            this.rozpocznijNaukeNauczoneJLabel.setText(wybranaTalia.getNauczone()+"");
            this.dlugoscLekcjiTextField.setText("");
            this.rozpocznijNaukeJDialog.pack();
            this.rozpocznijNaukeJDialog.setVisible(true);
        } catch (IndexOutOfBoundsException e)
        {
            System.out.println("Wyjatek : " + e);
            return;
        }
    }//GEN-LAST:event_rozpocznijNaukeJButtonActionPerformed

    private void UtworzTalieJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_UtworzTalieJButtonActionPerformed
    {//GEN-HEADEREND:event_UtworzTalieJButtonActionPerformed
        // TODO add your handling code here:
        this.utworzTalieJDialog.pack();
        this.utworzTalieJDialog.setVisible(true);
    }//GEN-LAST:event_UtworzTalieJButtonActionPerformed

    private void ImportujPlikJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ImportujPlikJButtonActionPerformed
    {//GEN-HEADEREND:event_ImportujPlikJButtonActionPerformed
        // TODO add your handling code here:
        this.Importuj();
    }//GEN-LAST:event_ImportujPlikJButtonActionPerformed

    private void rozpocznijNaukeJDialogWindowClosed(java.awt.event.WindowEvent evt)//GEN-FIRST:event_rozpocznijNaukeJDialogWindowClosed
    {//GEN-HEADEREND:event_rozpocznijNaukeJDialogWindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_rozpocznijNaukeJDialogWindowClosed

    private void rozpocznijNaukeJDialogWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_rozpocznijNaukeJDialogWindowClosing
    {//GEN-HEADEREND:event_rozpocznijNaukeJDialogWindowClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_rozpocznijNaukeJDialogWindowClosing

    private void naukaJDialogWindowClosed(java.awt.event.WindowEvent evt)//GEN-FIRST:event_naukaJDialogWindowClosed
    {//GEN-HEADEREND:event_naukaJDialogWindowClosed
        // TODO add your handling code here:
        this.watekLekcji.interrupt();
        this.setVisible(true);
    }//GEN-LAST:event_naukaJDialogWindowClosed

    private void naukaJDialogWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_naukaJDialogWindowClosing
    {//GEN-HEADEREND:event_naukaJDialogWindowClosing
        // TODO add your handling code here:
        this.watekLekcji.interrupt();
        this.setVisible(true);
    }//GEN-LAST:event_naukaJDialogWindowClosing

//startButton, rozpoczyna lekcje
    private void startJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_startJButtonActionPerformed
    {//GEN-HEADEREND:event_startJButtonActionPerformed
        // TODO add your handling code here:
        
        try
        {
            wylosowanaKarta = this.wybranaTalia.LosujKarte();
            lekcja = new Lekcja((Double.valueOf(this.dlugoscLekcjiTextField.getText())) * 60);
            this.watekLekcji = new Thread(this);
            this.watekLekcji.start();
            
            this.naukaNoweJLabel.setText(this.wybranaTalia.GetIloscKart()+"");
            this.dobrzeJButton.setEnabled(false);
            this.zleJButton.setEnabled(false);
            this.naukaPrzodJLabel.setText(wylosowanaKarta.GetEtykieta(0) + "");
            this.naukaJDialog.pack();
            this.rozpocznijNaukeJDialog.dispose();
            this.setVisible(false);
            this.naukaJDialog.setVisible(true);
        }
        catch (NumberFormatException ex)
        {
            System.out.println("Wyjatek : " + ex);
            return;
        }
        catch (IllegalArgumentException ex)
        {
            System.out.println("Wyjatek : "+ex);
            JOptionPane.showMessageDialog(null,"Umiesz już wszystkie slowka z tej stalii",  "Info", JOptionPane.INFORMATION_MESSAGE);
            this.rozpocznijNaukeJDialog.dispose();
            return;
        }
    }//GEN-LAST:event_startJButtonActionPerformed


    private void dobrzeJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_dobrzeJButtonActionPerformed
    {//GEN-HEADEREND:event_dobrzeJButtonActionPerformed
        // TODO add your handling code here:
        wylosowanaKarta = this.wybranaTalia.LosujKarte();
        this.wybranaTalia.setNauczone(wybranaTalia.getNauczone()+1);
        this.naukaNauczoneJLabel.setText(""+this.wybranaTalia.getNauczone());
        this.wybranaTalia.GetKartyDoNauki().remove(wylosowanaKarta);
        
        this.naukaNoweJLabel.setText(Integer.valueOf(this.naukaNoweJLabel.getText())-1+"");
        this.wybranaTalia.GetKartyDoNauki().remove(wylosowanaKarta);
        this.naukaPrzodJLabel.setText(wylosowanaKarta.GetEtykieta(0) + "");
        this.naukaTylJLabel.setText("...");
        this.pokazOdpowiedzJButton.setEnabled(true);
        this.dobrzeJButton.setEnabled(false);
        this.zleJButton.setEnabled(false);
        this.watekLekcji.interrupt();
        this.watekLekcji = new Thread(this);
        this.watekLekcji.start();
        
    }//GEN-LAST:event_dobrzeJButtonActionPerformed

    private void zleJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zleJButtonActionPerformed
    {//GEN-HEADEREND:event_zleJButtonActionPerformed
        // TODO add your handling code here:
        wylosowanaKarta = this.wybranaTalia.LosujKarte();
        this.naukaPrzodJLabel.setText(wylosowanaKarta.GetEtykieta(0) + "");
        this.naukaTylJLabel.setText("...");
        this.pokazOdpowiedzJButton.setEnabled(true);
        this.dobrzeJButton.setEnabled(false);
        this.zleJButton.setEnabled(false);
        this.watekLekcji.interrupt();
        this.watekLekcji = new Thread(this);
        this.watekLekcji.start();
    }//GEN-LAST:event_zleJButtonActionPerformed

    private void ZamknijJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ZamknijJButtonActionPerformed
    {//GEN-HEADEREND:event_ZamknijJButtonActionPerformed
        // TODO add your handling code here:
        this.przegladajJDialog.dispose();
    }//GEN-LAST:event_ZamknijJButtonActionPerformed

    private void listaKartJListMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_listaKartJListMouseClicked
    {//GEN-HEADEREND:event_listaKartJListMouseClicked
        // TODO add your handling code here:
        PrzegladajKarte();
    }//GEN-LAST:event_listaKartJListMouseClicked

    private void UsunJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_UsunJButtonActionPerformed
    {//GEN-HEADEREND:event_UsunJButtonActionPerformed
        // TODO add your handling code here:
        PrzegladajUsunKarte();
    }//GEN-LAST:event_UsunJButtonActionPerformed

    private void ZapiszZmianyJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ZapiszZmianyJButtonActionPerformed
    {//GEN-HEADEREND:event_ZapiszZmianyJButtonActionPerformed
        // TODO add your handling code here:
        PrzegladajZapiszZmiany();
    }//GEN-LAST:event_ZapiszZmianyJButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        // TODO add your handling code here:
        String nazwa;
        boolean nadpisany = false;
        File katalog = new File(".\\Talie");
        ArrayList<File> pliki = new ArrayList<>();
        for(File f : katalog.listFiles())
            pliki.add(f);
        if(listaTalii.GetListaTalii().size() == 0)
        {
            for(File f : pliki)
                f.delete();
        }
        else
        {
            for(File f : pliki)
            {
                nazwa = f.getName().substring(0, f.getName().length() - 4);
                for(Talia t : listaTalii.GetListaTalii())
                {
                    if(nazwa.equals(t.GetNazwaTalii()))
                    {
                        f.delete();
                        t.EksportTalii();
                        nadpisany = true;
                    }
                    else
                    {
                        nadpisany = false;
                    }
                    if(nadpisany == true)
                        break;
                    else
                        f.delete();
                }
            }

        }
    }//GEN-LAST:event_formWindowClosing

    private void WyslijJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_WyslijJButtonActionPerformed
    {//GEN-HEADEREND:event_WyslijJButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_WyslijJButtonActionPerformed

    private void pokazOdpowiedzJButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pokazOdpowiedzJButtonActionPerformed
    {//GEN-HEADEREND:event_pokazOdpowiedzJButtonActionPerformed
        // TODO add your handling code here:
        this.licznikKart++;
        this.naukaTylJLabel.setText(this.wylosowanaKarta.GetEtykieta(1) + "");
        this.pokazOdpowiedzJButton.setEnabled(false);
        this.czasOdpowiedziTextField.setText(this.lekcja.GetCzasOdpowiedzi() + "");
        this.dobrzeJButton.setEnabled(true);
        this.zleJButton.setEnabled(true);
        this.lekcja.SetCzasOdpowiedzi(0);
        this.watekLekcji.interrupt();
        this.watekLekcji = new Thread(this);
        this.watekLekcji.start();
    }//GEN-LAST:event_pokazOdpowiedzJButtonActionPerformed

    private void zakonczButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zakonczButtonActionPerformed
    {//GEN-HEADEREND:event_zakonczButtonActionPerformed
        // TODO add your handling code here:
        this.KoniecLekcji();
        this.watekLekcji.interrupt();
    }//GEN-LAST:event_zakonczButtonActionPerformed


    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea ChatJTextArea;
    private javax.swing.JTextField ChatJTextField;
    private javax.swing.JButton ChatOnlineJButton;
    private javax.swing.JButton DodajJButton;
    private javax.swing.JMenu EdytujJMenu;
    private javax.swing.JMenuItem EksportujJMenuItem;
    private javax.swing.JMenuItem ImportujJMenuItem;
    private javax.swing.JButton ImportujPlikJButton;
    private javax.swing.JList<String> ListaTaliiJList;
    private javax.swing.JScrollPane ListaTaliiJScrollPane;
    private javax.swing.JMenuBar MenuGorneJMenuBar;
    private javax.swing.JMenuItem OProgramieJMenuItem;
    private javax.swing.JMenu PlikJMenu;
    private javax.swing.JMenu PomocJMenu;
    private javax.swing.JMenuItem PordanikJMenuItem;
    private javax.swing.JButton PrzegladajTaliieJButton;
    private javax.swing.JLabel Przód;
    private javax.swing.JButton StatystykiJButton;
    private javax.swing.JLabel StatystykiJLabel;
    private javax.swing.JLabel TalieJLabel;
    private javax.swing.JButton UsunJButton;
    private javax.swing.JMenuItem UsunJMenuItem;
    private javax.swing.JButton UtworzTalieJButton;
    private javax.swing.JButton WyslijJButton;
    private javax.swing.JMenuItem ZakonczJMenuItem;
    private javax.swing.JButton ZamknijJButton;
    private javax.swing.JButton ZapiszZmianyJButton;
    private javax.swing.JMenuItem ZmienNazweJMenuItem;
    private javax.swing.JDialog chatJDialog;
    private javax.swing.JTextField czasDoKoncaTextField;
    private javax.swing.JTextField czasOdpowiedziTextField;
    private javax.swing.JTextField dlugoscLekcjiTextField;
    private javax.swing.JLabel doNaukuJLabel;
    private javax.swing.JButton dobrzeJButton;
    private javax.swing.JButton dodajButton;
    private javax.swing.JDialog dodajJDialog;
    private javax.swing.JComboBox<String> dodajTalieComboBox;
    private javax.swing.JLabel iloscKartJLabel;
    private javax.swing.JLabel iloscKartJLabel11;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JList<String> listaKartJList;
    private javax.swing.JLabel nauczoneJLabel;
    private javax.swing.JDialog naukaJDialog;
    private javax.swing.JLabel naukaNauczoneJLabel;
    private javax.swing.JLabel naukaNoweJLabel;
    private javax.swing.JLabel naukaPrzodJLabel;
    private javax.swing.JLabel naukaTylJLabel;
    private javax.swing.JLabel nazwaTaliiJLabel;
    private javax.swing.JLabel nazwaTaliiJLabel1;
    private javax.swing.JLabel noweJLabel;
    private javax.swing.JButton pokazOdpowiedzJButton;
    private javax.swing.JDialog poradnikJDialog;
    private javax.swing.JDialog przegladajJDialog;
    private javax.swing.JTextField przegladajPrzodJTextField;
    private javax.swing.JTextField przegladajTylJTextField;
    private javax.swing.JTextField przodTextField;
    private javax.swing.JButton rozpocznijNaukeJButton;
    private javax.swing.JDialog rozpocznijNaukeJDialog;
    private javax.swing.JLabel rozpocznijNaukeNauczoneJLabel;
    private javax.swing.JLabel rozpocznijNaukeNazwaTalii2;
    private javax.swing.JButton startJButton;
    private javax.swing.JButton talieJButton;
    private javax.swing.JTextField tylTextField;
    private javax.swing.JButton utworzTalieAnulujJButton;
    private javax.swing.JDialog utworzTalieJDialog;
    private javax.swing.JTextField utworzTalieNazwaTaliiTextField;
    private javax.swing.JButton utworzTalieOkJButton;
    private javax.swing.JButton zakonczButton;
    private javax.swing.JButton zamknijButton;
    private javax.swing.JButton zamknijPoradnikButton;
    private javax.swing.JButton zleJButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run()
    {
        BigDecimal odpowiedzTimer;
        while (lekcja.GetDlugoscLekcji() > 0 && !this.wybranaTalia.GetKartyDoNauki().isEmpty())
        {
            this.lekcja.SetCzasOdpowiedzi(this.lekcja.GetCzasOdpowiedzi() + 0.01);
            this.lekcja.SetDlugoscLekcji(this.lekcja.GetDlugoscLekcji() - 0.01);
            this.czasDoKoncaTextField.setText(Math.round(this.lekcja.GetDlugoscLekcji()) + "");
            this.lekcja.SetSumaCzasuOdpowiedzi(0.01);
            odpowiedzTimer = BigDecimal.valueOf(Double.valueOf(this.lekcja.GetCzasOdpowiedzi()));
            odpowiedzTimer = odpowiedzTimer.setScale(2, RoundingMode.HALF_UP);
            try
            {
                this.lekcja.SetCzasOdpowiedzi(Double.valueOf(odpowiedzTimer.toString()));
                
                Thread.sleep(10);
            } catch (InterruptedException e)
            {
                return;
            }
        }
        this.KoniecLekcji();
    }
    
}
