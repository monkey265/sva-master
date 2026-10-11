# SVA Master

**SVA Master** is an offline, privacy-first Android reference, cheatsheet, interactive sandbox, and quiz application for **SystemVerilog Assertions (SVA)** based on IEEE 1800. Built with Kotlin, Jetpack Compose, and Material 3 Expressive.

---

## ⚡ Features

- **📑 SVA Cheatsheet & Quick Reference**:
  - **Directives Overview**: `assert property`, `cover property`, `assume property`, `restrict property` with behavior breakdown for Dynamic Simulation vs Formal Verification.
  - **Anatomy & Controls**: Clocking, asynchronous reset, `disable iff`, and clean assertion scoping.
  - **Implication & Branching**: Overlapping (`|->`), non-overlapping (`|=>`), `if-else`, and temporal logic combinators.
  - **Linear Temporal Logic (LTL)**: Weak vs Strong semantics (`always`, `s_eventually`, `until`, `s_until`, `nexttime`, `s_nexttime`).
  - **Aborts**: `accept_on`, `reject_on`, `sync_accept_on`, `sync_reject_on`.
  - **Sequence Operators & Repetitions**: Delays (`##N`, `##[min:max]`), consecutive repetitions (`[*N]`), goto repetitions (`[->N]`), non-consecutive repetitions (`[=N]`), `intersect`, `throughout`, `within`, and `first_match`.
  - **Sampled Value System Functions**: `$past`, `$rose`, `$fell`, `$stable`, `$changed`, `$isunknown`, `$onehot`, `$onehot0`, and `$countones`.
- **📚 Deep Search & Filterable Reference**: Instant search across all SVA operators, categories, and engineering tips.
- **⚡ Interactive Sandbox**: Pre-configured verification scenarios (handshakes, FSM legal invariants, strong liveness proofs) with syntax-highlighted code.
- **🎯 SVA Mastery Quiz**: Interactive questions testing understanding of weak vs strong operators, implication timing, formal assumption semantics, and system functions.
- **🛡️ 100% Offline & Private**: Zero internet permissions, instant startup, fast native Compose UI.
- **🌗 Dark / Light Mode**: Specialized semiconductor Silicon & Neon theme with high-contrast SystemVerilog code blocks.

---

## 🏗️ Project Architecture

- **`app`**: Android Jetpack Compose application module.
  - `model/`: SVA domains, categories, items, and quiz questions.
  - `data/`: In-memory repository with reference specs and quiz suites.
  - `theme/`: Silicon & Amber Material 3 Expressive theme.
  - `ui/`: Compose screens (`CheatsheetScreen`, `ReferenceScreen`, `SandboxScreen`, `QuizScreen`) and syntax-highlighted code components.
- **`.github/workflows/`**:
  - `code-quality.yml`: Runs automated tests, lint, and builds debug APK on pull requests and pushes to `main`.
  - `release.yml`: Automatic tagging, APK artifact packaging, and GitHub Release generation upon pushing to the `release` branch.
- **`scripts/`**:
  - `review.sh`: Local automated code reviewer and quality gate.

---

## 🚀 Building & Testing

### Prerequisites
- JDK 17+
- Android SDK (API 36, minimum SDK 26)

### Running Tests
```bash
./gradlew test
```

### Local Quality Review
```bash
./scripts/review.sh
```

### Building APK
```bash
./gradlew assembleDebug
```
The resulting APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.

---

## 📄 License
MIT License
