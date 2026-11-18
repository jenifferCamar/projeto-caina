# Contribuindo — Sabor Express PDV

Este projeto segue **Gitflow** e **Conventional Commits**.

## Branches
- `main`: versão estável (produção/demonstração).
- `develop`: integração contínua das features.
- `feature/*`, `fix/*`, `docs/*`, `chore/*`, `refactor/*`, `test/*`: trabalho por card/issue.

## Fluxo
1. Crie a branch a partir de `develop`: `git checkout -b feature/nome-curto develop`
2. Faça commits pequenos e semânticos.
3. Abra PR para `develop` com revisão cruzada (mín. 1 revisor de outra trilha).
4. Após aprovação e merge em `develop`, a integração em `main` ocorre nos marcos (M1–M4).

## Conventional Commits
Formato: `tipo(escopo): descrição curta`

Tipos: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `revert`.

Exemplos:
- `feat(cliente): implement cliente repository`
- `fix(venda): corrige calculo de total`
- `docs: atualiza README para Sabor Express PDV`

## Regras
- View não acessa banco; Controller apenas orquestra; persistência isolada no Repository.
- Usar padrão Observer para atualização reativa de tabelas (Swing).
- Transações atômicas (`commit`/`rollback`) na camada de persistência.
- Sem SQL concatenado (usar JPA/JPQL/parametrizados).
- Commits no padrão semântico; PR com checklist de build/testes.
