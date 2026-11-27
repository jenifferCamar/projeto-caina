# Checklist de Conformidade da Rubrica — Sabor Express PDV (Card 25)

Data: 2026-11-27  
Branch local validada: `develop` (`a6074a8`)  
Coordenacao: Jeniffer (Trilha C) — Card 25 / apoio Card 10

## Resultado

- [x] `.\mvnw.cmd test` (JDK 17) — **BUILD SUCCESS**, 46 tests, 0 failures, 0 errors.
- [x] Nome **Sabor Express PDV** aplicado: `pom.xml` (`<name>`, `<description>`), `MainFrame` título `Sabor Express PDV - Gestão de Lanchonete`, `README.md`.
- [x] MVC: Views Swing não acessam Repository/JPA; Controllers orquestram; Services concentram regras; Repositories isolam persistência.
- [x] Observer/Repository: `ProdutoObserver`/`VendaObserver` presentes; notificações em services/controllers/views; repositories com interfaces + impl JPA.
- [x] ORM/transação: `EntityManager` com `begin/commit/rollback` em repositories (incl. venda/cancelamento atômico).
- [x] Sem SQL concatenado/injeção: sem `createNativeQuery`, `Statement`, `executeQuery/executeUpdate` com SQL montado por string; consultas via JPA/JPQL mapeado/Criteria conforme código.
- [ ] Smoke test visual completo (`.\mvnw.cmd exec:java`) — requer execução manual da UI.
- [ ] Consolidação/merge estável em `main` — depende do Card 10 (coordenação/equipe, PRs remotos).

## Evidências

- Build/teste local (2026-11-27): `.\mvnw.cmd test` => `Tests run: 46, Failures: 0, Errors: 0, Skipped: 0`, `BUILD SUCCESS`.
- UI principal: `MainFrame` — "Sabor Express PDV - Gestão de Lanchonete".
- Persistência: H2 em `./data/pdv_db`, `persistence.xml` com `hibernate.hbm2ddl.auto=update`.
- Gitflow: PRs para `develop`, revisão cruzada, Conventional Commits.
- Histórico: Card 25 anterior em `a6bd0c8` (`docs/CHECKLIST_RUBRICA.md`), checklist restaurado/atualizado nesta branch local para revisão.

## Pendências externas

- Smoke test manual das telas principais antes da apresentação.
- Revisão/merge dos PRs abertos em `develop` e merge estável `develop -> main` (Card 10).
- Tag/release se aplicável.
