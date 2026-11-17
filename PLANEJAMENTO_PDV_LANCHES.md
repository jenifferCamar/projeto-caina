# Planejamento do PDV de Lanches (PBL) — Sabor Express PDV

**Nome do sistema (PDV):** **Sabor Express PDV**  
**Subtítulo/slogan sugerido:** Sistema de Gestão para Lanchonete  
**Título da janela (app):** `Sabor Express PDV - Gestão de Lanchonete`  
**Projeto base:** PDV Core (Produto CRUD, MVC, Observer, JPA/Hibernate, H2, Java Swing)  
**Nicho:** Lanchonete  
**Repositório:** https://github.com/jenifferCamar/projeto-caina  
**Equipe:** Gusleme, w-tangly, Jeniffer  
**Fluxo Git:** `main` ← `develop` ← `feature/*` (PR para `develop`, revisão cruzada)  
**Commits:** Conventional Commits (`feat`, `fix`, `docs`, `refactor`, `test`, `chore`)

---

## 1. O que precisa mudar para o nome do PDV

| Item | Onde | O que fazer | Responsável | Card |
|---|---|---|---|---|
| Título da janela | `src/main/java/br/gov/sp/cps/fatec/pdv/view/MainFrame.java` | `super("PDV - Gestão de Produtos")` → `super("Sabor Express PDV - Gestão de Lanchonete")` | w-tangly | 8 |
| Nome/descrição Maven | `pom.xml` | Ajustar `<name>` e `<description>` para Sabor Express PDV / lanchonete | Jeniffer | 11 |
| Artefato/JAR (opcional) | `pom.xml` e README | Se mudar `<artifactId>`/nome do JAR, alinhar comando de execução e documentação | Jeniffer | 11 |
| Documentação | `README.md` | Título, contexto lanchonete, execução, entidades, fluxos e branding | Gusleme + Jeniffer | 9, 24 |
| Textos de UI/navegação | Views e `MainFrame` | Rótulos Cardápio, Clientes, Caixa, Histórico/Vendas | w-tangly / Gusleme | 5, 7, 8 |
| Dados de demonstração | `Main.java` (ou seed) | Popular com itens de lanchonete | Gusleme | 12 |
| Banco local | `./data/pdv_db*` | Recriar base local se schema/seed mudar (documentar) | Todos | 12, 24 |
| Diagramas | `README.md` / docs | Refletir telas, fluxos e contexto Sabor Express PDV | Jeniffer | 23, 9 |

**Critério:** ao final, não restar nome genérico antigo na UI, `pom.xml` e README; app abre como **Sabor Express PDV - Gestão de Lanchonete**.

---

## 2. Regras para trabalhar em paralelo (sem bloquear)

1. **Trilha por pessoa:** cada um atua no seu domínio/arquivos principais.
2. **Contrato antes da implementação:** alinhar e commitar em `develop` (ou PR base) interfaces/assinaturas (entidades, repositories, services, observers) antes de features dependentes.
3. **Branch curta + PR frequente:** 1 card por branch, PR cedo (draft se preciso), merge em `develop` após revisão.
4. **Dono de arquivo compartilhado:** só o dono faz o merge/ajuste final nesses arquivos (ou faz com alinhamento).
5. **Sem commit direto em `main/develop`**; usar PR com revisão cruzada (≥1 de outra trilha).
6. **Integração por marcos (M1–M4):** consolidar `develop` antes de cards dependentes; se atrasar, usar stub/contrato e registrar pendência.
7. **Avisar no grupo** ao alterar contrato público ou concluir marco.

### Arquivos compartilhados e donos

| Arquivo/área | Risco | Dono da integração | Regra |
|---|---|---|---|
| `Main.java` (bootstrap, wiring, seed) | Alto | **Gusleme** | Outros combinam wiring (Cliente/Venda) via PR/marco. |
| `view/MainFrame.java` e navegação (`CardLayout`) | Alto | **w-tangly** (Card 8) | Demais entregam views/painéis; w-tangly registra telas e aplica título do PDV. |
| `pom.xml` | Médio | **Jeniffer** | Só Jeniffer altera; solicitar dependências via issue/PR. |
| `persistence.xml` / config JPA | Médio | **Jeniffer** (revisão) | Autor registra entidade; revisão final de config com Jeniffer. |
| `Produto*` (entidade/repo/service) | Médio | **Gusleme** | Mudança de contrato/consulta combinada por PR pequeno. |
| `Venda*` / `ItemVenda*` / services de venda | Médio | **Jeniffer** | Consumir via interface pública; não alterar regra transacional sem alinhamento. |
| `README.md` e docs | Médio | **Gusleme** (24) + **Jeniffer** (9,23) | Dividir seções e consolidar em PR final. |
| `.gitignore`, templates, convenções | Baixo | **Jeniffer** (11) | Alterar no Card 11 ou com aprovação. |

