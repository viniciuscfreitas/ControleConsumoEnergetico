import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ControleConsumoApp {
    private ControleConsumo controleConsumo;
    private JLabel labelMetaAtual;
    private static final String META_FILE_PATH = "meta.txt";
    private JComboBox<String> tipoGraficoComboBox;
    private JPanel panelGraficos;

    public ControleConsumoApp() {
        controleConsumo = new ControleConsumo();
        controleConsumo.carregarDados();
        criarInterface();
        carregarMeta();
        verificarMetaDefinida();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            salvarMeta();
        }));
    }

    private void criarInterface() {
        JFrame frame = new JFrame("Controle de Consumo Energético");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel panelPrincipal = criarPainelPrincipal();
        tabbedPane.addTab("Painel Principal", panelPrincipal);

        JPanel panelEstatisticas = criarPainelEstatisticas();
        tabbedPane.addTab("Estatísticas e Gráficos", panelEstatisticas);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel criarPainelPrincipal() {
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelPrincipal.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel headerLabel = new JLabel("Sistema de Controle de Consumo Energético", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setForeground(new Color(34, 45, 65));

        JPanel panelRegistro = criarPainelRegistro(gbc);
        JPanel panelMeta = criarPainelMeta(gbc);
        JPanel panelAcoes = criarPainelAcoes(gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelPrincipal.add(headerLabel, gbc);
        gbc.gridy = 1;
        panelPrincipal.add(panelRegistro, gbc);
        gbc.gridy = 2;
        panelPrincipal.add(panelMeta, gbc);
        gbc.gridy = 3;
        panelPrincipal.add(panelAcoes, gbc);

        return panelPrincipal;
    }

    private JPanel criarPainelRegistro(GridBagConstraints gbc) {
        JPanel panelRegistro = new JPanel(new GridBagLayout());
        panelRegistro.setBorder(BorderFactory.createTitledBorder("Registro de Consumo"));
        panelRegistro.setBackground(Color.WHITE);

        JLabel labelConsumo = new JLabel("Valor do Consumo:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelRegistro.add(labelConsumo, gbc);

        JTextField campoConsumo = new JTextField(10);
        campoConsumo.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 1;
        gbc.gridy = 0;
        panelRegistro.add(campoConsumo, gbc);

        JLabel labelDescricao = new JLabel("Descrição:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelRegistro.add(labelDescricao, gbc);

        JTextField campoDescricao = new JTextField(20);
        campoDescricao.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panelRegistro.add(campoDescricao, gbc);

        JButton botaoRegistrar = new JButton("Registrar");
        botaoRegistrar.setToolTipText("Registrar um novo consumo");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panelRegistro.add(botaoRegistrar, gbc);

        botaoRegistrar.addActionListener(e -> {
            try {
                float valor = Float.parseFloat(campoConsumo.getText());
                if (valor <= 0) {
                    throw new NumberFormatException("O valor de consumo deve ser positivo.");
                }
                String descricao = campoDescricao.getText();
                controleConsumo.registrarConsumo(new Date(), valor, descricao);
                JOptionPane.showMessageDialog(null, "Consumo registrado com sucesso!");

                atualizarGrafico();
                atualizarHistorico();

                if (controleConsumo.verificarAlerta()) {
                    JOptionPane.showMessageDialog(null, "Atenção! Seu consumo ultrapassou a meta configurada!", "Alerta de Meta", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Por favor, insira um valor válido e positivo para o consumo.");
            }
        });

        return panelRegistro;
    }

    private JPanel criarPainelMeta(GridBagConstraints gbc) {
        JPanel panelMeta = new JPanel(new GridBagLayout());
        panelMeta.setBorder(BorderFactory.createTitledBorder("Configuração de Metas"));
        panelMeta.setBackground(Color.WHITE);

        JLabel labelMeta = new JLabel("Meta de Consumo:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panelMeta.add(labelMeta, gbc);

        JTextField campoMeta = new JTextField(10);
        campoMeta.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 1;
        gbc.gridy = 0;
        panelMeta.add(campoMeta, gbc);

        JLabel labelTipoMeta = new JLabel("Tipo de Meta:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelMeta.add(labelTipoMeta, gbc);

        String[] tiposMeta = {"Diária", "Semanal", "Mensal"};
        JComboBox<String> comboBoxTipoMeta = new JComboBox<>(tiposMeta);
        comboBoxTipoMeta.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panelMeta.add(comboBoxTipoMeta, gbc);

        JButton botaoDefinirMeta = new JButton("Definir Meta");
        botaoDefinirMeta.setToolTipText("Definir meta de consumo");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panelMeta.add(botaoDefinirMeta, gbc);

        labelMetaAtual = new JLabel("Meta Atual: Não definida");
        labelMetaAtual.setForeground(new Color(0, 102, 102));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panelMeta.add(labelMetaAtual, gbc);

        botaoDefinirMeta.addActionListener(e -> {
            try {
                float valorMeta = Float.parseFloat(campoMeta.getText());
                if (valorMeta <= 0) {
                    throw new NumberFormatException("O valor da meta deve ser positivo.");
                }
                String tipoMeta = (String) comboBoxTipoMeta.getSelectedItem();
                controleConsumo.definirMeta(valorMeta, tipoMeta);
                JOptionPane.showMessageDialog(null, "Meta " + tipoMeta + " definida com sucesso!");
                atualizarMetaAtual();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Por favor, insira um valor válido e positivo para a meta.");
            }
        });

        return panelMeta;
    }

    private JPanel criarPainelAcoes(GridBagConstraints gbc) {
        JPanel panelAcoes = new JPanel(new GridBagLayout());
        panelAcoes.setBorder(BorderFactory.createTitledBorder("Ações e Relatórios"));
        panelAcoes.setBackground(Color.WHITE);

        JButton botaoExibirHistorico = new JButton("Exibir Histórico");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelAcoes.add(botaoExibirHistorico, gbc);

        JButton botaoExibirAlerta = new JButton("Exibir Alerta");
        gbc.gridx = 1;
        gbc.gridy = 0;
        panelAcoes.add(botaoExibirAlerta, gbc);

        JButton botaoRelatorioMensal = new JButton("Relatório Mensal");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panelAcoes.add(botaoRelatorioMensal, gbc);

        JButton botaoExportarCSV = new JButton("Exportar para CSV");
        gbc.gridx = 0;
        gbc.gridy = 4;
        panelAcoes.add(botaoExportarCSV, gbc);

        JButton botaoLogout = new JButton("Logout");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panelAcoes.add(botaoLogout, gbc);

        botaoExibirHistorico.addActionListener(e -> atualizarHistorico());
        botaoExibirAlerta.addActionListener(e -> verificarAlerta());
        botaoRelatorioMensal.addActionListener(e -> exibirRelatorioMensal());
        botaoExportarCSV.addActionListener(e -> exportarRelatorioParaCSV());
        botaoLogout.addActionListener(e -> realizarLogout());

        return panelAcoes;
    }

    private JPanel criarPainelEstatisticas() {
        JPanel panelEstatisticas = new JPanel(new BorderLayout());

        JPanel panelDetalhes = new JPanel(new GridLayout(3, 1));
        panelDetalhes.setBorder(BorderFactory.createTitledBorder("Estatísticas Detalhadas"));

        JLabel consumoMedioLabel = new JLabel("Consumo Médio Diário: " + controleConsumo.calcularConsumoMedioDiario());
        JLabel consumoTotalMesLabel = new JLabel("Consumo Total no Mês Atual: " + controleConsumo.calcularConsumoTotalMesAtual());
        JLabel comparacaoMesAnteriorLabel = new JLabel("Comparação com Mês Anterior: " + controleConsumo.compararMesAnterior());

        panelDetalhes.add(consumoMedioLabel);
        panelDetalhes.add(consumoTotalMesLabel);
        panelDetalhes.add(comparacaoMesAnteriorLabel);

        tipoGraficoComboBox = new JComboBox<>(new String[]{"Barras", "Pizza", "Linha"});
        tipoGraficoComboBox.addActionListener(e -> atualizarGrafico());

        panelEstatisticas.add(tipoGraficoComboBox, BorderLayout.NORTH);
        panelEstatisticas.add(panelDetalhes, BorderLayout.WEST);

        panelGraficos = new JPanel(new BorderLayout());
        atualizarGrafico();
        panelEstatisticas.add(panelGraficos, BorderLayout.CENTER);

        return panelEstatisticas;
    }

    private void atualizarGrafico() {
        panelGraficos.removeAll();
        String tipoGrafico = (String) tipoGraficoComboBox.getSelectedItem();
        JFreeChart chart;

        switch (tipoGrafico) {
            case "Pizza":
                chart = criarGraficoPizza();
                break;
            case "Linha":
                chart = criarGraficoLinha();
                break;
            default:
                chart = criarGraficoBarras();
                break;
        }

        ChartPanel chartPanel = new ChartPanel(chart);
        panelGraficos.add(chartPanel, BorderLayout.CENTER);
        panelGraficos.revalidate();
        panelGraficos.repaint();
    }

    private JFreeChart criarGraficoBarras() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Float> consumoMensal = controleConsumo.calcularConsumoMensal();

        for (Map.Entry<String, Float> entry : consumoMensal.entrySet()) {
            dataset.addValue(entry.getValue(), "Consumo", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Consumo Mensal de Energia",
                "Mês/Ano",
                "Consumo (kWh)",
                dataset
        );

        chart.getCategoryPlot().getRenderer().setSeriesPaint(0, new Color(79, 129, 189));
        chart.getCategoryPlot().setBackgroundPaint(Color.WHITE);
        chart.getCategoryPlot().setDomainGridlinePaint(Color.GRAY);
        chart.getCategoryPlot().setRangeGridlinePaint(Color.GRAY);

        return chart;
    }

    private JFreeChart criarGraficoPizza() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Float> consumoMensal = controleConsumo.calcularConsumoMensal();

        for (Map.Entry<String, Float> entry : consumoMensal.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Distribuição do Consumo Mensal",
                dataset,
                true,
                true,
                false
        );

        return chart;
    }

    private JFreeChart criarGraficoLinha() {
        XYSeries series = new XYSeries("Consumo Diário");
        Map<Date, Float> consumoDiario = controleConsumo.getConsumoDiario();

        for (Map.Entry<Date, Float> entry : consumoDiario.entrySet()) {
            series.add(entry.getKey().getTime(), entry.getValue());
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Consumo Diário",
                "Data",
                "Consumo (kWh)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(0, true);
        chart.getXYPlot().setRenderer(renderer);

        DateAxis dateAxis = new DateAxis("Data");
        dateAxis.setDateFormatOverride(new SimpleDateFormat("dd/MM/yyyy"));
        chart.getXYPlot().setDomainAxis(dateAxis);

        chart.getXYPlot().setBackgroundPaint(Color.WHITE);
        chart.getXYPlot().setDomainGridlinePaint(Color.GRAY);
        chart.getXYPlot().setRangeGridlinePaint(Color.GRAY);

        return chart;
    }

    private void verificarMetaDefinida() {
        if (controleConsumo.getMeta() == null) {
            JOptionPane.showMessageDialog(null, "Atenção: Nenhuma meta foi definida. Por favor, configure uma meta.", "Meta Indefinida", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void atualizarMetaAtual() {
        MetaConsumo meta = controleConsumo.getMeta();
        if (meta != null) {
            labelMetaAtual.setText("Meta Atual: " + meta.getTipo() + " - Valor: " + meta.getValorMeta());
        } else {
            labelMetaAtual.setText("Meta Atual: Não definida");
        }
    }

    private void exibirRelatorioMensal() {
        Map<String, Float> consumoMensal = controleConsumo.calcularConsumoMensal();
        StringBuilder relatorio = new StringBuilder("Relatório Mensal de Consumo:\n\n");

        for (Map.Entry<String, Float> entrada : consumoMensal.entrySet()) {
            relatorio.append("Mês/Ano: ").append(entrada.getKey())
                    .append(" - Consumo: ").append(entrada.getValue()).append("\n");
        }

        JTextArea areaTexto = new JTextArea(relatorio.toString());
        areaTexto.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(areaTexto), "Relatório Mensal de Consumo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void atualizarHistorico() {
        controleConsumo.carregarDados(); // Recarrega os dados do banco para garantir que estão atualizados
        StringBuilder historico = new StringBuilder("Histórico de Consumo:\n");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (RegistroConsumo consumo : controleConsumo.getConsumoList()) {
            historico.append("Data: ").append(sdf.format(consumo.getData()))
                    .append(" - Valor: ").append(consumo.getValor())
                    .append(" - Descrição: ").append(consumo.getDescricao()).append("\n");
        }

        JTextArea areaTexto = new JTextArea(historico.toString());
        areaTexto.setEditable(false);
        JOptionPane.showMessageDialog(null, new JScrollPane(areaTexto), "Histórico de Consumo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void verificarAlerta() {
        if (controleConsumo.verificarAlerta()) {
            JOptionPane.showMessageDialog(null, "Alerta: O consumo excedeu a meta!", "Alerta", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Consumo dentro da meta.");
        }
    }

    private void exportarRelatorioParaCSV() {
        String caminhoArquivo = "relatorio_consumo.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo))) {
            writer.println("Data,Consumo (kWh),Descrição");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (RegistroConsumo consumo : controleConsumo.getConsumoList()) {
                String data = sdf.format(consumo.getData());
                float valor = consumo.getValor();
                String descricao = consumo.getDescricao();
                writer.println(data + "," + valor + "," + descricao);
            }

            JOptionPane.showMessageDialog(null, "Relatório exportado para " + caminhoArquivo);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao exportar relatório: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void realizarLogout() {
        int confirm = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja sair?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void salvarMeta() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(META_FILE_PATH))) {
            MetaConsumo meta = controleConsumo.getMeta();
            if (meta != null) {
                writer.println(meta.getValorMeta());
                writer.println(meta.getTipo());
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar meta: " + e.getMessage());
        }
    }

    private void carregarMeta() {
        File metaFile = new File(META_FILE_PATH);
        if (!metaFile.exists()) {
            System.out.println("O arquivo de meta não foi encontrado. Nenhuma meta será carregada.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(metaFile))) {
            String valorMetaStr = reader.readLine();
            String tipoMeta = reader.readLine();

            if (valorMetaStr != null && tipoMeta != null) {
                try {
                    float valorMeta = Float.parseFloat(valorMetaStr);
                    controleConsumo.definirMeta(valorMeta, tipoMeta);
                    atualizarMetaAtual();
                    System.out.println("Meta carregada com sucesso: " + valorMeta + " - " + tipoMeta);
                } catch (NumberFormatException e) {
                    System.out.println("O valor da meta no arquivo é inválido.");
                }
            } else {
                System.out.println("Arquivo de meta está vazio ou incompleto.");
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar o arquivo de meta: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
}