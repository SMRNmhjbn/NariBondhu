"use strict";

/*
 * NariBondhu GPT server endpoint.
 * OPENAI_API_KEY is read only on the server and never exposed to browser code.
 * Chat content is not written to a database or application log.
 * Responses API store:false avoids saving response application state.
 * OpenAI abuse-monitoring retention still applies by default (up to 30 days).
 */

const DEFAULT_MODEL = "gpt-5.6-luna";
const MAX_BODY_BYTES = 12000;
const MAX_MESSAGES = 8;
const MAX_USER_CHARS = 600;
const MAX_ASSISTANT_CHARS = 1400;

const RED_PATTERNS = [
  "faint", "fainting", "passed out", "unconscious", "collapse",
  "dizzy", "dizziness", "lightheaded",
  "very heavy bleeding", "heavy bleeding", "soaking", "flooding", "bleeding through pad", "clots every hour",
  "severe abdominal pain", "severe lower abdominal pain", "severe stomach pain",
  "can't breathe", "cannot breathe", "difficulty breathing", "trouble breathing",
  "bleeding in pregnancy", "bleeding while pregnant", "bleeding during pregnancy", "blackout",
  "severe pain", "unbearable pain", "sharp abdominal", "severe pelvic", "excruciating",
  "pregnant and bleeding", "pregnancy bleeding", "ectopic", "positive test and pain",
  "fever and pelvic", "high fever", "chest pain", "shortness of breath",
  "অজ্ঞান", "মাথা ঘোরা", "অতিরিক্ত রক্তপাত", "অতিরিক্ত রক্তক্ষরণ",
  "তীব্র ব্যথা", "অসহ্য ব্যথা", "গর্ভাবস্থায় রক্তপাত", "গর্ভাবস্থায় রক্তপাত",
  "গর্ভবতী অবস্থায় রক্তপাত", "গর্ভবতী অবস্থায় রক্তপাত", "শ্বাসকষ্ট", "বুক ব্যথা",
  "জ্ঞান হারানো", "প্রচুর রক্তপাত", "প্রচণ্ড রক্তপাত", "তীব্র পেটব্যথা", "অসহ্য পেটব্যথা", "শ্বাস নিতে কষ্ট"
];

const AMBER_PATTERNS = [
  "repeated unusual bleeding", "bleeding between periods", "spotting for weeks",
  "persistent pelvic", "pain for weeks", "chronic pain",
  "unusual discharge", "foul smell", "fishy", "green discharge", "yellow discharge",
  "itching", "burning", "vulvar pain", "soreness", "recurring irritation",
  "missed period", "no period for", "irregular period", "irregular cycle", "3 months", "months without",
  "fertility", "cannot conceive", "trying to conceive", "pregnant?", "am i pregnant",
  "diagnose", "do i have pcos", "prescribe", "medicine", "medication", "dosage", "tablet", "pill dose",
  "ultrasound result", "lab report", "blood test result", "hormone level",
  "should i see a doctor", "do i need a doctor", "medical advice",
  "my symptoms", "my symptom", "my report", "my ultrasound", "my test result", "my blood test",
  "i have pcos", "i have endometriosis", "i was diagnosed", "my pain", "my cramps",
  "i have pain", "i have bleeding", "i am bleeding", "my discharge",
  "my period is late", "late period", "missed my period", "should i take", "can i take", "should i stop taking",
  "অনিয়মিত মাসিক", "অনিয়মিত মাসিক", "মাসিক বন্ধ", "সাদা স্রাব", "চুলকানি", "জ্বালাপোড়া", "জ্বালাপোড়া",
  "দুর্গন্ধ", "ডাক্তার", "ওষুধ", "চিকিৎসা", "রিপোর্ট", "পিসিওএস আছে কিনা", "সন্তান",
  "আমার লক্ষণ", "আমার রিপোর্ট", "আমার আল্ট্রাসাউন্ড", "আমার ব্যথা", "আমার ক্র্যাম্প",
  "আমার রক্তপাত", "আমার স্রাব", "মাসিক দেরি", "আমার পিরিয়ড দেরি", "আমি কি ওষুধ খাব"
];