### Marcos de integração

| Marco | Após cards | Entrega em `develop` | Integra | Desbloqueia |
|---|---|---|---|---|
| **M1 — Base** | 11, 1, 2 | Convenções, Produto/Categoria, Cliente | Jeniffer | 3, 4, 12, 13, 14, 15 |
| **M2 — Domínios** | 3, 4, 12, 14 | Venda/ItemVenda, UI Cliente, seed, produto ativo | Jeniffer | 5, 6, 7, 8, 16, 18 |
| **M3 — Fluxo de venda** | 5, 6, 8, 13, 15, 16, 17, 18, 19 | Caixa, transação/estoque, pagamento/cancelamento, navegação/UI | Jeniffer (+ w-tangly em `MainFrame`) | 7, 20, 21, 22 |
| **M4 — Qualidade e entrega** | 7, 9, 20, 21, 22, 23, 24 | Histórico/relatório, exceções, testes, docs/README | Todos (coord. Jeniffer) | 10, 25, `main` |

---

## 3. Resumo da atribuição

| Pessoa | Trilha | Cards (ordem) | Total |
|---|---|---|---|
| **Gusleme** | A — Cardápio/Produtos & Operação | 1 → 12 → 14 → 18 → 5 → 7 → 20 → 24 | 8 |
| **w-tangly** | B — Clientes & Experiência de UI | 2 → 4 → 15 → 13 → 8 → 19 → 22 | 7 |
| **Jeniffer** | C — Vendas-core, Transações & Integração | 11 → 3 → 6 → 16 → 17 → 21 → 23 → 9 → 25 (+ coord. 10) | 9 + coord. |
| **Todos** | Compartilhado | 10 (integração, revisão, entrega) | — |

---

## 4. Gusleme — o que fazer (Trilha A)

### Card 1 — Modelagem do domínio de lanches (Produto/Categoria)
- **Branch:** `feature/dominio-lanches` | **Depende de:** 11 | **Marco:** M1 | **Labels:** `feature`, `persistence`, `model`
- **Tarefas:**
  - [ ] Criar/ajustar entidade `Categoria` (ex.: Lanche, Bebida, Acompanhamento, Sobremesa) e relacionar com `Produto`.
  - [ ] Evoluir `Produto` para cardápio (nome, descrição opcional, preço, categoria, ativo, estoque/controle conforme definido) com JPA coerente.
  - [ ] Ajustar `ProdutoRepository`/`ProdutoService` e validações (preço > 0, campos obrigatórios, unicidade quando aplicável).
  - [ ] Preparar base para carga inicial de lanches (integração com Card 12).
- **Critérios de aceite:** Persistência no H2 sem erros; constraints/relacionamentos corretos; sem SQL manual/injeção.
- **Commit:** `feat(model): add categoria and adapt produto for lanches`
- **Status:** ⬜ A fazer · 🔄 Em andamento · 👀 Em revisão · ✅ Concluído · ⛔ Bloqueado | **PR/Evidência:** ___

### Card 12 — Seed de dados do cardápio de lanches
- **Branch:** `feature/seed-cardapio-lanches` | **Depende de:** 1 | **Marco:** M2 | **Labels:** `feature`, `persistence`
- **Tarefas:**
  - [ ] Popular base com categorias e produtos de lanchonete (lanches, bebidas, acompanhamentos, sobremesas).
  - [ ] Seed idempotente (apenas se base vazia) no bootstrap (`Main` ou classe dedicada).
  - [ ] Dados realistas para demonstração do **Sabor Express PDV**.
- **Critérios de aceite:** Ao iniciar com base vazia, cardápio aparece pronto para uso.
- **Commit:** `feat(data): seed lanchonete menu on empty database`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 14 — Ativação/inativação de item do cardápio
- **Branch:** `feature/produto-ativo` | **Depende de:** 1 | **Marco:** M2 | **Labels:** `feature`, `model`, `ui`
- **Tarefas:**
  - [ ] Campo `ativo` em `Produto` mapeado/persistido.
  - [ ] UI para ativar/inativar; inativos não disponíveis para venda.
  - [ ] `ProdutoService`/listagens respeitam status.
