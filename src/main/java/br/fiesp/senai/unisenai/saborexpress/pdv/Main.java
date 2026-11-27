package br.fiesp.senai.unisenai.saborexpress.pdv;

import br.fiesp.senai.unisenai.saborexpress.pdv.config.DataLoader;
import br.fiesp.senai.unisenai.saborexpress.pdv.controller.ClienteController;
import br.fiesp.senai.unisenai.saborexpress.pdv.controller.ProdutoController;
import br.fiesp.senai.unisenai.saborexpress.pdv.controller.VendaController;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.CategoriaRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.CategoriaRepositoryImpl;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.ClienteRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.ClienteRepositoryImpl;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.ProdutoRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.ProdutoRepositoryImpl;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.VendaRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.VendaRepositoryImpl;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.service.ProdutoService;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.service.VendaService;
import br.fiesp.senai.unisenai.saborexpress.pdv.util.JPAUtil;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.CaixaView;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.HistoricoVendasView;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ponto de Entrada Principal (Bootstrap) da Aplicação Desktop PDV-Swing-JPA.
 * <p>
 * <b>Conceito Pedagógico (Inversão de Controle & Thread Safety no Swing):</b>
 * <ul>
 * <li><b>Look & Feel Moderno:</b> Configura o tema FlatLaf para uma interface
 * moderna,
 * elegante e profissional, com fallback transparente para o tema nativo do
 * Sistema Operacional.</li>
 * <li><b>Event Dispatch Thread (EDT):</b> Toda criação e manipulação de
 * componentes gráficos
 * Swing deve ser executada obrigatoriamente dentro do método
 * {@link SwingUtilities#invokeLater(Runnable)},
 * garantindo segurança concorrente (Single-Thread Rule do Swing).</li>
 * <li><b>Injeção de Dependências Manual (Pure DI):</b> A classe {@code Main}
 * atua como
 * o <i>Composition Root</i>, instanciando o Repositório, o Serviço, a Visão e o
 * Controlador,
 * injetando as dependências de forma limpa sem a complexidade de frameworks
 * externos.</li>
 * <li><b>Ciclo de Vida Limpo:</b> Registra um
 * {@link java.awt.event.WindowListener} para encerrar
 * a conexão com o banco de dados H2 e o
 * {@link jakarta.persistence.EntityManagerFactory}
 * quando a janela for fechada pelo usuário.</li>
 * <li><b>Data Loader (Database Seeding):</b> Executa carga inicial de dados
 * idempotente via {@link DataLoader} (padrão CommandLineRunner do Spring Boot).</li>
 * </ul>
 *
 * @author Cainã Antunes Silva
 */
public class Main {

    public static void main(String[] args) {
        // 1. Configuração do Look and Feel (FlatLaf Moderno ou Nativo do S.O.)
        configurarLookAndFeel();

        // 2. Inicialização da Interface Gráfica na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Instanciação da camada de persistência (JPA / Hibernate / H2)
                ProdutoRepository produtoRepository = new ProdutoRepositoryImpl();
                CategoriaRepository categoriaRepository = new CategoriaRepositoryImpl();

                // Carga inicial de dados via DataLoader (padrão CommandLineRunner)
                System.out.println("[PDV-Swing] Executando DataLoader para carga inicial...");
                DataLoader dataLoader = new DataLoader(produtoRepository, categoriaRepository);
                dataLoader.run();

                // Instanciação da camada de serviço de negócio e do Observer
                ProdutoService service = new ProdutoService(produtoRepository, categoriaRepository);

                // Instanciação dos repositórios e serviços de Venda
                VendaRepository vendaRepository = new VendaRepositoryImpl();
                VendaService vendaService = new VendaService(vendaRepository, service);

                // Instanciação da Janela Principal (View com CardLayout)
                MainFrame mainFrame = new MainFrame();

                // Instanciação dos Controladores conectando View e Model (MVC)
                ProdutoController produtoController = new ProdutoController(mainFrame, service);
                produtoController.inicializar();

                ClienteRepository clienteRepository = new ClienteRepositoryImpl();
                ClienteController clienteController = new ClienteController(mainFrame, clienteRepository);
                clienteController.inicializar();

                CaixaView caixaView = mainFrame.getCaixaView();
                HistoricoVendasView historicoView = mainFrame.getHistoricoView();
                VendaController vendaController = new VendaController(mainFrame, caixaView, historicoView, service, vendaService);
                vendaController.inicializar();

                // Mostra o Cardápio como tela inicial
                mainFrame.mostrarLista();

                // Gerenciamento de encerramento de conexões
                mainFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.out.println("[PDV-Swing] Encerrando recursos de banco de dados JPA/H2...");
                        JPAUtil.close();
                    }
                });

                // Exibe a janela para o usuário
                mainFrame.setVisible(true);

            } catch (Exception e) {
                System.err.println("[PDV-Swing] Erro fatal durante a inicialização da aplicação: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Aplica o tema visual FlatLaf ou o Look and Feel do sistema operacional.
     */
    private static void configurarLookAndFeel() {
        try {
            FlatLightLaf.setup();
        } catch (Throwable t) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Não foi possível configurar Look and Feel do sistema: " + ex.getMessage());
            }
        }
    }
}