import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;

public class HorasLibresUNABApp extends JFrame implements ActionListener {
    private JTextField campoHorasLibres, campoNuevoEvento, campoUsuario;
    private JPasswordField campoContrasena;
    private JComboBox<String> comboEvento, comboCarrera;
    private JComboBox<Integer> comboSemestres;
    private JButton botonRegistrar, botonExportar, botonVerificar, botonAgregarEvento;
    private JButton botonPromedioDia, botonPromedioMes, botonPromedioAnio;
    private JButton botonIniciarSesion, botonRegistrarse;
    private JTextArea areaResultado;
    private HashMap<String, Integer> eventosConocidos;
    private HashMap<String, String> usuariosConocidos;
    private HashMap<String, Integer> horasPorUsuario;               
    private String nombreActual;
    private JTabbedPane tabbedPane;
    private JPanel panelPrincipal, panelLogin;
    private JLabel labelBienvenida;

    // Colores personalizados
    private static final Color COLOR_NARANJA = new Color(255, 140, 0);
    private static final Color COLOR_NARANJA_CLARO = new Color(255, 178, 102);
    private static final Color COLOR_FONDO = new Color(255, 250, 240);
    private static final Color COLOR_TEXTO = new Color(0,0,0);

    private static final String ARCHIVO_USUARIOS = "usuarios.txt";
    private static final String ARCHIVO_EVENTOS = "eventos.txt";
    private static final String ARCHIVO_HORAS = "horas.txt";