- **Critérios de aceite:** Item inativo não é vendido e permanece no cadastro/histórico conforme regra.
- **Commit:** `feat(produto): support activate and deactivate menu items`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 18 — Controle e alerta de estoque baixo
- **Branch:** `feature/estoque-alerta` | **Depende de:** 1, 14 | **Marco:** M3 | **Labels:** `feature`, `business`, `ui`
- **Tarefas:**
  - [ ] Definir estoque mínimo (campo/regra documentada).
  - [ ] Indicar estoque baixo/zerado na listagem.
  - [ ] Bloquear venda quando quantidade > disponível, com mensagem clara.
  - [ ] Alinhar regra de bloqueio com Card 6 (transação/venda) — contrato combinado.
- **Critérios de aceite:** Alertas visíveis e bloqueio efetivo sem estoque suficiente.
- **Commit:** `feat(estoque): add low-stock alert and sale blocking`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 5 — Tela de Caixa/PDV para registrar venda
- **Branch:** `feature/view-caixa-venda` | **Depende de:** 1, 3, 6 (contratos/services) | **Marco:** M3 | **Labels:** `feature`, `ui`, `mvc`
- **Tarefas:**
  - [ ] View de caixa: selecionar produto do cardápio, quantidade, adicionar/remover itens, subtotal/total.
  - [ ] Seleção opcional de cliente e finalização do pedido.
  - [ ] `VendaController`/integração com services; atualização reativa (Observer).
  - [ ] Feedback de erros (estoque insuficiente, quantidade inválida).
  - [ ] Consumir service/repository de venda via interface pública (não alterar regra transacional de Jeniffer).
- **Critérios de aceite:** Fluxo de venda funcional e sincronizado; Controller apenas orquestra.
- **Commit:** `feat(ui): implement caixa view for lanche sales`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 7 — Histórico/consulta de vendas
- **Branch:** `feature/historico-vendas` | **Depende de:** 3, 6, 16 | **Marco:** M4 | **Labels:** `feature`, `ui`, `persistence`
- **Tarefas:**
  - [ ] Listagem de vendas com data, cliente, total e status.
  - [ ] Visualização de detalhes/itens da venda selecionada.
  - [ ] `VendaTableModel`/views com MVC+Observer.
- **Critérios de aceite:** Histórico carrega do banco e atualiza após nova venda.
- **Commit:** `feat(ui): add sales history and details view`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 20 — Resumo/relatório simples de vendas
- **Branch:** `feature/relatorio-vendas` | **Depende de:** 3, 6, 17 | **Marco:** M4 | **Labels:** `feature`, `ui`, `persistence`
- **Tarefas:**
  - [ ] Exibir quantidade de vendas, faturamento e ticket médio (período definido ou geral).
  - [ ] Consultas agregadas em repository/service (sem persistência na View).
  - [ ] Atualizar após vendas/cancelamentos.
- **Critérios de aceite:** Indicadores conferem com dados registrados no banco.
- **Commit:** `feat(relatorio): add basic sales summary dashboard`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 24 — README de entrega do PBL
- **Branch:** `docs/readme-entrega` | **Depende de:** 9, 23, implementação estável | **Marco:** M4 | **Labels:** `docs`
- **Tarefas:**
  - [ ] Documentar **Sabor Express PDV** (nicho, funcionalidades, modelo, decisões arquiteturais).
  - [ ] Pré-requisitos (JDK 17), execução (`mvnw`/JAR/IDE), H2 e estrutura de pastas.
  - [ ] Divisão de tarefas da equipe e evidências de Gitflow/commits.
  - [ ] Consolidar seções com Jeniffer (diagramas/arquitetura) em PR final.
- **Critérios de aceite:** Terceiro executa e entende o projeto apenas pelo README.
- **Commit:** `docs: finalize README for Sabor Express PDV delivery`
- **Status:** ⬜ | **PR/Evidência:** ___

---

## 5. w-tangly — o que fazer (Trilha B)

### Card 2 — CRUD de Cliente (ORM + Repository)
- **Branch:** `feature/cliente-crud` | **Depende de:** 11 | **Marco:** M1 | **Labels:** `feature`, `persistence`
- **Tarefas:**
  - [ ] Entidade `Cliente` (id, nome, telefone, observações opcional) com anotações JPA.
  - [ ] `ClienteRepository` (interface) + `ClienteRepositoryImpl` (salvar, atualizar, excluir, buscarPorId, listarTodos).
  - [ ] `ClienteService` com validações e `ClienteObserver` para notificação reativa.
  - [ ] Testes básicos de persistência/repositório (quando aplicável).
