# KMS - Narcotics Court Verdicts

A Knowledge Management System for Indonesian narcotics court rulings, built in
Java with a strict Model-View-Controller architecture. The app loads a dataset
of criminal verdicts (Pid.Sus cases from district courts in East Java,
2024-2025), lets you manage them through a console menu, and produces summary
statistics over the whole knowledge base.

Final project for the Object-Oriented Programming course, even semester
2025/2026.

## Team

| Name | NIM | Class | Role | Branch |
|------|-----|-------|------|--------|
| Wilsons | 202510370110282 | International | Knowledge/DB Engineer - Model layer | `wilsnav-afk/knowledge` |
| Saladin | 202510370110141 | International | GUI Designer - View layer | `feature/view` |
| Reyhan | 202510370110213 | International | Backend Developer - Controller layer | `feature/controller` |

**Demo video:** https://www.youtube.com/watch?v=9SLOAQ4woG4

## Features

- **CRUD** - add, list, edit (sentence & fine) and delete verdicts
- **Search** - by exact case number or by (partial) defendant name
- **Filter** - by narcotic type, by court, or by sentence range in months
- **Sort** - by sentence length (Comparable) or fine amount (Comparator), both directions
- **Statistics** - totals, average sentence, average fine, most common narcotic, role distribution
- **Export** - write the statistics report to `statistics_report.txt`
- **Robust input** - every prompt survives wrong input; the app never crashes on bad data
- **Dataset preload** - 55 verdicts load automatically from `data/verdicts.csv` at startup
- **Two front-ends** - the required console menu (`app.Main`) and a bonus
  JavaFX window (`app.MainFX`) with a sortable table, add/edit/delete dialogs
  and the statistics report. Both drive the exact same controller.

## Requirements

- JDK 11 or newer (console version needs nothing else)
- For the JavaFX GUI only: the [JavaFX SDK](https://gluonhq.com/products/javafx/)
  unzipped into `lib/` so the jars sit at `lib/javafx-sdk-24.0.1/lib`
  (any recent version works - adjust the folder name in the commands below)

## Compile

From the project root:

```bash
# Windows (PowerShell)
javac --module-path "lib\javafx-sdk-24.0.1\lib" --add-modules javafx.controls -d out (Get-ChildItem -Recurse src -Filter *.java).FullName

# Linux / macOS
javac --module-path lib/javafx-sdk-24.0.1/lib --add-modules javafx.controls -d out $(find src -name "*.java")
```

Without the JavaFX SDK you can still compile everything except the GUI:
exclude `JavaFXView.java` and `MainFX.java` and drop the module flags.

## Run

```bash
# console version (no JavaFX needed at runtime)
java -cp out app.Main

# GUI version
java --module-path lib/javafx-sdk-24.0.1/lib --add-modules javafx.controls -cp out app.MainFX
```

On Windows the two batch files `run-console.bat` and `run-gui.bat` do the
same thing.

Run from the project root so the app can find `data/verdicts.csv`.
If the file is missing the app still starts, just with an empty knowledge
base - you can add verdicts through the menu.

In IntelliJ IDEA: open the project, mark `src` as Sources Root, then run
`app.Main` (or `app.MainFX` with the JavaFX VM options). Set the working
directory to the project root in the run configuration.

## Architecture

```
        keyboard input                     formatted output
             |                                    ^
             v                                    |
        +---------+   raw strings   +------------------+
        |  VIEW   | --------------> |    CONTROLLER    |
        | Console |                 |    Knowledge     |
        |  View   | <-------------- |    Controller    |
        +---------+   display data  +------------------+
                                        |         ^
                              validated |         | results
                                calls   v         |
                                    +------------------+
                                    |      MODEL       |
                                    | Verdict,         |
                                    | KnowledgeRepo,   |
                                    | VerdictStatistics|
                                    +------------------+
```

The view never touches the model. The controller validates everything coming
from the view, calls the repository, and pushes results back for display.

### Package layout

```
src/
├── model/
│   ├── LegalDocument.java       abstract base (case number, court, date)
│   ├── Verdict.java             main entity, extends LegalDocument,
│   │                            implements Comparable<Verdict>
│   ├── VerdictRepository.java   storage contract (interface)
│   ├── KnowledgeRepository.java ArrayList-backed CRUD + search + sort
│   └── VerdictStatistics.java   aggregate figures & report text
├── view/
│   ├── ConsoleView.java         menus, tables, prompts - no logic
│   └── JavaFXView.java          bonus GUI window - same controller behind it
├── controller/
│   └── KnowledgeController.java orchestrates every menu action
├── util/
│   ├── InputHandler.java        validated console input (try-catch inside)
│   └── CsvLoader.java           dataset import, skips broken rows
└── app/
    ├── Main.java                wiring + menu loop, nothing else
    └── MainFX.java              GUI entry point (bonus)
```

### Where each OOP concept lives

| Concept | Location |
|---------|----------|
| Encapsulation | every field in `Verdict`/`LegalDocument` is private; setters validate |
| Inheritance (`extends`) | `Verdict extends LegalDocument` |
| Interface (`implements`) | `KnowledgeRepository implements VerdictRepository`; `Verdict implements Comparable<Verdict>` |
| Overriding | `toString()`, `summary()`, `compareTo()` in `Verdict`; all repository methods |
| Overloading | `Verdict.display()` / `Verdict.display(boolean)` |
| Constructors | `Verdict()` no-arg and the full 12-argument version |
| Static members | `Verdict.totalCreated` + `getTotalCreated()`; all of `InputHandler` |
| ArrayList | `KnowledgeRepository`'s backing store |
| Primitive arrays | parallel `int[]`/`String[]` counting in `VerdictStatistics` |
| Exception handling | `InputHandler` retry loops, `CsvLoader` row skipping, setter validation caught in the controller |

## Console wireframe

Sketched before implementation (View layer plan):

```
==========================================================
   KNOWLEDGE MANAGEMENT SYSTEM - NARCOTICS COURT VERDICTS
==========================================================
 1. List all verdicts
 2. Add a new verdict
 ...
 0. Exit
----------------------------------------------------------
Choose an option: 1

----------------------------------------------------------------
No  Case Number               Defendant            Age Narcotic ...
----------------------------------------------------------------
1   2841/Pid.Sus/2024/PN Sby  Agus Prasetyo...     34  Crystal meth
2   2856/Pid.Sus/2024/PN Sby  Rudi Hartono...      28  Crystal meth
----------------------------------------------------------------
Total: 55 verdict(s)
```

## Dataset

`data/verdicts.csv` holds 55 verdicts, semicolon-separated (judge names
contain commas). Columns: case number, court, verdict date, defendant name,
age, narcotic type, evidence weight (g), violated article, defendant role,
sentence (months), fine (Rp), presiding judge. Derived from public narcotics
rulings (Pid.Sus) of East Java district courts, 2024-2025.

## Git workflow

- `main` - releasable code only, changes arrive via reviewed pull requests
- `develop` - integration branch
- `feature/model`, `feature/view`, `feature/controller` - one per member/layer
- Commit format: `feat: ...`, `fix: ...`, `docs: ...`, `refactor: ...`
- CI (GitHub Actions) compiles the project and boots the app on every push
  to `develop` and every PR
