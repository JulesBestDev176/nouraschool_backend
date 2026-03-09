# Protection des branches (main / develop)

La CI est définie dans [.github/workflows/ci.yml](../.github/workflows/ci.yml). Pour que les merges respectent la qualité, configurez la **branch protection** dans GitHub (ou GitLab).

## GitHub

1. **Settings** → **Branches** → **Add branch protection rule** (ou modifier une règle existante).
2. **Branch name pattern** : `main` puis une seconde règle pour `develop`.
3. Cochez au minimum :
   - **Require a pull request before merging**
   - **Require status checks to pass before merging** → sélectionnez le job **build** (ou **CI**) du workflow.
   - **Require branches to be up to date before merging** (recommandé).
4. Optionnel : **Require conversation resolution before merging**, **Do not allow bypassing the above settings**.

## GitLab

1. **Settings** → **Repository** → **Protected branches**.
2. Protéger **main** et **develop** :
   - **Allowed to merge** : Maintainers (ou plus restrictif).
   - **Allowed to push** : No one (forcer les MR) ou Maintainers.
   - **Allowed to force push** : désactivé.
3. Dans **CI/CD** → **Pipelines**, le pipeline doit passer (lint, test, coverage, build, scan) avant merge.

## Résumé

| Branche   | Protection recommandée                                      |
|----------|--------------------------------------------------------------|
| `main`   | MR obligatoire, status check CI, pas de push direct          |
| `develop`| MR obligatoire, status check CI, pas de push direct (optionnel) |

Une fois la protection activée, les pushs / MR qui ne passent pas la CI (Checkstyle, SpotBugs, PMD, tests, couverture ≥ 80 %, build, scan image) ne pourront pas être mergés.