- **Critérios de aceite:** CRUD funcional via service/repository; `commit/rollback` em falhas; persistência isolada.
- **Commit:** `feat(cliente): implement cliente entity repository and service`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 4 — Telas Swing de Cliente (MVC + Observer)
- **Branch:** `feature/view-cliente` | **Depende de:** 2 | **Marco:** M2 | **Labels:** `feature`, `ui`, `mvc`
- **Tarefas:**
  - [ ] `ClienteTableModel`, `ClienteFormView`, `ClienteListView` seguindo padrão de Produto.
  - [ ] Integrar ao `MainFrame`/`CardLayout` sem View acessar banco (expor painel para registro pelo dono do `MainFrame`).
  - [ ] `ClienteController` orquestrando ações e reagindo a `onClientesChanged`.
- **Critérios de aceite:** CRUD utilizável na UI; tabela atualiza reativamente; MVC respeitado.
- **Commit:** `feat(ui): add cliente views tablemodel and controller`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 15 — Busca e validação de cliente
- **Branch:** `feature/cliente-busca-validacao` | **Depende de:** 2, 4 | **Marco:** M3 | **Labels:** `feature`, `ui`, `business`
- **Tarefas:**
  - [ ] Busca por nome/telefone no repositório e na tela.
  - [ ] Validações no `ClienteService` (obrigatórios/formato).
  - [ ] Mensagens amigáveis na View sem expor detalhes de persistência.
- **Critérios de aceite:** Cadastro consistente e busca rápida no caixa.
- **Commit:** `feat(cliente): add search and input validation`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 13 — Busca e filtro de produtos por categoria/nome
- **Branch:** `feature/produto-busca-filtro` | **Depende de:** 1 (contrato) | **Marco:** M3 | **Labels:** `feature`, `ui`, `persistence`
- **Tarefas:**
  - [ ] Propor/usar consultas no `ProdutoRepository` (categoria, nome/código) — contrato alinhado com Gusleme.
  - [ ] Filtros na listagem com atualização da `JTable`.
  - [ ] Atualização reativa via `Observer`; View sem acesso direto ao banco.
- **Critérios de aceite:** Localização rápida de itens; arquitetura preservada.
- **Commit:** `feat(produto): add search and category filters`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 8 — Ajustes de UI do cardápio e navegação + nome do PDV
- **Branch:** `feature/ui-cardapio-navegacao` | **Depende de:** 4, 5, 7 (integração) | **Marco:** M3 | **Labels:** `enhancement`, `ui`
- **Tarefas:**
  - [ ] Ajustar listagem de produtos para exibir categoria e rótulos do cardápio de lanches.
  - [ ] Navegação no `MainFrame` (Cardápio, Clientes, Caixa, Histórico) via `CardLayout` — **dono do `MainFrame`**.
  - [ ] Aplicar título da janela: `Sabor Express PDV - Gestão de Lanchonete`.
  - [ ] Garantir atualização reativa das tabelas após CRUD e vendas.
- **Critérios de aceite:** Navegação clara; dados consistentes; nome do PDV visível na janela principal.
- **Commit:** `feat(ui): improve navigation and apply Sabor Express PDV branding`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 19 — Formatação e máscaras na interface (moeda/telefone)
- **Branch:** `feature/ui-formatacao-mascaras` | **Depende de:** 4, 5 | **Marco:** M3 | **Labels:** `enhancement`, `ui`
- **Tarefas:**
  - [ ] Formatar moeda em pt-BR (preço/subtotal/total).
  - [ ] Máscara/validação para telefone e campos numéricos.
  - [ ] Conversão segura para `BigDecimal`/inteiros.
- **Critérios de aceite:** UI consistente e sem erros de conversão comuns.
- **Commit:** `feat(ui): format currency and add input masks`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 22 — Testes de persistência e regras de venda
- **Branch:** `test/persistencia-venda` | **Depende de:** 2, 3, 6 | **Marco:** M4 | **Labels:** `test`, `persistence`
- **Tarefas:**
  - [ ] Testes JUnit para repositórios (`Produto`, `Cliente`, `Venda`/`ItemVenda`).
  - [ ] Cenários de serviço: venda com estoque suficiente, insuficiente e rollback em falha.
  - [ ] Execução via `.\mvnw.cmd test`.
