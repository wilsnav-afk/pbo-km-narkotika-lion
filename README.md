# Your part: VIEW layer (GUI Designer)

You own everything the user sees: the console menus and tables, plus the
JavaFX window (that one is a +5 bonus). Your files are in `files/`,
already in the right folder structure.

## Your files

```
src/view/ConsoleView.java   menus, tables, prompts - contains NO business logic
src/view/JavaFXView.java    the GUI window: table, dialogs, search bar
run-gui.bat                 starts the GUI on Windows
```

(The stylesheet `kms.css` belongs to Member A; your JavaFXView loads it
at startup, and falls back to the default look when it's missing.)

## Setup (once)

```bash
git clone <repo-url>
cd <repo-folder>
git config user.name  "Saladin"
git config user.email "agamsaladin26@gmail.com"     # must match your GitHub account!
```

**Wait until Member A's model PR is merged into `develop`**, then:

```bash
git checkout develop && git pull
git checkout -b feature/view
```

## How to commit

You have the finished files. To get 10+ real commits, add each file in
stages: paste part of it, commit, paste more, commit.

Commit in this order with these exact messages:

1. `docs: add console wireframe mockup to README` - the wireframe section in the project README
2. `feat: add ConsoleView skeleton with main menu` - class + showMenu
3. `feat: render verdict list as aligned table` - showVerdictList + the cut helper
4. `feat: add detail view and message helpers` - showDetail, showMessage
5. `feat: add statistics display` - showStatistics
6. `feat: add interactive form for new verdicts` - readVerdictForm
7. `feat: add input wrappers so controller never touches scanner` - askText/askInt/askDouble/askChoice
8. `feat: add yes-no confirmation prompt` - confirm
9. `feat: add JavaFX window with sortable verdict table` - JavaFXView.java: show(), buildTable, columns
10. `feat: add search bar and toolbar actions to GUI` - buildToolbar, runSearch, refresh
11. `feat: add add/edit/delete dialogs to GUI` - the three dialog methods
12. `feat: add statistics dialog and export to GUI` - showStatistics, exportStatistics
13. `feat: wire theme loading and severity badges into GUI` - buildHeader, styleDialog, categoryColumn (the kms.css file itself is Member A's commit)
14. `fix: format fines with thousand separators in table` - the fineColumn cell factory
15. `docs: document JavaFX setup and run commands` - run-gui.bat

Note: JavaFXView.java only compiles when the JavaFX SDK is in `lib/`
(download link + instructions are in the project README). If you skip
that locally, commits still work - CI compiles it for you.

When done:

```bash
git push -u origin feature/view
```

Then open a Pull Request `feature/view` → `develop` and ask a teammate
to review it.

## Your video segment (3:45 - 4:45)

- State clearly: "the View never accesses the Model directly - it only
  receives data that already came through the Controller"
- Show the list of all 55 verdicts in the neat console table
- Show the JavaFX window: sortable table, search, the add dialog
  (mention this is the +5 bonus feature)
- Demo the delete flow with its confirmation
