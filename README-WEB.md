# NariBondhu web preview

A no-build, browser-based companion UI added alongside the existing Android app.

## Deploy to Vercel

1. Import this GitHub repository into Vercel (or use the existing Vercel project).
2. Set **Framework Preset** to **Other**.
3. Set **Root Directory** to the repository root (`./`).
4. Leave **Build Command** and **Install Command** empty.
5. Set **Output Directory** to `web` (also defined in the root `vercel.json`).
6. Deploy the branch or merge the pull request into `main` to trigger the production deployment.

There is no `package.json`, Node build, Gemini key, Google AI Studio token or server function required for this static preview.

## Why the output directory is `web`

The repository already contains a `public/assets/aistudio` PDF. The website deliberately uses a separate `web/` output directory so Vercel serves only the website files and does not publish pre-existing files from the Android/AI Studio asset folder as part of this web deployment.

## What works in this preview

- Responsive dashboard and navigation
- English/Bangla interface toggle
- Cycle and spotting calendar
- Period, symptom and wellbeing entries
- Sample preview data with a clear label
- Browser-local persistence, JSON export and clear-data controls
- Educational library based on the Android repository's clinical content catalogue
- Care-preparation checklist and transparent provider-directory status

## What is not connected

- Firebase Authentication, Firestore or Android app data sync
- Gemini / NariBondhu AI responses
- Live doctor verification, provider availability or appointment booking
- Push notifications or cross-device backup

The AI page intentionally does not simulate answers. A future AI integration should use a server-side endpoint, keep provider credentials out of browser code, and preserve the consent and clinical-safety rules in the Android implementation.

## Privacy notes

This is a frontend demo, not a secure medical-record system. Entries are saved in this browser's `localStorage`; they are not encrypted by this demo, are not sent to a server, and do not sync to Firebase. Avoid entering sensitive information on a shared device. Exported JSON files may contain personal information.

## Health information

The library adapts the topics and safety framing in `app/src/main/java/com/example/data/ClinicalContentCatalog.kt`. It is general education only, not diagnosis, treatment or a substitute for a qualified clinician. For urgent symptoms, seek emergency care rather than waiting for an app response.
