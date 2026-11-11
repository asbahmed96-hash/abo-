# ABO NESMA 🦅 (phase 1)
Offline Android study assistant. 
## Upload to GitHub + build APK
1. Create an empty repo, push this folder (`git init && git add . && git commit -m init && git push`).
2. Actions tab -> "Android CI" -> run. Download artifact `ABO-NESMA-debug-apk`.
Workflow installs Gradle 8.9 itself (no wrapper jar bundled).
## NOT built or tested yet
Nothing here was compiled or run (no Android SDK/Gradle/network in the authoring sandbox). First CI run may need small fixes.
## Missing vs spec
Full UI screens, exams UI/timer, flashcard UI, translation dictionaries, error review UI, TXT/JSON file picker export (Exporter logic exists), OCR traineddata (ara/eng) must be added to app/src/main/assets/tessdata/.