- **Critérios de aceite:** Testes passam e cobrem fluxo crítico de venda.
- **Commit:** `test: add repository and sale service tests`
- **Status:** ⬜ | **PR/Evidência:** ___

---

## 6. Jeniffer — o que fazer (Trilha C)

### Card 11 — Configuração do repositório e convenções
- **Branch:** `chore/setup-repo` | **Depende de:** — | **Marco:** M1 | **Labels:** `chore`, `docs`
- **Tarefas:**
  - [ ] Criar/ajustar `develop`; definir proteção de branches (se possível).
  - [ ] Padronizar templates de issue/PR e convenção de commits (README/CONTRIBUTING).
  - [ ] Revisar `.gitignore` (`target/`, dados locais H2 conforme política, configs IDE).
  - [ ] Atualizar `pom.xml` (`<name>`, `<description>`) para **Sabor Express PDV**.
- **Critérios de aceite:** Gitflow utilizável e convenções documentadas.
- **Commit:** `chore: setup gitflow conventions and project metadata`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 3 — Modelagem de Venda e ItemVenda (relacionamentos JPA)
- **Branch:** `feature/venda-modelagem` | **Depende de:** 11; contrato 1, 2 | **Marco:** M2 | **Labels:** `feature`, `persistence`, `model`
- **Tarefas:**
  - [ ] Entidade `Venda` (id, dataHora, cliente opcional, itens, total, status/forma de pagamento).
  - [ ] Entidade `ItemVenda` (produto, quantidade, preçoUnitário, subtotal) com relacionamentos para `Venda`/`Produto`.
  - [ ] Integridade (cascade/orphanRemoval conforme desenho) e total consistente.
  - [ ] `VendaRepository`/`VendaRepositoryImpl` (salvar, listar/buscar).
- **Critérios de aceite:** Modelo relacional coerente no H2; venda persiste com itens; integridade referencial preservada.
- **Commit:** `feat(venda): add venda and itemvenda entities with relationships`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 6 — Regras de negócio e transação atômica de venda
- **Branch:** `feature/regra-venda-transacao` | **Depende de:** 1, 3 | **Marco:** M3 | **Labels:** `feature`, `business`, `persistence`
- **Tarefas:**
  - [ ] `VendaService`: validar itens, calcular total, verificar disponibilidade/estoque e registrar venda.
  - [ ] Baixa/ajuste de estoque na mesma operação lógica.
  - [ ] Transação atômica com `commit` em sucesso e `rollback` em exceção.
  - [ ] Exceções de negócio sem expor detalhes de persistência na View.
  - [ ] Alinhar regra de estoque com Gusleme (Card 18) via contrato/service.
- **Critérios de aceite:** Venda atômica; falha não deixa estado parcial; sem SQL injection.
- **Commit:** `feat(service): add transactional sale flow with stock validation`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 16 — Status e forma de pagamento da venda
- **Branch:** `feature/venda-status-pagamento` | **Depende de:** 3 | **Marco:** M3 | **Labels:** `feature`, `model`, `ui`
- **Tarefas:**
  - [ ] Campos de forma de pagamento (dinheiro, cartão, Pix etc.) e status (aberto/finalizado/cancelado).
  - [ ] Expor dados para exibição/seleção no caixa e histórico.
  - [ ] Validar valores/transições no `VendaService`.
- **Critérios de aceite:** Pagamento/status registrados e exibidos corretamente.
- **Commit:** `feat(venda): add payment method and order status`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 17 — Cancelamento de venda com estorno de estoque
- **Branch:** `feature/venda-cancelamento` | **Depende de:** 3, 6, 18 | **Marco:** M3 | **Labels:** `feature`, `business`, `persistence`
- **Tarefas:**
  - [ ] Cancelamento conforme regra definida.
  - [ ] Estorno de estoque em transação atômica (`commit/rollback`).
  - [ ] Atualização de UI/histórico via `Observer`.
- **Critérios de aceite:** Sem inconsistência entre venda e estoque.
- **Commit:** `feat(venda): cancel sale with atomic stock rollback`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 21 — Tratamento centralizado de exceções e mensagens
- **Branch:** `refactor/tratamento-excecoes` | **Depende de:** 2, 6 | **Marco:** M4 | **Labels:** `refactor`, `business`
- **Tarefas:**
  - [ ] Padronizar exceções de negócio no Service.
  - [ ] Exibir mensagens amigáveis no Controller/View; log técnico quando útil.
  - [ ] Garantir rollback e feedback em falhas de persistência.
