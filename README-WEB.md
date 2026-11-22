# NariBondhu web preview

A no-build, browser-based NariBondhu companion site with an optional GPT educational assistant.

## Deploy to Vercel

1. Import this GitHub repository into Vercel (or use the existing Vercel project).
2. Set **Framework Preset** to **Other**.
3. Set **Root Directory** to the repository root (`./`).
4. Leave **Build Command** and **Install Command** empty.
5. Set **Output Directory** to `web` (also defined in the root `vercel.json`).
6. Deploy the branch or merge the pull request into `main`.

The site itself is static. The `api/chat.js` file is a Vercel Node.js Function with a 30-second maximum duration; no npm package is required.

## Configure the GPT assistant

The assistant will not return model-generated answers until the site owner adds a server-side API key.

1. Create an OpenAI API key in the [OpenAI API dashboard](https://platform.openai.com/api-keys).
2. In Vercel, open **Project → Settings → Environment Variables**.
3. Add `OPENAI_API_KEY` and paste the key as its value.
4. Select **Production** (and Preview too if you want preview deployments to use GPT).
5. Optionally add `OPENAI_MODEL`; the default is `gpt-6-luna` (cost-sensitive model).
6. Save and redeploy the Production deployment.

**Never put the key in `web/app.js`, HTML, GitHub, or any `NEXT_PUBLIC_*` variable.** The browser calls the same-origin `/api/chat` endpoint; the function keeps the key on the server. OpenAI API billing is separate from a ChatGPT subscription. Set project usage limits and monitor API spend.

## GPT privacy design

- GPT is **optional** and starts disabled. The user must explicitly opt in.
- Only the text in the current chat session is sent to the OpenAI API. The user's period diary, symptom log, profile, files and reports are not attached or automatically sent.
- Chat history exists only in JavaScript memory in the current page. NariBondhu does not write chat messages to localStorage or a database. Reloading or clearing the chat removes the local conversation.
- The endpoint sends `store: false` to the Responses API and does not create a persistent OpenAI Conversation.
- OpenAI states that API data is not used to train models by default. However, abuse-monitoring logs may retain prompts and responses for up to 30 days by default; `store: false` does **not** turn off abuse-monitoring retention. See [OpenAI API data controls](https://platform.openai.com/docs/guides/your-data).
- Users are warned not to enter their name, address, phone number, ID, photographs, files or personal medical reports.
- Users can withdraw AI consent from **Profile & Privacy** at any time. Withdrawing consent clears the current in-page chat and cancels an active browser request where possible.
- Browser health entries remain in localStorage and are not encrypted by this demo. This is not a secure medical-record system.

## Medical safety

The server runs a deterministic safety check based on the Android app's `ClinicalRulesEngine.kt` **before** calling GPT:

- **RED:** emergency red flags bypass GPT. The response directs the user to Bangladesh emergency 999 and the nearest hospital emergency department. BondhuHelp remains available for follow-up support; routine booking must never delay emergency care.
- **AMBER:** persistent/uncertain symptoms, diagnosis or medication requests, fertility concerns and personal report interpretation bypass GPT and direct the user to BondhuHelp for a qualified clinician.
- **GREEN:** GPT may answer general educational questions only. It must not diagnose, prescribe, interpret personal reports or reassure users that serious symptoms are harmless.

This is a safety layer, not a clinically validated diagnostic system. Keyword checks can miss context or produce false positives. BondhuHelp in this browser preview is a care-preparation page; live provider verification and booking are not connected. Emergency care must be obtained directly when needed.

## Operational safeguards and limitations

- The endpoint limits message size/history, checks same-origin browser requests, uses `store: false`, sets no-store response headers and applies a best-effort per-instance request limit.
- Serverless instances do not share in-memory rate-limit state. Configure Vercel Firewall rate limits before opening the endpoint to a broad public audience.
- "24/7" means the chat can be opened at any time; successful replies still depend on a configured key, API billing, Vercel availability, internet access and OpenAI service availability.
- No Firebase authentication, Firestore sync, live doctor verification or appointment booking is claimed by this web preview.

## Why the output directory is `web`

The repository contains Android project files and an existing `public/assets/aistudio` PDF. Vercel serves the separate `web/` output directory for the site while the serverless chat endpoint is deployed from the root `api/` directory.
