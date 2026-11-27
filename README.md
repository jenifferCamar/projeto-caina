![senai_logo](./logo_unisenai.png)

# Sabor Express PDV — Sistema de Gestão para Lanchonete

> Projeto prático (PBL) de **Técnicas de Programação II** baseado no PDV Core.  
> Sistema desktop em **Java 17, Swing, JPA/Hibernate 6, H2**, com arquitetura **MVC**, padrões **Repository/Observer** e transações atômicas.

## 1. Contexto

O **Sabor Express PDV** evolui o PDV base para o nicho de lanchonete, cobrindo cadastro de produtos/categorias, clientes e fluxo de venda com validação, persistência transacional, baixa/estorno de estoque e atualização reativa da interface.

## 2. Objetivos técnicos

- Aplicar **MVC** com separação entre View, Controller, Service e Repository.
- Usar **JPA/Hibernate** com banco **H2** embutido para persistência ORM.
- Implementar **Observer** para atualização reativa da UI.
- Garantir regras de negócio e transações com **commit/rollback**.
- Manter versionamento colaborativo com **Gitflow** e Conventional Commits.

## 3. Funcionalidades

- **Cardápio/Produtos**: cadastro, edição, exclusão lógica/ativa-inativa conforme regra, busca, categoria e estoque.
- **Clientes**: cadastro e consulta para uso no caixa.
- **Caixa/Venda**: montagem de carrinho, validação de produto/estoque, finalização com baixa de estoque atômica.
- **Cancelamento**: cancelamento de venda finalizada com estorno de estoque e status `CANCELADA` (Card 17).
- **Histórico**: listagem de vendas e detalhes.
- **Exceções**: `BusinessException`, `ValidationException`, `PersistenceException` com tratamento amigável na UI.

## 4. Arquitetura e padrões

- **MVC**: Views Swing não acessam repositório; Controllers orquestram; Services concentram regras; Repositories isolam JPA.
- **Repository**: interfaces e implementações JPA por agregado.
- **Observer**: `ProdutoObserver` e `VendaObserver` notificam UI via `SwingUtilities.invokeLater`.
- **Transação atômica**: operações de venda/cancelamento usam `begin/commit/rollback` no `EntityManager`.

## 5. Diagramas Arquiteturais (Mermaid)

### 5.1. Visão Geral da Arquitetura em Camadas (Sabor Express PDV)

```mermaid
graph TD
    subgraph UI_Layer ["Camada de Apresentação (View - Java Swing)"]
        MF["MainFrame (CardLayout)"]
        PV["ProdutoList/FormView"]
        CV["CaixaView / HistoricoVendasView"]
        CLV["ClienteList/FormView"]
    end

    subgraph Controller_Layer ["Camada de Controle (Controller / Mediator)"]
        PC["ProdutoController"]
        VC["VendaController"]
        CC["ClienteController"]
    end

    subgraph Model_Layer ["Camada de Modelo (Domain / Service / Observer)"]
        PS["ProdutoService"]
        VS["VendaService"]
        CS["ClienteService"]
        PO["ProdutoObserver"]
        VO["VendaObserver"]
        ENT["Entidades: Categoria, Produto, Cliente, Venda, ItemVenda"]
    end

    subgraph Persistence_Layer ["Camada de Persistência (Repository / JPA / H2)"]
        REPO["Repositories (Produto/Cliente/Venda/Categoria)"]
        JPA["JPAUtil / EntityManager"]
        H2[("H2 Database")]
    end

    PV -- eventos --> PC
    CV -- eventos --> VC
    CLV -- eventos --> CC
    PC --> PS
    VC --> VS
    VC --> PS
    CC --> CS
    PS --> REPO
    VS --> REPO
    CS --> REPO
    PS -- notifica --> PO
    VS -- notifica --> VO
    PO -- atualiza UI --> PV
    VO -- atualiza UI --> CV
    REPO --> JPA --> H2
    REPO --> ENT
```

### 5.2. Diagrama de Sequência: Fluxo de Venda (Sabor Express PDV)

O diagrama detalha a finalização de venda no caixa, incluindo validação, transação atômica com baixa de estoque, rollback em falha e notificação reativa da UI (Observer). Também contempla o cancelamento de venda finalizada com estorno de estoque (Card 17, validado).