const SYSTEM_INSTRUCTIONS = [
  "You are NariBondhu GPT (নারীবন্ধু GPT), a warm, respectful educational women's-health assistant for adults in Bangladesh.",
  "SCOPE: Give general health education and help users navigate NariBondhu. Use simple, non-judgmental language. Reply in the language selected by the user (Bangla or English).",
  "NEVER diagnose, claim a user has or does not have a disease, prescribe or recommend medication, give medicine dosages, recommend changing a prescribed treatment, guarantee ovulation or fertility, or interpret a personal lab/ultrasound report.",
  "NEVER reassure a user that potentially serious symptoms are harmless. Do not provide personalised triage or treatment plans.",
  "CLINICAL ESCALATION: For persistent, worsening, uncertain, personal symptoms, diagnosis requests, medication questions, fertility concerns, or personal test/report interpretation, do not attempt to solve the clinical concern. Tell the user to open BondhuHelp and speak with a qualified gynaecologist.",
  "EMERGENCY: If any urgent red-flag symptom is mentioned, tell the user to call Bangladesh emergency 999 or go to the nearest hospital emergency department immediately. Do not suggest waiting for a routine appointment or continuing the chat.",
  "SOURCE DISCIPLINE: Prefer NariBondhu's educational library and reputable sources such as WHO, RCOG, NICE and DGHS Bangladesh. Do not invent citations or claim that a source says something unless you are confident.",
  "PRIVACY: Do not ask for the user's name, address, phone number, national ID, account details, photographs, files or medical reports. Do not request information that is not needed for general education. The user's health diary is not provided to you.",
  "Keep answers concise, practical and educational. Include a brief reminder that you are not a doctor when relevant."
].join("\n");

function classifySafety(messages) {
  const text = messages
    .filter((item) => item.role === "user")
    .map((item) => item.content.toLowerCase())
    .join("\n");

  const redMatch = RED_PATTERNS.some((pattern) => text.includes(pattern)) ||
    (/heavy bleeding.{0,60}(weak|dizz|faint)/i.test(text)) ||
    (/(pregnan|pregnancy).{0,60}(bleed|severe pain)/i.test(text)) ||
    (/(রক্তপাত|রক্তক্ষরণ).{0,35}(মাথা ঘোরা|অজ্ঞান|দুর্বল)/.test(text)) ||
    (/(গর্ভবতী|গর্ভাবস্থা).{0,35}(রক্তপাত|ব্যথা)/.test(text));

  if (redMatch) return "red";

  const amberMatch = AMBER_PATTERNS.some((pattern) => text.includes(pattern)) ||
    (/(symptom|pain|bleed|discharge|itch|লক্ষণ|ব্যথা|রক্তপাত|স্রাব).{0,50}(weeks|persistent|worsening|বারবার|দীর্ঘদিন|বাড়ছে|বাড়ছে)/i.test(text));

  if (amberMatch) return "amber";
  return "green";
}

function escalationMessage(language, tier) {
  if (tier === "red") {
    return language === "bn"
      ? "আপনার বার্তায় জরুরি চিকিৎসার প্রয়োজন হতে পারে এমন লক্ষণ রয়েছে। আমি এখানে এটি মূল্যায়ন বা চিকিৎসা পরামর্শ দিতে পারি না। এখনই বাংলাদেশে ৯৯৯-এ কল করুন অথবা নিকটস্থ হাসপাতালের জরুরি বিভাগে যান। অ্যাপের উত্তর বা সাধারণ অ্যাপয়েন্টমেন্টের জন্য অপেক্ষা করবেন না। জরুরি সহায়তা নেওয়ার পর চিকিৎসকের সঙ্গে যোগাযোগের জন্য বন্ধুহেল্প খুলতে পারেন।"
      : "Your message may describe a medical emergency. I cannot assess or advise on it here. Call Bangladesh emergency 999 now or go to the nearest hospital emergency department. Do not wait for an app response or routine appointment. Once urgent care is arranged, you can also open BondhuHelp for clinician support.";
  }
  return language === "bn"
    ? "এই বিষয়টির জন্য একজন যোগ্য চিকিৎসকের ব্যক্তিগত মূল্যায়ন প্রয়োজন। আমি রোগ নির্ণয়, ব্যক্তিগত রিপোর্ট ব্যাখ্যা বা ওষুধের পরামর্শ দিতে পারি না। অনুগ্রহ করে বন্ধুহেল্প খুলে একজন যোগ্য স্ত্রীরোগ বিশেষজ্ঞের সঙ্গে কথা বলুন।"
    : "This concern needs assessment by a qualified clinician. I cannot diagnose a condition, interpret a personal medical report or recommend medication. Please open BondhuHelp and speak with a qualified gynaecologist.";
}

function setHeaders(res) {
  res.setHeader("Content-Type", "application/json; charset=utf-8");
  res.setHeader("Cache-Control", "no-store, max-age=0");
  res.setHeader("X-Content-Type-Options", "nosniff");
  res.setHeader("Referrer-Policy", "no-referrer");
}

function sameOrigin(req) {
  const origin = req.headers.origin;
  if (!origin) return true;
  const host = req.headers["x-forwarded-host"] || req.headers.host;
  try {
    return new URL(origin).host === host;
  } catch {
    return false;
  }
}

function getBody(req) {
  if (req.body && typeof req.body === "object") return req.body;
  if (typeof req.body === "string") return JSON.parse(req.body);
  return {};
}

// Best-effort per-instance protection. Configure Vercel Firewall rate limits for
// robust global limits because serverless instances do not share this memory.
const rateBuckets = globalThis.__naribondhuGptRateBuckets ||
  (globalThis.__naribondhuGptRateBuckets = new Map());

