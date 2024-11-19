import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;


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
    private JLabel lblHorasTotales;
    private JLabel lbTotalHorasTexto;
    private JLabel lbTotalHorasValor;


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
        labelBienvenida.setVisible(false);
    
        // Inicializar paneles principales
        inicializarPanelLogin();
        inicializarPanelPrincipal();
        
        // Crear paneles temporales para perfil y eventos
        JPanel panelPerfilTemp = new JPanel();
        JPanel panelEventosTemp = new JPanel();
    
        // Agregar todas las pestañas al TabbedPane con paneles temporales
        tabbedPane.addTab("Inicio de Sesión", crearPanelConPadding(panelLogin));
        tabbedPane.addTab("Sistema de Horas", crearPanelConPadding(panelPrincipal));
        tabbedPane.addTab("Perfil de Usuario", panelPerfilTemp);
        tabbedPane.addTab("Eventos", panelEventosTemp);
    
        // Inicializar el contenido real de los paneles
        inicializarPanelPerfil();
        crearPanelEventos();
        
        agregarLogotipo();

        // Deshabilitar pestañas hasta el login
        tabbedPane.setEnabledAt(1, false);
        tabbedPane.setEnabledAt(2, false); 
        tabbedPane.setEnabledAt(3, false); 
        
        add(labelBienvenida, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    private void agregarLogotipo() {
        // Cargar la imagen
        ImageIcon icono = new ImageIcon("imagenes/logo.png"); // Asegúrate de que la ruta sea correcta
    
        // Crear un JLabel con el ImageIcon
        JLabel logotipoLabel = new JLabel(icono);
    
        // Ajustar tamaño si es necesario
        Image imagen = icono.getImage(); // Convertir la imagen a un objeto Image
        Image imagenEscalada = imagen.getScaledInstance(150, 150, Image.SCALE_SMOOTH); // Redimensionar a 150x150 píxeles
        icono = new ImageIcon(imagenEscalada);
        logotipoLabel.setIcon(icono);
    
        // Añadir el JLabel al panel de bienvenida o al panel donde desees mostrar el logotipo
        panelLogin.add(logotipoLabel, BorderLayout.NORTH); // En este caso, lo agregamos al panel de login
    
        // Si lo deseas en otro panel, por ejemplo en el panel principal:
        // panelPrincipal.add(logotipoLabel, BorderLayout.NORTH);
    }
    
    private void crearPanelEventos() {
    JPanel panelEventos = new JPanel(new BorderLayout(10, 10));
    panelEventos.setBackground(COLOR_FONDO);
    panelEventos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Panel superior para título
    JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
    panelTitulo.setBackground(COLOR_FONDO);
    JLabel titulo = new JLabel("Catálogo de Eventos Disponibles");
    titulo.setFont(new Font("Arial", Font.BOLD, 20));
    titulo.setForeground(COLOR_NARANJA);
    panelTitulo.add(titulo);

    // Crear los datos y las columnas para el JTable
    String[] columnNames = {"Nombre del Evento", "Horas Otorgadas", "Horario Disponible", "Ubicación", "Cupos"};
    DefaultTableModel modelEventos = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Hace que la tabla no sea editable
        }
    };

    // Agregar datos de ejemplo (estos pueden ser cargados desde una base de datos)
    Object[][] eventosData = {
        {"Voluntariado Hospital", 4, "Lunes a Viernes 8:00 AM - 12:00 PM", "Fosunab", "20"},
        {"Tutoría Académica", 2, "Martes y Jueves 2:00 PM - 4:00 PM", "Biblioteca ", "15"},
        {"Apoyo Administrativo", 3, "Miércoles 9:00 AM - 12:00 PM", "Auditorio Mayor", "10"},
        {"Proyecto Social", 5, "Sábados 8:00 AM - 1:00 PM", "CSU", "25"},
        {"Investigación", 6, "Flexible", "Laboratorios UNAB", "8"},
        {"Mentor Estudiantil", 3, "Lunes y Miércoles 3:00 PM - 6:00 PM", "Salón de Estudios", "12"},
        {"Apoyo Deportivo", 2, "Viernes 2:00 PM - 4:00 PM", "CSU", "15"},
        {"Biblioteca", 4, "Lunes a Viernes 1:00 PM - 5:00 PM", "Biblioteca ", "6"}
    };

    // Agregar los datos a la tabla
    for (Object[] evento : eventosData) {
        modelEventos.addRow(evento);
    }

    // Crear y configurar la tabla
    JTable tablaEventos = new JTable(modelEventos);
    tablaEventos.setRowHeight(25);
    tablaEventos.setFont(new Font("Arial", Font.PLAIN, 14));
    tablaEventos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    tablaEventos.getTableHeader().setBackground(COLOR_NARANJA);
    tablaEventos.getTableHeader().setForeground(Color.BLACK);
    tablaEventos.setSelectionBackground(COLOR_NARANJA_CLARO);
    tablaEventos.setGridColor(Color.GRAY);
    tablaEventos.setShowGrid(true);

    // Configurar el ancho de las columnas
    tablaEventos.getColumnModel().getColumn(0).setPreferredWidth(150); // Nombre
    tablaEventos.getColumnModel().getColumn(1).setPreferredWidth(100); // Horas
    tablaEventos.getColumnModel().getColumn(2).setPreferredWidth(200); // Horario
    tablaEventos.getColumnModel().getColumn(3).setPreferredWidth(150); // Ubicación
    tablaEventos.getColumnModel().getColumn(4).setPreferredWidth(100); // Cupos

    // Crear panel de búsqueda
    JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelBusqueda.setBackground(COLOR_FONDO);
    JLabel lblBuscar = new JLabel("Buscar evento: ");
    lblBuscar.setFont(new Font("Arial", Font.BOLD, 14));
    JTextField txtBuscar = new JTextField(20);
    styleTextField(txtBuscar);
    
    JButton btnBuscar = new JButton("Buscar");
    styleButton(btnBuscar);
    
    panelBusqueda.add(lblBuscar);
    panelBusqueda.add(txtBuscar);
    panelBusqueda.add(btnBuscar);

    // Agregar funcionalidad de búsqueda
    txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
        public void changedUpdate(DocumentEvent e) { filtrarTabla(); }
        public void removeUpdate(DocumentEvent e) { filtrarTabla(); }
        public void insertUpdate(DocumentEvent e) { filtrarTabla(); }

        private void filtrarTabla() {
            String texto = txtBuscar.getText().toLowerCase();
            DefaultTableModel model = (DefaultTableModel) tablaEventos.getModel();
            model.setRowCount(0); // Limpiar tabla

            for (Object[] evento : eventosData) {
                if (evento[0].toString().toLowerCase().contains(texto) ||
                    evento[2].toString().toLowerCase().contains(texto) ||
                    evento[3].toString().toLowerCase().contains(texto)) {
                    model.addRow(evento);
                }
            }
        }
    });

    // Agregar todo al panel principal
    JScrollPane scrollPane = new JScrollPane(tablaEventos);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    panelEventos.add(panelTitulo, BorderLayout.NORTH);
    panelEventos.add(panelBusqueda, BorderLayout.CENTER);
    panelEventos.add(scrollPane, BorderLayout.SOUTH);

    // Actualizar la pestaña de eventos en el TabbedPane
    tabbedPane.setComponentAt(3, panelEventos);
}


    
    private void mostrarEventos(JTextArea areaEventos) {
        areaEventos.setText("");  // Limpiar el área de texto
        for (Map.Entry<String, Integer> evento : eventosConocidos.entrySet()) {
            areaEventos.append(String.format("Evento: %s - %d horas\n", evento.getKey(), evento.getValue()));
        }
    }