```mermaid
sequenceDiagram
    autonumber
    actor Operador
    participant Caixa as CaixaView
    participant Ctrl as VendaController
    participant VSvc as VendaService
    participant PSvc as ProdutoService
    participant VRepo as VendaRepositoryImpl
    participant JPA as EntityManager (H2)
    participant Obs as VendaObserver / UI

    Operador->>Caixa: Clica em "Finalizar Venda"
    Caixa->>Ctrl: ActionListener (btnFinalizar)
    Ctrl->>Ctrl: Valida carrinho não vazio e monta itens
    Ctrl->>VSvc: finalizarVenda(carrinho, clienteId, clienteNome)

    Note over VSvc: Validação de negócio<br/>- Carrinho não vazio<br/>- Produto existente/ativo<br/>- Estoque suficiente

    VSvc->>VRepo: finalizarVenda(venda)
    VRepo->>JPA: begin()
    loop Para cada ItemVenda
        VRepo->>JPA: find(Produto)
        VRepo->>VRepo: valida quantidade > 0 e estoque suficiente
        VRepo->>JPA: merge(produto com estoque decrementado)
    end
    VRepo->>JPA: persist(venda + itens)
    alt Sucesso
        VRepo->>JPA: commit()
        JPA-->>VRepo: Venda persistida
        VRepo-->>VSvc: Venda finalizada
        VSvc->>Obs: notificarObservadores() via SwingUtilities.invokeLater
        Obs-->>Caixa: atualiza histórico/tabelas (Observer)
        VSvc-->>Ctrl: Venda
        Ctrl->>Caixa: mensagem de sucesso e limpa carrinho
    else Falha de validação/persistência
        VRepo->>JPA: rollback()
        VRepo-->>VSvc: BusinessException/ValidationException/PersistenceException
        VSvc-->>Ctrl: exceção
        Ctrl->>Caixa: JOptionPane amigável (sem estado parcial)
    end

    Note over Operador,Obs: Cancelamento de venda finalizada (estorno)

    Operador->>Caixa: Seleciona venda e clica "Cancelar Venda"
    Caixa->>Ctrl: ActionListener (cancelar)
    Ctrl->>VSvc: cancelarVenda(id)
    VSvc->>VRepo: cancelarVenda(id)
    VRepo->>JPA: begin()
    VRepo->>JPA: find(Venda)
    VRepo->>VRepo: valida FINALIZADA e não CANCELADA
    loop Para cada ItemVenda
        VRepo->>JPA: find(Produto)
        VRepo->>JPA: merge(produto com estoque estornado)
    end
    VRepo->>JPA: merge(venda STATUS_CANCELADA)
    alt Sucesso
        VRepo->>JPA: commit()
        VRepo-->>VSvc: Venda cancelada
        VSvc->>Obs: notifica VendaObserver e ProdutoObserver
        Obs-->>Caixa: atualiza histórico e catálogo
    else Falha
        VRepo->>JPA: rollback()
        VRepo-->>VSvc: exceção de domínio/persistência
        Ctrl->>Caixa: mensagem amigável
    end
```

### 5.3. Diagrama de Classes: Domínio e Padrões (Sabor Express PDV)

O diagrama reflete as entidades reais do domínio e os padrões Repository/Observer usados pelo Sabor Express PDV.