function isRateLimited(req) {
  const ip = req.headers["x-real-ip"] ||
    String(req.headers["x-forwarded-for"] || "unknown").split(",")[0].trim();
  const now = Date.now();
  const current = rateBuckets.get(ip);
  if (!current || now - current.start > 60_000) {
    rateBuckets.set(ip, { start: now, count: 1 });
    return false;
  }
  current.count += 1;
  return current.count > 12;
}

function extractOutputText(payload) {
  if (typeof payload.output_text === "string" && payload.output_text.trim()) {
    return payload.output_text.trim();
  }
  const parts = [];
  for (const item of payload.output || []) {
    if (item.type !== "message" || !Array.isArray(item.content)) continue;
    for (const content of item.content) {
      if (content.type === "output_text" && typeof content.text === "string") {
        parts.push(content.text);
      }
    }
  }
  return parts.join("\n").trim();
}

module.exports = async function handler(req, res) {
  setHeaders(res);

  if (req.method === "GET") {
    return res.status(200).json({
      configured: Boolean(process.env.OPENAI_API_KEY),
      model: process.env.OPENAI_MODEL || DEFAULT_MODEL
    });
  }

  if (req.method !== "POST") {
    res.setHeader("Allow", "GET, POST");
    return res.status(405).json({ code: "method_not_allowed" });
  }

  if (!sameOrigin(req)) {
    return res.status(403).json({ code: "origin_not_allowed" });
  }

  const contentLength = Number(req.headers["content-length"] || 0);
  if (contentLength > MAX_BODY_BYTES) {
    return res.status(413).json({ code: "request_too_large" });
  }

  let body;
  try {
    body = getBody(req);
  } catch {
    return res.status(400).json({ code: "invalid_json" });
  }

  if (Buffer.byteLength(JSON.stringify(body || {}), "utf8") > MAX_BODY_BYTES) {
    return res.status(413).json({ code: "request_too_large" });
  }

  if (body.consent !== true) {
    return res.status(403).json({ code: "consent_required" });
  }

  if (isRateLimited(req)) {
    return res.status(429).json({ code: "rate_limited" });
  }

  if (!Array.isArray(body.messages) || body.messages.length < 1 || body.messages.length > MAX_MESSAGES) {
    return res.status(400).json({ code: "invalid_messages" });
  }

  const messages = [];
  for (const item of body.messages) {
    if (!item || !["user", "assistant"].includes(item.role) || typeof item.content !== "string") {
      return res.status(400).json({ code: "invalid_messages" });
    }
    const max = item.role === "user" ? MAX_USER_CHARS : MAX_ASSISTANT_CHARS;
    const content = item.content.trim().slice(0, max);
    if (!content) continue;
    messages.push({ role: item.role, content });
  }

  if (!messages.length || messages[messages.length - 1].role !== "user" || !messages.some((item) => item.role === "user")) {
    return res.status(400).json({ code: "invalid_messages" });
  }

  const language = body.language === "bn" ? "bn" : "en";
  const safety = classifySafety(messages);

  // RED and AMBER messages never reach OpenAI.
  if (safety === "red" || safety === "amber") {
    return res.status(200).json({
      safety,
      message: escalationMessage(language, safety),
      emergency: safety === "red",
      bondhuHelp: true
    });
  }

  const apiKey = process.env.OPENAI_API_KEY;
  if (!apiKey) {
    return res.status(503).json({ code: "not_configured" });
  }

  try {
    const upstream = await fetch("https://api.openai.com/v1/responses", {
      method: "POST",
      headers: {
        "Authorization": "Bearer " + apiKey,
        "Content-Type": "application/json"
      },
      signal: AbortSignal.timeout(25_000),
      body: JSON.stringify({
        model: process.env.OPENAI_MODEL || DEFAULT_MODEL,
        instructions: SYSTEM_INSTRUCTIONS + "\n\nThe selected interface language is " + (language === "bn" ? "Bangla (বাংলা)" : "English") + ".",
        input: messages,
        max_output_tokens: 420,
        store: false
      })
    });

    if (!upstream.ok) {
      // Do not log or return user content or provider response bodies.
      return res.status(502).json({ code: "upstream_unavailable" });
    }

    const payload = await upstream.json();
    const message = extractOutputText(payload);
    if (!message) {
      return res.status(502).json({ code: "empty_response" });
    }

    return res.status(200).json({
      safety: "green",
      message: message.slice(0, 5000),
      emergency: false,
      bondhuHelp: false
    });
  } catch {
    return res.status(502).json({ code: "upstream_unavailable" });
  }
};

module.exports.classifySafety = classifySafety;
module.exports.escalationMessage = escalationMessage;
module.exports.DEFAULT_MODEL = DEFAULT_MODEL;