- **Critérios de aceite:** Aplicação estável; sem vazamento de detalhes internos.
- **Commit:** `refactor(service): centralize business exception handling`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 23 — Diagramas Mermaid atualizados (arquitetura e sequência)
- **Branch:** `docs/diagramas-pdv-lanches` | **Depende de:** implementação das entidades/fluxos | **Marco:** M4 | **Labels:** `docs`
- **Tarefas:**
  - [ ] Diagrama de classes com `Categoria`, `Cliente`, `Venda`, `ItemVenda` e relacionamentos.
  - [ ] Diagrama de sequência da venda (validação, transação, baixa/estorno de estoque, Observer/UI).
  - [ ] Consistência com código real e com nome **Sabor Express PDV**.
- **Critérios de aceite:** Diagramas refletem a implementação.
- **Commit:** `docs: update architecture and sale sequence diagrams`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 9 — Documentação técnica e diagramas (apoio/integração)
- **Branch:** `docs/pdv-lanches` | **Depende de:** 23 | **Marco:** M4 | **Labels:** `docs`
- **Tarefas:**
  - [ ] Atualizar `README.md` (contexto Sabor Express PDV, execução, estrutura, entidades, fluxos).
  - [ ] Integrar diagramas Mermaid e registrar decisões arquiteturais.
  - [ ] Registrar evidências de Gitflow/commits e divisão de tarefas (alinhar com Gusleme no Card 24).
- **Critérios de aceite:** Documentação alinhada ao código e instruções válidas.
- **Commit:** `docs: update README and diagrams for Sabor Express PDV`
- **Status:** ⬜ | **PR/Evidência:** ___

### Card 25 — Checklist de revisão e conformidade com a rubrica
- **Branch:** `chore/rubrica-checklist` | **Depende de:** 10 | **Marco:** M4 | **Labels:** `chore`, `release`
- **Tarefas:**
  - [ ] Verificar MVC, Observer/Repository, ORM/`commit`-`rollback`, ausência de SQL concatenado.
  - [ ] `.\mvnw.cmd clean package` e smoke test das telas principais.
  - [ ] Confirmar nome **Sabor Express PDV** aplicado (UI, `pom.xml`, README).
  - [ ] Consolidar PRs em `develop` e merge estável em `main` (coordenação).
- **Critérios de aceite:** Atendimento à rubrica e projeto estável para apresentação.
- **Commit:** `chore: rubric compliance checklist and release prep`
- **Status:** ⬜ | **PR/Evidência:** ___

---

## 7. Todos — trabalho compartilhado

### Card 10 — Integração, revisão e entrega (Gitflow)
- **Responsáveis:** Todos (coordenação: **Jeniffer**) | **Fluxo:** `feature/* → develop → main` | **Marco:** M4 | **Labels:** `chore`, `release`
- **Tarefas:**
  - [ ] Cada um abre PR da sua feature para `develop` com descrição e checklist.
  - [ ] Revisão cruzada (mín. 1 revisor de outra trilha).
  - [ ] Resolver conflitos (autor da branch; acionar dono se arquivo compartilhado).
  - [ ] Validar build/execução (`.\mvnw.cmd clean package` / `exec:java`) e aderência à rubrica.
  - [ ] Merge estável em `main` e tag/release se aplicável.
- **Critérios de aceite:** `main` estável, funcionalidades integradas, histórico coerente e colaborativo.
- **Commit:** `chore: integrate features and prepare release`
- **Status:** ⬜ | **PR/Evidência:** ___

---

## 8. Checklist final de avaliação (rubrica)

- [ ] **MVC:** View sem acesso a banco; Controller orquestra; Repository isola persistência.
- [ ] **Padrões:** Observer para atualização reativa; contratos/repositórios desacoplados.
- [ ] **Persistência:** ORM coerente; `commit/rollback`; sem SQL injection.
- [ ] **UI:** Funcional e responsiva; dados consistentes após CRUD e vendas.
- [ ] **Engenharia:** Gitflow, Conventional Commits, README/diagramas, colaboração.
- [ ] **Identidade:** Nome **Sabor Express PDV** aplicado na aplicação e documentação.
- [ ] **Execução:** `.\mvnw.cmd clean package` e smoke test OK; instruções no README válidas.