```mermaid
classDiagram
    direction LR

    class Categoria {
        -Long id
        -String nome
        -String descricao
        -Boolean ativa
    }

    class Produto {
        -Long id
        -String codigoBarras
        -String nome
        -String descricao
        -BigDecimal preco
        -Boolean ativo
        -Integer estoque
    }

    class Cliente {
        -Long id
        -String nome
        -String telefone
        -String observacoes
    }

    class Venda {
        -Long id
        -LocalDateTime dataHora
        -Long clienteId
        -String clienteNome
        -String formaPagamento
        -String status
        -BigDecimal total
        +adicionarItem(ItemVenda)
        +recalcularTotal()
    }

    class ItemVenda {
        -Long id
        -Integer quantidade
        -BigDecimal precoUnitario
        -BigDecimal subtotal
    }

    class VendaRepository {
        <<interface>>
        +salvar(Venda) Venda
        +atualizar(Venda) Venda
        +finalizarVenda(Venda) Venda
        +cancelarVenda(Long) Venda
        +buscarPorId(Long) Optional~Venda~
        +listarTodos() List~Venda~
    }

    class VendaService {
        -VendaRepository vendaRepository
        -ProdutoService produtoService
        -List~VendaObserver~ observers
        +finalizarVenda(List~ItemCarrinho~, Long, String) Venda
        +cancelarVenda(Long) Venda
        +addObserver(VendaObserver) void
        +notificarObservadores() void
    }

    class VendaObserver {
        <<interface>>
        +onVendasChanged(List~Venda~) void
    }

    class ProdutoObserver {
        <<interface>>
        +onProdutosChanged(List~Produto~) void
    }

    Categoria "1" --> "0..*" Produto : classifica
    Venda "1" --> "1..*" ItemVenda : contém
    ItemVenda "*" --> "1" Produto : referencia
    Venda ..> Cliente : referencia cliente

    VendaService --> VendaRepository : usa
    VendaService o--> VendaObserver : notifica
    VendaService --> ProdutoObserver : notifica estoque
    VendaRepository --> Venda : persiste
    VendaRepository --> ItemVenda : cascade
```

## 6. Stack tecnológica

| Tecnologia | Uso |
|---|---|
| Java 17 (LTS) | Linguagem base |
| Java Swing + FlatLaf | Interface desktop |
| Jakarta Persistence 3.1 / Hibernate 6 | ORM e transações |
| H2 Database | Banco embutido (`./data/pdv_db`) |
| Maven Wrapper | Build/teste reprodutível |
| JUnit 5 | Testes automatizados |

## 7. Estrutura do projeto

```text
src/
├── main/java/br/fiesp/senai/unisenai/saborexpress/pdv/
│   ├── Main.java
│   ├── config/
│   ├── controller/
│   ├── model/
│   │   ├── entity/        # Categoria, Produto, Cliente, Venda, ItemVenda
│   │   ├── exception/     # Business/Validation/PersistenceException
│   │   ├── observer/      # ProdutoObserver, VendaObserver
│   │   ├── repository/    # interfaces + impl JPA
│   │   └── service/       # regras de negócio e notificações
│   ├── util/
│   └── view/              # MainFrame, Produto/Cliente/Caixa/Histórico, tablemodels
└── main/resources/META-INF/persistence.xml
```

## 8. Como compilar e executar

### Pré-requisitos
- Java JDK 17+ (`java -version`).

### Testes

```powershell
$env:JAVA_HOME='C:\Users\Jeniffer.Oliveira\AppData\Local\Programs\Microsoft\jdk-17.0.21.21-hotspot'
.\mvnw.cmd test
```

### Executar aplicação

```powershell
$env:JAVA_HOME='C:\Users\Jeniffer.Oliveira\AppData\Local\Programs\Microsoft\jdk-17.0.21.21-hotspot'
.\mvnw.cmd exec:java
```

### Empacotar

```powershell
.\mvnw.cmd clean package
java -jar target/exemplo_persistencia_JavaSwing-1.0.0.jar
```

> Observação: se o H2 local tiver schema antigo, feche a aplicação e remova `./data/pdv_db*` antes de subir novamente.

## 9. Gitflow e evidências

- Fluxo: `main` ← `develop` ← `feature/*`/`fix/*`/`docs/*`, PR para `develop`.
- Commits: Conventional Commits (`feat`, `fix`, `docs`, `refactor`, `test`, `chore`).
- Evidências: PRs com descrição/checklist, revisão cruzada, histórico Git e testes Maven.

## 10. Referências

- Gamma et al. *Design Patterns*. Addison-Wesley, 1995.
- Freeman & Freeman. *Head First Design Patterns*. O'Reilly, 2004.
- Fowler. *Patterns of Enterprise Application Architecture*. Addison-Wesley, 2002.
- Jakarta Persistence 3.1: https://jakarta.ee/specifications/persistence/3.1/
- Hibernate ORM: https://hibernate.org/orm/documentation/
- H2 Database: https://www.h2database.com/html/main.html