    public HorasLibresUNABApp() {
        setTitle("Sistema de Horas Libres UNAB");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(COLOR_FONDO);

        eventosConocidos = new HashMap<>();
        usuariosConocidos = new HashMap<>();
        horasPorUsuario = new HashMap<>();

        cargarDatos();
        inicializarComponentes();
        configurarMenuBar();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void cargarDatos() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_EVENTOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    eventosConocidos.put(partes[0], Integer.parseInt(partes[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar los eventos: " + e.getMessage());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    usuariosConocidos.put(partes[0], partes[1]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar los usuarios: " + e.getMessage());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_HORAS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    horasPorUsuario.put(partes[0], Integer.parseInt(partes[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar las horas: " + e.getMessage());
        }
    }

    private void inicializarComponentes() {
        // Inicializar TabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(COLOR_FONDO);
        tabbedPane.setForeground(COLOR_TEXTO);

        // Panel de bienvenida
        labelBienvenida = new JLabel("Bienvenido al Sistema de Horas Libres UNAB", SwingConstants.CENTER);
        labelBienvenida.setFont(new Font("Arial", Font.BOLD, 18));
        labelBienvenida.setForeground(COLOR_NARANJA);
        labelBienvenida.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(labelBienvenida, BorderLayout.NORTH);
        labelBienvenida.setVisible(false);

        // Inicializar paneles
        inicializarPanelLogin();
        inicializarPanelPrincipal();

        // Agregar paneles al TabbedPane
        tabbedPane.addTab("Inicio de Sesión", crearPanelConPadding(panelLogin));
        tabbedPane.addTab("Sistema de Horas", crearPanelConPadding(panelPrincipal));
        
        // Deshabilitar la pestaña del sistema hasta que se inicie sesión
        tabbedPane.setEnabledAt(1, false);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void inicializarPanelLogin() {
        panelLogin = new JPanel();
        panelLogin.setLayout(new BoxLayout(panelLogin, BoxLayout.Y_AXIS));
        panelLogin.setBackground(COLOR_FONDO);

        // Panel para el formulario de login
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Estilo para labels
        JLabel titleLabel = new JLabel("Acceso al Sistema", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        
        JLabel userLabel = new JLabel("Usuario:");
        JLabel passLabel = new JLabel("Contraseña:");
        styleLabel(userLabel);
        styleLabel(passLabel);

        // Campos de texto
        campoUsuario = new JTextField(20);
        campoContrasena = new JPasswordField(20);
        styleTextField(campoUsuario);
        styleTextField(campoContrasena);

        // Botones
        botonIniciarSesion = new JButton("Iniciar Sesión");
        botonRegistrarse = new JButton("Registrarse");
        styleButton(botonIniciarSesion);
        styleButton(botonRegistrarse);

        // Agregar componentes con GridBagLayout
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        formPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(campoUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(campoContrasena, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(COLOR_FONDO);
        buttonPanel.add(botonIniciarSesion);
        buttonPanel.add(botonRegistrarse);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Agregar el formPanel al centro del panelLogin
        panelLogin.add(Box.createVerticalGlue());
        panelLogin.add(formPanel);
        panelLogin.add(Box.createVerticalGlue());

        // Agregar listeners
        botonIniciarSesion.addActionListener(this);
        botonRegistrarse.addActionListener(this);
    }

    private void inicializarPanelPrincipal() {
        panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(COLOR_FONDO);

        // Panel para los campos de entrada
        JPanel panelEntrada = new JPanel(new GridBagLayout());
        panelEntrada.setBackground(COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Inicializar componentes
        JLabel lblHoras = new JLabel("Horas Libres:");
        JLabel lblEvento = new JLabel("Evento:");
        JLabel lblNuevoEvento = new JLabel("Nuevo Evento:");
        JLabel lblCarrera = new JLabel("Carrera:");
        JLabel lblSemestre = new JLabel("Semestre:");

        styleLabel(lblHoras);
        styleLabel(lblEvento);
        styleLabel(lblNuevoEvento);
        styleLabel(lblCarrera);
        styleLabel(lblSemestre);

        campoHorasLibres = new JTextField(10);
        campoNuevoEvento = new JTextField(15);
        comboEvento = new JComboBox<>(eventosConocidos.keySet().toArray(new String[0]));
        comboCarrera = new JComboBox<>(new String[]{"Ing. de Sistemas", "Derecho", "Biomedica", "Ing. Industrial", "Otra"});
        comboSemestres = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

        styleTextField(campoHorasLibres);
        styleTextField(campoNuevoEvento);
        styleComboBox(comboEvento);
        styleComboBox(comboCarrera);
        styleComboBox(comboSemestres);

        // Botones
        botonAgregarEvento = new JButton("Agregar Evento");
        botonRegistrar = new JButton("Registrar Horas");
        botonExportar = new JButton("Exportar Datos");
        botonVerificar = new JButton("Verificar Graduación");
        botonPromedioDia = new JButton("Promedio Diario");
        botonPromedioMes = new JButton("Promedio Mensual");
        botonPromedioAnio = new JButton("Promedio Anual");

        styleButton(botonAgregarEvento);
        styleButton(botonRegistrar);
        styleButton(botonExportar);
        styleButton(botonVerificar);
        styleButton(botonPromedioDia);
        styleButton(botonPromedioMes);
        styleButton(botonPromedioAnio);

        // Agregar componentes con GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0;
        panelEntrada.add(lblHoras, gbc);
        gbc.gridx = 1;
        panelEntrada.add(campoHorasLibres, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelEntrada.add(lblEvento, gbc);
        gbc.gridx = 1;
        panelEntrada.add(comboEvento, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelEntrada.add(lblNuevoEvento, gbc);
        gbc.gridx = 1;
        panelEntrada.add(campoNuevoEvento, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelEntrada.add(lblCarrera, gbc);
        gbc.gridx = 1;
        panelEntrada.add(comboCarrera, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panelEntrada.add(lblSemestre, gbc);
        gbc.gridx = 1;
        panelEntrada.add(comboSemestres, gbc);

        // Panel para botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBackground(COLOR_FONDO);
        panelBotones.add(botonAgregarEvento);
        panelBotones.add(botonRegistrar);
        panelBotones.add(botonExportar);
        panelBotones.add(botonVerificar);

        JPanel panelPromedios = new JPanel(new FlowLayout());
        panelPromedios.setBackground(COLOR_FONDO);
        panelPromedios.add(botonPromedioDia);
        panelPromedios.add(botonPromedioMes);
        panelPromedios.add(botonPromedioAnio);

        // Área de resultados
        areaResultado = new JTextArea();
        areaResultado.setEditable(false);
        areaResultado.setBackground(COLOR_FONDO);
        areaResultado.setForeground(COLOR_TEXTO);
        areaResultado.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(areaResultado);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Agregar todo al panel principal
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(COLOR_FONDO);
        panelNorte.add(panelEntrada, BorderLayout.CENTER);
        panelNorte.add(panelBotones, BorderLayout.SOUTH);

        panelPrincipal.add(panelNorte, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        panelPrincipal.add(panelPromedios, BorderLayout.SOUTH);

        // Agregar listeners
        botonAgregarEvento.addActionListener(this);
        botonRegistrar.addActionListener(this);
        botonExportar.addActionListener(this);
        botonVerificar.addActionListener(this);
        botonPromedioDia.addActionListener(this);
        botonPromedioMes.addActionListener(this);
        botonPromedioAnio.addActionListener(this);
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(COLOR_TEXTO);
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_NARANJA),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(COLOR_NARANJA);
        button.setForeground(Color.black);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(COLOR_NARANJA_CLARO);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(COLOR_NARANJA);
            }
        });
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(COLOR_TEXTO);
    }

    private JPanel crearPanelConPadding(JPanel panel) {
        JPanel paddingPanel = new JPanel(new BorderLayout());
        paddingPanel.setBackground(COLOR_FONDO);
        paddingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        paddingPanel.add(panel, BorderLayout.CENTER);
        return paddingPanel;
    }

    private void configurarMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenu menuAyuda = new JMenu("Ayuda");

        JMenuItem menuItemSalir = new JMenuItem("Salir");
        JMenuItem menuItemAcerca = new JMenuItem("Acerca de");

        menuItemSalir.addActionListener(e -> System.exit(0));
        menuItemAcerca.addActionListener(e -> mostrarAcercaDe());

        menuArchivo.add(menuItemSalir);
        menuAyuda.add(menuItemAcerca);

        menuBar.add(menuArchivo);
        menuBar.add(menuAyuda);

        setJMenuBar(menuBar);
    }

    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this,
            "Sistema de Horas Libres UNAB\nVersión 1.0\n© 2024 UNAB",
            "Acerca de",
            JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botonIniciarSesion) {
            iniciarSesion();
        } else if (e.getSource() == botonRegistrarse) {
            registrarUsuario();
        } else if (e.getSource() == botonAgregarEvento) {
            agregarNuevoEvento();
        } else if (e.getSource() == botonRegistrar) {
            registrarHoras();
        } else if (e.getSource() == botonExportar) {
            exportarDatos();
        } else if (e.getSource() == botonVerificar) {
            verificarGraduacion();
        } else if (e.getSource() == botonPromedioDia) {
            calcularPromedio("día");
        } else if (e.getSource() == botonPromedioMes) {
            calcularPromedio("mes");
        } else if (e.getSource() == botonPromedioAnio) {
            calcularPromedio("año");
        }
    }

    private void iniciarSesion() {
        String usuario = campoUsuario.getText();
        String contrasena = new String(campoContrasena.getPassword());
        
        if (usuariosConocidos.containsKey(usuario) && 
            usuariosConocidos.get(usuario).equals(contrasena)) {
            nombreActual = usuario;
            tabbedPane.setEnabledAt(1, true);
            tabbedPane.setSelectedIndex(1);
            labelBienvenida.setText("Bienvenid@, " + usuario);
            labelBienvenida.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                "Usuario o contraseña incorrectos",
                "Error de inicio de sesión",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarUsuario() {
        String usuario = campoUsuario.getText();
        String contrasena = new String(campoContrasena.getPassword());
        
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor complete todos los campos",
                "Error de registro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (usuariosConocidos.containsKey(usuario)) {
            JOptionPane.showMessageDialog(this,
                "El usuario ya existe",
                "Error de registro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        usuariosConocidos.put(usuario, contrasena);
        horasPorUsuario.put(usuario, 0);
        guardarDatos();
        
        JOptionPane.showMessageDialog(this,
            "Usuario registrado exitosamente",
            "Registro exitoso",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void agregarNuevoEvento() {
        String nuevoEvento = campoNuevoEvento.getText().trim();
        if (!nuevoEvento.isEmpty()) {
            if (!eventosConocidos.containsKey(nuevoEvento)) {
                eventosConocidos.put(nuevoEvento, 1);
                comboEvento.addItem(nuevoEvento);
                guardarDatos();
                campoNuevoEvento.setText("");
                JOptionPane.showMessageDialog(this,
                    "Evento agregado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "El evento ya existe",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void registrarHoras() {
        try {
            int horas = Integer.parseInt(campoHorasLibres.getText());
            String evento = (String) comboEvento.getSelectedItem();
            
            if (nombreActual != null && evento != null) {
                int horasActuales = horasPorUsuario.getOrDefault(nombreActual, 0);
                horasPorUsuario.put(nombreActual, horasActuales + horas);
                guardarDatos();
                
                areaResultado.append(String.format(
                    "Registradas %d horas para el evento '%s'\n",
                    horas, evento));
                campoHorasLibres.setText("");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Por favor ingrese un número válido de horas",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarDatos() {
        if (nombreActual == null) return;
        
        try (PrintWriter writer = new PrintWriter("reporte_" + nombreActual + ".txt")) {
            writer.println("Reporte de Horas Libres - " + nombreActual);
            writer.println("Total de horas: " + horasPorUsuario.get(nombreActual));
            writer.println("Carrera: " + comboCarrera.getSelectedItem());
            writer.println("Semestre: " + comboSemestres.getSelectedItem());
            
            JOptionPane.showMessageDialog(this,
                "Datos exportados exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error al exportar los datos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verificarGraduacion() {
        if (nombreActual == null) return;
        
        int horasTotal = horasPorUsuario.getOrDefault(nombreActual, 0);
        int horasRequeridas = 120; // Ejemplo de horas requeridas
        
        String mensaje = String.format(
            "Horas acumuladas: %d\nHoras requeridas: %d\n\n%s",
            horasTotal,
            horasRequeridas,
            horasTotal >= horasRequeridas ? 
                "¡Felicitaciones! Has completado las horas requeridas." :
                "Aún necesitas " + (horasRequeridas - horasTotal) + " horas más.");
        
        JOptionPane.showMessageDialog(this,
            mensaje,
            "Verificación de Graduación",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void calcularPromedio(String periodo) {
        if (nombreActual == null) return;
        
        int horasTotal = horasPorUsuario.getOrDefault(nombreActual, 0);
        double promedio = 0;
        
        switch (periodo) {
            case "día":
                promedio = horasTotal / 365.0;
                break;
            case "mes":
                promedio = horasTotal / 12.0;
                break;
            case "año":
                promedio = horasTotal;
                break;
        }
        
        areaResultado.append(String.format(
            "Promedio por %s: %.2f horas\n",
            periodo, promedio));
    }

    private void guardarDatos() {
        try (PrintWriter writer = new PrintWriter(ARCHIVO_USUARIOS)) {
            for (var entry : usuariosConocidos.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
        }

        try (PrintWriter writer = new PrintWriter(ARCHIVO_EVENTOS)) {
            for (var entry : eventosConocidos.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar eventos: " + e.getMessage());
        }

        try (PrintWriter writer = new PrintWriter(ARCHIVO_HORAS)) {
            for (var entry : horasPorUsuario.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar horas: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new HorasLibresUNABApp());
    }
}