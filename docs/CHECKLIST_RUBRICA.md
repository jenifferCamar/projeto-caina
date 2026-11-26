# Checklist de Conformidade da Rubrica — Sabor Express PDV (Card 25)

Data: 2026-11-26  
Branch base validada: `origin/develop` (worktree `kilo-card25`)  
Coordenacao: Jeniffer (Trilha C) — Card 10/25

## Resultado

- [x] `.\mvnw.cmd clean package` — **BUILD SUCCESS**, 25 tests, 0 failures, 0 errors.
- [x] Nome **Sabor Express PDV** aplicado: `pom.xml` (`<name>`, `<description>`), `MainFrame` título, `README.md`.
- [x] MVC: Views Swing não acessam Repository/JPA; Controllers orquestram; Services concentram regras; Repositories isolam persistência.
- [x] Observer/Repository: `ProdutoObserver`/`VendaObserver` presentes; notificações em services/controllers/views; repositories com interfaces + impl JPA.
- [x] ORM/transação: `EntityManager` com `begin/commit/rollback` em repositories (incl. venda/cancelamento atômico).
- [x] Sem SQL concatenado/injeção: sem `createNativeQuery`, `Statement`, `executeQuery/executeUpdate` com SQL montado por string; consultas via JPA/JPQL mapeado.
- [ ] Smoke test visual completo (`.\mvnw.cmd exec:java`) — requer execução manual da UI.
- [ ] Merge final para `main` — fora do escopo desta etapa (usuário pediu foco em `develop`).

## Evidências

- Build/teste: Maven Surefire/JAR, 25 testes passando.
- UI principal: `MainFrame` — "Sabor Express PDV - Gestão de Lanchonete".
- Persistência: H2 em `./data/pdv_db`, `persistence.xml` com `hibernate.hbm2ddl.auto=update`.
- Gitflow: PRs para `develop`, revisão cruzada, Conventional Commits.

## Pendências externas

- Revisão/merge dos PRs abertos em `develop` (ex. PR #51 Card 9).
- Smoke test manual das telas principais antes da apresentação.