private void agregarNuevoEvento(JTextField campoNombreEvento, JTextArea areaDescripcionEvento, DefaultTableModel modelEventos) {
    String nombreEvento = campoNombreEvento.getText().trim();
    String descripcionEvento = areaDescripcionEvento.getText().trim();

    if (!nombreEvento.isEmpty() && !descripcionEvento.isEmpty() && !eventosConocidos.containsKey(nombreEvento)) {
        eventosConocidos.put(nombreEvento, 0);  // Aquí agregas el evento al mapa
        modelEventos.addRow(new Object[]{nombreEvento, descripcionEvento, 0});
        campoNombreEvento.setText("");  // Limpiar el campo de nombre
        areaDescripcionEvento.setText("");  // Limpiar la descripción
    } else {
        JOptionPane.showMessageDialog(this, "El evento o la descripción está vacío o ya existe", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    private void crearPanelPerfil() {
    // Comprobamos que el usuario esté logueado antes de mostrar el perfil
    if (nombreActual == null) return;

    JPanel panelPerfil = new JPanel();
    panelPerfil.setLayout(new GridLayout(4, 2));

    JLabel lblNombre = new JLabel("Nombre:");
    JLabel lblCarrera = new JLabel("Carrera:");
    JLabel lblSemestre = new JLabel("Semestre:");
    JLabel lblHorasLibres = new JLabel("Horas Libres:");

    JLabel lblNombreValor = new JLabel(nombreActual);
    JLabel lblCarreraValor = new JLabel((String) comboCarrera.getSelectedItem());
    JLabel lblSemestreValor = new JLabel(String.valueOf(comboSemestres.getSelectedItem()));
    JLabel lblHorasLibresValor = new JLabel(String.valueOf(horasPorUsuario.getOrDefault(nombreActual, 0)));

    panelPerfil.add(lblNombre);
    panelPerfil.add(lblNombreValor);
    panelPerfil.add(lblCarrera);
    panelPerfil.add(lblCarreraValor);
    panelPerfil.add(lblSemestre);
    panelPerfil.add(lblSemestreValor);
    panelPerfil.add(lblHorasLibres);
    panelPerfil.add(lblHorasLibresValor);

    // Añadir el panel de perfil al panel principal de la pestaña
    tabbedPane.setComponentAt(2, panelPerfil); // La pestaña "Perfil de Usuario" es la 3ra
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
        
        // Panel para mostrar horas totales
        JPanel panelHorasTotales = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelHorasTotales.setBackground(COLOR_FONDO);
        
        // Inicializar las etiquetas
        lbTotalHorasTexto = new JLabel("Total Horas Acumuladas: ");
        lbTotalHorasTexto.setFont(new Font("Arial", Font.BOLD, 16));
        lbTotalHorasTexto.setForeground(COLOR_NARANJA);
        
        lbTotalHorasValor = new JLabel("0");
        lbTotalHorasValor.setFont(new Font("Arial", Font.BOLD, 16));
        lbTotalHorasValor.setForeground(Color.BLACK);
        
        // Panel con borde para el contador
        JPanel panelContadorHoras = new JPanel();
        panelContadorHoras.setBackground(Color.WHITE);
        panelContadorHoras.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_NARANJA, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        panelContadorHoras.add(lbTotalHorasTexto);
        panelContadorHoras.add(lbTotalHorasValor);
        panelHorasTotales.add(panelContadorHoras);

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
         
        // Crear el JLabel para mostrar las horas totales registradas
        lblHorasTotales = new JLabel("Horas Libres: 0");
        lblHorasTotales.setFont(new Font("Arial", Font.PLAIN, 14));
        panelPrincipal.add(lblHorasTotales, BorderLayout.SOUTH);

         // Títulos de las secciones
        JLabel labelBio = new JLabel("Biográfica");
        labelBio.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 16));
        labelBio.setForeground(COLOR_NARANJA);

        JLabel labelGeneral = new JLabel("General");
        labelGeneral.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 16));
        labelGeneral.setForeground(COLOR_NARANJA);

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
        panelNorte.add(panelHorasTotales, BorderLayout.NORTH);
        panelNorte.add(panelEntrada, BorderLayout.CENTER);
        panelNorte.add(panelBotones, BorderLayout.SOUTH);

        panelPrincipal.add(panelNorte, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        panelPrincipal.add(panelPromedios, BorderLayout.SOUTH);

        actualizarHorasTotales();

        // Agregar listeners
        botonAgregarEvento.addActionListener(this);
        botonRegistrar.addActionListener(this);
        botonExportar.addActionListener(this);
        botonVerificar.addActionListener(this);
        botonPromedioDia.addActionListener(this);
        botonPromedioMes.addActionListener(this);
        botonPromedioAnio.addActionListener(this);
    }
    private void actualizarHorasTotales() {
    int horasTotales = 0;
    for (Integer horas : horasPorUsuario.values()) {
        horasTotales += horas;
    }
    lbTotalHorasValor.setText(String.valueOf(horasTotales));
}
    private void inicializarPanelPerfil() {
        JPanel panelPerfil = new JPanel(new GridBagLayout());
        panelPerfil.setBackground(COLOR_FONDO);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
    
        // Sección Biográfica
        JLabel tituloBiografica = new JLabel("Información Biográfica");
        tituloBiografica.setFont(new Font("Arial", Font.BOLD, 16));
        tituloBiografica.setForeground(COLOR_NARANJA);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelPerfil.add(tituloBiografica, gbc);
    
        // Campos Biográficos
        String[][] camposBiograficos = {
            {"Correo:", "usuario@email.com"},
            {"Fecha de Nacimiento:", "01/01/2000"},
            {"Teléfono:", "+57 123456789"},
            {"Ciudadanía:", "Colombiana"}
        };
    
        gbc.gridwidth = 1;
        int row = 1;
        for (String[] campo : camposBiograficos) {
            // Label
            JLabel label = new JLabel(campo[0]);
            styleLabel(label);
            gbc.gridx = 0;
            gbc.gridy = row;
            panelPerfil.add(label, gbc);
    
            // Valor
            JLabel valor = new JLabel(campo[1]);
            valor.setFont(new Font("Arial", Font.PLAIN, 14));
            gbc.gridx = 1;
            panelPerfil.add(valor, gbc);
            row++;
        }
        
        
        JPanel PanelPerfil = new JPanel(new GridBagLayout());

        // Separador
        JSeparator separador = new JSeparator();
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelPerfil.add(separador, gbc);
    
        // Sección General
        JLabel tituloGeneral = new JLabel("Información General");
        tituloGeneral.setFont(new Font("Arial", Font.BOLD, 16));
        tituloGeneral.setForeground(COLOR_NARANJA);
        gbc.gridy = row++;
        panelPerfil.add(tituloGeneral, gbc);
    
        // Campos Generales
        String[][] camposGenerales = {
            {"Nivel:", "Estudiante"},
            {"Clase:", comboCarrera.getSelectedItem() != null ? comboCarrera.getSelectedItem().toString() : "No seleccionado"},
            {"Estatus:", "Activo"}
        };
    
        gbc.gridwidth = 1;
        for (String[] campo : camposGenerales) {
            // Label
            JLabel label = new JLabel(campo[0]);
            styleLabel(label);
            gbc.gridx = 0;
            gbc.gridy = row;
            panelPerfil.add(label, gbc);
    
            // Valor
            JLabel valor = new JLabel(campo[1]);
            valor.setFont(new Font("Arial", Font.PLAIN, 14));
            gbc.gridx = 1;
            panelPerfil.add(valor, gbc);
            row++;
        }
    
        // Añadir el panel a un JScrollPane para permitir scroll si es necesario
        JScrollPane scrollPane = new JScrollPane(panelPerfil);
        scrollPane.setBackground(COLOR_FONDO);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    
        // Actualizar la pestaña de perfil en el TabbedPane
        tabbedPane.setComponentAt(2, scrollPane);
        
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
            tabbedPane.setEnabledAt(2, true); 
            tabbedPane.setEnabledAt(3, true); 
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
            if (nombreActual != null) {
                int horasActuales = horasPorUsuario.getOrDefault(nombreActual, 0);
                horasPorUsuario.put(nombreActual, horasActuales + horas);
                
                // Actualizar el contador
                actualizarHorasTotales();
                
                campoHorasLibres.setText("");
                JOptionPane.showMessageDialog(this, 
                    "Horas registradas correctamente.\nTotal de horas: " + (horasActuales + horas), 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese un número válido", 
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
