(() => {
  "use strict";

  const STORAGE_KEY = "naribondhu.web-preview.v1";
  const $ = (selector, root = document) => root.querySelector(selector);
  const $$ = (selector, root = document) => Array.from(root.querySelectorAll(selector));

  const COPY = {
    en: {
      navYourSpace:"YOUR SPACE", navHome:"Home", navHealth:"My Health", navLearn:"Health Library", navHelp:"BondhuHelp", navAi:"NariBondhu AI",
      privateDemo:"Private demo", localOnly:"Saved on this device", navProfile:"Profile & privacy", topEyebrow:"YOUR PRIVATE SPACE", localBadge:"Local preview", discreet:"Discreet mode",
      samplePreview:"Sample preview", sampleNotice:" — these illustrative entries are not your personal health records.", useMyData:"Use my own data",
      heroEyebrow:"A LITTLE SPACE FOR YOU", heroTitle:"Understand your body.<br>Care at your own pace.", heroText:"A warm, private place to notice your cycle, understand symptoms and find the right information — without judgement.",
      logPeriod:"Log a period", exploreLibrary:"Explore the health library", heroTrust:"Your demo entries stay in this browser", gentleReminder:"You deserve gentle care",
      yourOverview:"YOUR OVERVIEW", cycleOverview:"Your cycle at a glance", viewTracker:"Open tracker", cycleEstimate:"CYCLE ESTIMATE", cycleDay:"cycle day",
      nextEstimate:"NEXT PERIOD ESTIMATE", estimatedStart:"Estimated start", estimateDisclaimer:"An estimate only — cycles can naturally vary.", averageCycle:"Average cycle", periodsLogged:"Periods logged",
      quickActions:"QUICK ACTIONS", logPeriodHint:"Record a period or spotting", logSymptom:"Log a symptom", logSymptomHint:"Note how you feel today", logWellness:"Daily wellbeing", logWellnessHint:"Mood, sleep and energy",
      madeForRealLife:"MADE FOR REAL LIFE", yourHealthSpace:"Your health, your way", featureCycleTitle:"Cycle & diary", featureCycleText:"Keep period dates and personal notes together.",
      featureLearnTitle:"Learn with confidence", featureLearnText:"Read simple guides on PCOS, PMS and intimate health.", featureCareTitle:"Prepare for care", featureCareText:"Organise your concerns before speaking with a clinician.",
      safetyTitle:"When to seek urgent care", safetyText:"Very heavy bleeding with dizziness or fainting, severe pelvic pain, or bleeding/pain during pregnancy needs urgent medical assessment. Do not wait for an app response.",
      call999:"Call 999", bottomPrivacy:"Your health is personal. This browser demo does not send your entries to a server.",
      trackerEyebrow:"YOUR PERSONAL JOURNAL", trackerTitle:"My Health", trackerIntro:"Track your cycle and daily patterns in one calm, private space.",
      cycleCalendar:"CYCLE CALENDAR", periodDays:"Period", spotting:"Spotting", estimatedDays:"Estimated", cycleInsights:"CYCLE INSIGHTS", lastPeriod:"Last period started",
      trackerEstimateNote:"Estimates need at least two recorded period starts and are not a medical prediction.", recentEntries:"RECENT ENTRIES", periodHistory:"Period history",
      noticePatterns:"NOTICE YOUR PATTERNS", dailyDiary:"Daily diary", wellbeing:"Wellbeing", symptom:"Symptom", bodyNotes:"BODY NOTES", symptomDiary:"Symptom diary",
      dailyCheckin:"DAILY CHECK-IN", wellbeingLog:"Wellbeing log", trackerPrivacy:"Entries are stored in this browser only. This demo does not sync with your Android app or Firebase account.",
      libraryEyebrow:"KNOWLEDGE WITHOUT JUDGEMENT", libraryTitle:"Health Library", libraryIntro:"Clear, approachable information to help you understand your body and prepare for a clinician conversation.",
      learnAtYourPace:"LEARN AT YOUR PACE", libraryFeatureTitle:"Small steps. Better questions. More confidence.", libraryFeatureText:"Use these guides as a starting point, not a substitute for personalised medical advice.",
      filterAll:"All topics", filterCycle:"Cycle health", filterIntimate:"Intimate health", clinicalNoteTitle:"A note about health information",
      clinicalNoteText:"These guides are educational. They cannot diagnose a condition, interpret your personal test results or recommend medicines. Speak with a qualified clinician about your own health.",
      careEyebrow:"SUPPORT FOR YOUR NEXT STEP", careTitle:"BondhuHelp", careIntro:"Prepare for a thoughtful conversation with a qualified healthcare professional.",
      careStartsHere:"CARE STARTS WITH A CONVERSATION", careHeroTitle:"You deserve to be heard.", careHeroText:"A clear timeline of your symptoms and questions can make a doctor visit feel easier.",
      prepareVisit:"Prepare for a visit", beforeVisitTitle:"Before your visit", beforeVisitText:"Write down when symptoms started, how often they happen, and what makes them better or worse.",
      openChecklist:"Open checklist", verifyCareTitle:"Choose care carefully", verifyCareText:"Check a clinician's current BMDC registration and confirm the clinic, fees and appointment details directly.",
      visitBmdc:"Visit BMDC website", shareChoiceTitle:"Your information, your choice", shareChoiceText:"Only share the health notes or reports you feel comfortable discussing with your clinician.",
      privacySettings:"Privacy settings", directoryNotLiveTitle:"Live doctor booking is not connected in this web preview.",
      directoryNotLiveText:"The Android repository contains a provider catalogue, but this browser demo does not verify provider credentials, availability or fees. It therefore does not display those entries as live recommendations or accept bookings.",
      urgentCareTitle:"Need urgent help?", urgentCareText:"For severe symptoms or immediate danger, go to the nearest emergency department. In Bangladesh, call 999 for emergency assistance.",
      assistantEyebrow:"EDUCATION, WITH SAFETY FIRST", assistantTitle:"NariBondhu AI", assistantIntro:"A future educational companion designed to explain women's health topics in simple English or বাংলা.",
      notConnected:"Not connected in this demo", aiDemoTitle:"Your AI guide needs a secure connection.",
      aiDemoText:"This website is running without Google AI Studio or Gemini API tokens. No question is sent to an AI service, and no generated medical answer is simulated.",
      readLibrary:"Read the health library", aiPrincipleOne:"Education, not diagnosis", aiPrincipleOneText:"Explain general health topics; never diagnose a disease or prescribe medicines.",
      aiPrincipleTwo:"Consent before sharing", aiPrincipleTwoText:"A future integration must clearly explain what information is sent and ask permission first.",
      aiPrincipleThree:"Urgent symptoms come first", aiPrincipleThreeText:"Potential emergency symptoms should lead to urgent-care guidance, not a long chat.",
      privacyEyebrow:"YOUR CHOICES MATTER", profileTitle:"Profile & privacy", profileIntro:"Manage this browser preview and understand where your information is stored.",
      previewProfile:"PREVIEW PROFILE", guestProfile:"Your private space", guestProfileText:"No account is signed in. This is a local browser preview.",
      languageSetting:"Language", languageSettingText:"Choose the language for the interface.", discreetSetting:"Discreet mode", discreetSettingText:"Blur cycle and symptom details on this screen.",
      privacyAtAGlance:"PRIVACY AT A GLANCE", localStorageTitle:"Stored in this browser",
      privacyListOne:"Period, symptom and wellbeing entries use this browser's local storage.",
      privacyListTwo:"This demo does not send entries to Firebase, Gemini or another server.",
      privacyListThree:"There is no sign-in, analytics tracker or advertising script in this demo.",
      privacyCaution:"This is not encrypted storage or a secure medical record. Avoid entering sensitive information on a shared device. Browser data may be lost if you clear site data.",
      yourData:"YOUR DATA", dataControlsTitle:"Export or clear your entries", dataControlsText:"Keep a copy of your browser entries or remove them from this device.",
      exportData:"Export my data", clearData:"Clear all local data", aboutTagline:"Understand your body. Recognise your needs. Reach the right care.",
      footerDisclaimer:"Educational demo only · Not a medical service", footerPrivacy:"Privacy & data",
      addPeriodTitle:"Log a period or spotting", addSymptomTitle:"Log how you feel", addWellnessTitle:"Daily wellbeing check-in",
      startWithOwnTitle:"Start with your own entries?", startWithOwnText:"This preview currently contains clearly labelled sample entries. Clear those examples before adding your own information.",
      clearSamples:"Clear sample entries", keepPreview:"Keep sample preview", saveEntry:"Save entry", cancel:"Cancel",
      kindLabel:"Entry type", periodOption:"Period", spottingOption:"Spotting", startDateLabel:"Start date", endDateLabel:"End date",
      endDateHint:"For a period, add the last day of bleeding. For spotting, the end date is optional.", noteLabel:"Note (optional)", notePlaceholder:"Anything you would like to remember…",
      symptomLabel:"Symptom", severityLabel:"How strong was it?", mild:"Mild", moderate:"Moderate", severe:"Severe", dateLabel:"Date",
      moodLabel:"Mood", sleepLabel:"Sleep (hours)", energyLabel:"Energy", low:"Low", steady:"Steady", good:"Good", moodGreat:"Good", moodOkay:"Okay", moodLow:"Low", moodAnxious:"Anxious",
      today:"Today", tomorrow:"Tomorrow", inDays:"In {n} days", daysAgo:"{n} days ago", noEstimate:"Not enough entries yet",
      noPeriods:"No period entries yet. Add your first start date to begin your cycle journal.", noSymptoms:"No symptoms logged yet. Your notes will appear here.", noWellness:"No wellbeing check-ins yet. A small daily note can help you notice patterns.",
      periodEntry:"Period", spottingEntry:"Spotting", readGuide:"Read guide", minutes:"min read", articleDisclaimer:"Educational information only",
      calendarEmpty:"No entry recorded for this day.", calendarPeriod:"Period recorded", calendarSpotting:"Spotting recorded", calendarEstimate:"Estimated period window",
      periodSaved:"Your entry has been saved in this browser.", symptomSaved:"Your symptom note has been saved.", wellnessSaved:"Your wellbeing check-in has been saved.",
      demoCleared:"Sample entries cleared. You can now add your own.", dataCleared:"All local entries have been cleared.", exportReady:"Your data export is ready.",
      exportWarning:"Your export may contain personal health information. Store it somewhere private.",
      clearConfirmTitle:"Clear all local entries?", clearConfirmText:"This removes the period, symptom and wellbeing entries saved by this demo in this browser. There is no server copy to recover.",
      confirmClear:"Clear all entries", dataReset:"All local entries have been cleared.",
      checklistTitle:"Prepare for your appointment", checklistIntro:"A short note can help you explain what has been happening. Take only what you feel comfortable sharing.",
      checklistItems:["When did the symptom or change begin?","How often does it happen, and how long does it last?","What makes it better or worse?","What medicines or supplements are you currently taking?","Bring relevant reports or test results, if you have them."],
      copyChecklist:"Copy checklist", checklistCopied:"Checklist copied.", checklistCopyFail:"Copy is not available here; you can select and copy the checklist.",
      aiSafetyTitle:"NariBondhu AI safety principles", aiSafetyText:"The Android implementation defines an educational-only scope. It must not diagnose, prescribe or interpret personal reports. It should encourage qualified care for persistent, worsening or uncertain concerns and urgent care for red-flag symptoms.",
      urgentReminder:"If you have very heavy bleeding with dizziness/fainting, severe pelvic pain, or pregnancy with bleeding/pain, seek urgent medical care now.",
      cycleInProgress:"Your cycle is in progress", cycleNeedData:"Start your cycle journal", cycleNeedDataText:"Add period dates to see a personal overview. Predictions stay hidden until there is enough history.",
      cycleBasedOn:"Based on {n} recorded cycle interval(s)", noLastPeriod:"No period start recorded", nextIn:"About {n} days from now", nextToday:"Estimated around today", nextPast:"The estimate date has passed",
      noHistory:"No entries yet", dayEntry:"{date}: {detail}", sampleTag:"Sample", savedTag:"Saved", severitySuffix:"severity",
      articlePcosTitle:"Understanding PCOS & insulin resistance", articlePmsTitle:"Understanding PMS & recurring cycle symptoms",
      articleCycleTitle:"Why cycles vary & spotting vs. menstruation", articleIntimateTitle:"Vaginal and vulvar health: discharge & hygiene",
      articlePcosSummary:"PCOS can affect ovulation, cycle regularity and metabolism. Learn common signs and what to discuss with a doctor.",
      articlePmsSummary:"Notice recurring physical and emotional changes before a period, and learn when symptoms need clinical support.",
      articleCycleSummary:"Cycles are not always 28 days. Understand normal variation and why spotting is logged separately.",
      articleIntimateSummary:"Understand common discharge patterns, gentle external hygiene and changes worth discussing with a clinician."
    },
    bn: {
      navYourSpace:"আপনার জায়গা", navHome:"হোম", navHealth:"আমার স্বাস্থ্য", navLearn:"স্বাস্থ্য পাঠাগার", navHelp:"বন্ধুহেল্প", navAi:"নারীবন্ধু AI",
      privateDemo:"ব্যক্তিগত ডেমো", localOnly:"এই ডিভাইসে সংরক্ষিত", navProfile:"প্রোফাইল ও গোপনীয়তা", topEyebrow:"আপনার ব্যক্তিগত জায়গা", localBadge:"লোকাল প্রিভিউ", discreet:"গোপনীয় মোড",
      samplePreview:"নমুনা প্রিভিউ", sampleNotice:" — এগুলো কেবল উদাহরণ, আপনার ব্যক্তিগত স্বাস্থ্য-তথ্য নয়।", useMyData:"নিজের তথ্য ব্যবহার করুন",
      heroEyebrow:"আপনার জন্য একটু নিজের সময়", heroTitle:"নিজের শরীরকে বুঝুন।<br>নিজের গতিতে যত্ন নিন।", heroText:"মাসিকের চক্র বুঝতে, লক্ষণ লিখে রাখতে এবং নির্ভরযোগ্য তথ্য জানতে একটি উষ্ণ ও ব্যক্তিগত জায়গা—কোনো বিচার ছাড়াই।",
      logPeriod:"মাসিক লিখুন", exploreLibrary:"স্বাস্থ্য পাঠাগার দেখুন", heroTrust:"ডেমোর তথ্য এই ব্রাউজারেই থাকে", gentleReminder:"আপনি যত্ন পাওয়ার যোগ্য",
      yourOverview:"আপনার সারসংক্ষেপ", cycleOverview:"এক নজরে আপনার চক্র", viewTracker:"ট্র্যাকার খুলুন", cycleEstimate:"চক্রের হিসাব", cycleDay:"চক্রের দিন",
      nextEstimate:"পরবর্তী মাসিকের আনুমানিক সময়", estimatedStart:"সম্ভাব্য শুরুর তারিখ", estimateDisclaimer:"এটি কেবল অনুমান—মাসিকের চক্র স্বাভাবিকভাবেই বদলাতে পারে।", averageCycle:"গড় চক্র", periodsLogged:"লিখে রাখা মাসিক",
      quickActions:"দ্রুত কাজ", logPeriodHint:"মাসিক বা স্পটিং লিখুন", logSymptom:"লক্ষণ লিখুন", logSymptomHint:"আজ কেমন লাগছে লিখুন", logWellness:"দৈনিক সুস্থতা", logWellnessHint:"মন, ঘুম ও শক্তির মাত্রা",
      madeForRealLife:"দৈনন্দিন জীবনের জন্য", yourHealthSpace:"আপনার স্বাস্থ্য, আপনার মতো করে", featureCycleTitle:"চক্র ও ডায়েরি", featureCycleText:"মাসিকের তারিখ ও ব্যক্তিগত নোট এক জায়গায় রাখুন।",
      featureLearnTitle:"জেনে বুঝে এগিয়ে যান", featureLearnText:"পিসিওএস, পিএমএস ও ব্যক্তিগত স্বাস্থ্য নিয়ে সহজ গাইড পড়ুন।", featureCareTitle:"চিকিৎসার প্রস্তুতি", featureCareText:"চিকিৎসকের সঙ্গে কথা বলার আগে আপনার উদ্বেগগুলো গুছিয়ে নিন।",
      safetyTitle:"কখন জরুরি চিকিৎসা প্রয়োজন", safetyText:"মাথা ঘোরা বা অজ্ঞান হওয়ার সঙ্গে অতিরিক্ত রক্তপাত, তীব্র তলপেটের ব্যথা, অথবা গর্ভাবস্থায় রক্তপাত/ব্যথা হলে দ্রুত চিকিৎসা নিন। অ্যাপের উত্তরের জন্য অপেক্ষা করবেন না।",
      call999:"৯৯৯-এ কল করুন", bottomPrivacy:"আপনার স্বাস্থ্য-তথ্য ব্যক্তিগত। এই ব্রাউজার ডেমো আপনার তথ্য কোনো সার্ভারে পাঠায় না।",
      trackerEyebrow:"আপনার ব্যক্তিগত ডায়েরি", trackerTitle:"আমার স্বাস্থ্য", trackerIntro:"একটি শান্ত ও ব্যক্তিগত জায়গায় মাসিকের চক্র এবং দৈনন্দিন পরিবর্তন লিখে রাখুন।",
      cycleCalendar:"চক্রের ক্যালেন্ডার", periodDays:"মাসিক", spotting:"স্পটিং", estimatedDays:"আনুমানিক", cycleInsights:"চক্রের সারসংক্ষেপ", lastPeriod:"শেষ মাসিক শুরু",
      trackerEstimateNote:"অন্তত দুটি মাসিক শুরুর তারিখ না থাকলে হিসাব করা যায় না। এটি চিকিৎসাগত পূর্বাভাস নয়।", recentEntries:"সাম্প্রতিক তথ্য", periodHistory:"মাসিকের ইতিহাস",
      noticePatterns:"নিজের পরিবর্তন লক্ষ্য করুন", dailyDiary:"দৈনিক ডায়েরি", wellbeing:"সুস্থতা", symptom:"লক্ষণ", bodyNotes:"শরীরের নোট", symptomDiary:"লক্ষণের ডায়েরি",
      dailyCheckin:"দৈনিক খোঁজ", wellbeingLog:"সুস্থতার নোট", trackerPrivacy:"তথ্য শুধু এই ব্রাউজারেই থাকে। এই ডেমো আপনার Android অ্যাপ বা Firebase অ্যাকাউন্টের সঙ্গে যুক্ত নয়।",
      libraryEyebrow:"বিচার নয়, সঠিক তথ্য", libraryTitle:"স্বাস্থ্য পাঠাগার", libraryIntro:"নিজের শরীর বুঝতে এবং চিকিৎসকের সঙ্গে কথা বলার প্রস্তুতি নিতে সহজ ও পরিষ্কার তথ্য।",
      learnAtYourPace:"নিজের গতিতে শিখুন", libraryFeatureTitle:"ছোট ছোট পদক্ষেপ। ভালো প্রশ্ন। আরও আত্মবিশ্বাস।", libraryFeatureText:"এই গাইডগুলো প্রাথমিক ধারণার জন্য—ব্যক্তিগত চিকিৎসা পরামর্শের বিকল্প নয়।",
      filterAll:"সব বিষয়", filterCycle:"মাসিকের চক্র", filterIntimate:"ব্যক্তিগত স্বাস্থ্য", clinicalNoteTitle:"স্বাস্থ্য তথ্য সম্পর্কে মনে রাখুন",
      clinicalNoteText:"এই গাইডগুলো শিক্ষামূলক। এগুলো রোগ নির্ণয়, ব্যক্তিগত পরীক্ষার রিপোর্ট ব্যাখ্যা বা ওষুধের পরামর্শ দিতে পারে না। নিজের স্বাস্থ্য নিয়ে যোগ্য চিকিৎসকের সঙ্গে কথা বলুন।",
      careEyebrow:"পরবর্তী পদক্ষেপে সহায়তা", careTitle:"বন্ধুহেল্প", careIntro:"যোগ্য স্বাস্থ্যসেবা প্রদানকারীর সঙ্গে কথা বলার জন্য প্রস্তুতি নিন।",
      careStartsHere:"যত্ন শুরু হয় কথা বলার মাধ্যমে", careHeroTitle:"আপনার কথা শোনার অধিকার আপনার আছে।", careHeroText:"লক্ষণ ও প্রশ্নের একটি পরিষ্কার তালিকা চিকিৎসকের সঙ্গে কথা বলা সহজ করতে পারে।",
      prepareVisit:"চিকিৎসকের কাছে যাওয়ার প্রস্তুতি", beforeVisitTitle:"চিকিৎসকের কাছে যাওয়ার আগে", beforeVisitText:"লক্ষণ কখন শুরু হয়েছে, কতবার হয় এবং কীসে বাড়ে বা কমে—লিখে রাখুন।",
      openChecklist:"চেকলিস্ট খুলুন", verifyCareTitle:"যাচাই করে চিকিৎসা নিন", verifyCareText:"চিকিৎসকের বর্তমান BMDC নিবন্ধন যাচাই করুন এবং ক্লিনিক, ফি ও অ্যাপয়েন্টমেন্টের তথ্য সরাসরি নিশ্চিত করুন।",
      visitBmdc:"BMDC ওয়েবসাইট দেখুন", shareChoiceTitle:"আপনার তথ্য, আপনার সিদ্ধান্ত", shareChoiceText:"যে স্বাস্থ্য-নোট বা রিপোর্ট নিয়ে কথা বলতে স্বাচ্ছন্দ্যবোধ করেন, কেবল সেগুলোই শেয়ার করুন।",
      privacySettings:"গোপনীয়তা সেটিংস", directoryNotLiveTitle:"এই ওয়েব প্রিভিউতে সরাসরি ডাক্তার বুকিং চালু নেই।",
      directoryNotLiveText:"Android রিপোজিটরিতে চিকিৎসক তালিকার তথ্য আছে, তবে এই ব্রাউজার ডেমো চিকিৎসকের পরিচয়, সময়সূচি বা ফি যাচাই করে না। তাই সেগুলোকে বাস্তব সুপারিশ হিসেবে দেখানো বা বুকিং নেওয়া হচ্ছে না।",
      urgentCareTitle:"জরুরি সহায়তা প্রয়োজন?", urgentCareText:"গুরুতর লক্ষণ বা তাৎক্ষণিক বিপদে নিকটস্থ হাসপাতালের জরুরি বিভাগে যান। বাংলাদেশে জরুরি সহায়তার জন্য ৯৯৯-এ কল করুন।",
      assistantEyebrow:"নিরাপত্তাকে অগ্রাধিকার দিয়ে তথ্য", assistantTitle:"নারীবন্ধু AI", assistantIntro:"সহজ বাংলা বা ইংরেজিতে নারীস্বাস্থ্য বিষয়ে তথ্য বোঝানোর জন্য ভবিষ্যতের একটি শিক্ষামূলক সহকারী।",
      notConnected:"এই ডেমোতে সংযুক্ত নয়", aiDemoTitle:"AI সহকারীর জন্য নিরাপদ সংযোগ প্রয়োজন।",
      aiDemoText:"এই ওয়েবসাইট Google AI Studio বা Gemini API টোকেন ছাড়া চলছে। কোনো প্রশ্ন AI সেবায় পাঠানো হয় না এবং AI উত্তর দেওয়ার ভানও করা হয় না।",
      readLibrary:"স্বাস্থ্য পাঠাগার পড়ুন", aiPrincipleOne:"শিক্ষা, রোগ নির্ণয় নয়", aiPrincipleOneText:"সাধারণ স্বাস্থ্য বিষয় ব্যাখ্যা করবে; রোগ নির্ণয় বা ওষুধের পরামর্শ দেবে না।",
      aiPrincipleTwo:"তথ্য শেয়ারের আগে সম্মতি", aiPrincipleTwoText:"ভবিষ্যতের সংযোগে কোন তথ্য পাঠানো হবে তা স্পষ্টভাবে জানিয়ে আগে অনুমতি নিতে হবে।",
      aiPrincipleThree:"জরুরি লক্ষণ আগে", aiPrincipleThreeText:"সম্ভাব্য জরুরি লক্ষণে দীর্ঘ কথোপকথন নয়, দ্রুত চিকিৎসা নেওয়ার নির্দেশনা দিতে হবে।",
      privacyEyebrow:"আপনার সিদ্ধান্ত গুরুত্বপূর্ণ", profileTitle:"প্রোফাইল ও গোপনীয়তা", profileIntro:"এই ব্রাউজার প্রিভিউ পরিচালনা করুন এবং আপনার তথ্য কোথায় থাকে তা জানুন।",
      previewProfile:"প্রিভিউ প্রোফাইল", guestProfile:"আপনার ব্যক্তিগত জায়গা", guestProfileText:"কোনো অ্যাকাউন্টে সাইন ইন করা নেই। এটি স্থানীয় ব্রাউজার প্রিভিউ।",
      languageSetting:"ভাষা", languageSettingText:"ইন্টারফেসের ভাষা বেছে নিন।", discreetSetting:"গোপনীয় মোড", discreetSettingText:"এই পর্দায় চক্র ও লক্ষণের তথ্য ঝাপসা করে রাখুন।",
      privacyAtAGlance:"এক নজরে গোপনীয়তা", localStorageTitle:"এই ব্রাউজারে সংরক্ষিত",
      privacyListOne:"মাসিক, লক্ষণ ও সুস্থতার নোট এই ব্রাউজারের লোকাল স্টোরেজে থাকে।",
      privacyListTwo:"এই ডেমো কোনো তথ্য Firebase, Gemini বা অন্য সার্ভারে পাঠায় না।",
      privacyListThree:"এই ডেমোতে সাইন-ইন, অ্যানালিটিক্স ট্র্যাকার বা বিজ্ঞাপনের স্ক্রিপ্ট নেই।",
      privacyCaution:"এটি এনক্রিপ্ট করা স্টোরেজ বা নিরাপদ মেডিকেল রেকর্ড নয়। অন্যের সঙ্গে শেয়ার করা ডিভাইসে সংবেদনশীল তথ্য লিখবেন না। ব্রাউজারের সাইট ডেটা মুছে ফেললে তথ্য হারিয়ে যেতে পারে।",
      yourData:"আপনার তথ্য", dataControlsTitle:"তথ্য এক্সপোর্ট বা মুছে ফেলুন", dataControlsText:"ব্রাউজারের তথ্যের একটি কপি রাখুন অথবা এই ডিভাইস থেকে মুছে ফেলুন।",
      exportData:"আমার তথ্য এক্সপোর্ট", clearData:"সব স্থানীয় তথ্য মুছুন", aboutTagline:"নিজের শরীরকে জানুন। প্রয়োজন বুঝুন। সঠিক যত্নে পৌঁছান।",
      footerDisclaimer:"শিক্ষামূলক ডেমো · চিকিৎসাসেবা নয়", footerPrivacy:"গোপনীয়তা ও তথ্য",
      addPeriodTitle:"মাসিক বা স্পটিং লিখুন", addSymptomTitle:"আপনার অনুভূতি লিখুন", addWellnessTitle:"দৈনিক সুস্থতার খোঁজ",
      startWithOwnTitle:"নিজের তথ্য দিয়ে শুরু করবেন?", startWithOwnText:"এই প্রিভিউতে এখন নমুনা তথ্য আছে। নিজের তথ্য যোগ করার আগে উদাহরণগুলো মুছে ফেলুন।",
      clearSamples:"নমুনা তথ্য মুছুন", keepPreview:"নমুনা প্রিভিউ রাখুন", saveEntry:"সংরক্ষণ করুন", cancel:"বাতিল",
      kindLabel:"তথ্যের ধরন", periodOption:"মাসিক", spottingOption:"স্পটিং", startDateLabel:"শুরুর তারিখ", endDateLabel:"শেষের তারিখ",
      endDateHint:"মাসিকের ক্ষেত্রে রক্তপাতের শেষ দিন দিন। স্পটিংয়ের ক্ষেত্রে শেষের তারিখ ঐচ্ছিক।", noteLabel:"নোট (ঐচ্ছিক)", notePlaceholder:"যা মনে রাখতে চান লিখুন…",
      symptomLabel:"লক্ষণ", severityLabel:"কতটা তীব্র ছিল?", mild:"হালকা", moderate:"মাঝারি", severe:"তীব্র", dateLabel:"তারিখ",
      moodLabel:"মনের অবস্থা", sleepLabel:"ঘুম (ঘণ্টা)", energyLabel:"শক্তির মাত্রা", low:"কম", steady:"মোটামুটি", good:"ভালো", moodGreat:"ভালো", moodOkay:"মোটামুটি", moodLow:"মন খারাপ", moodAnxious:"উদ্বিগ্ন",
      today:"আজ", tomorrow:"আগামীকাল", inDays:"আর {n} দিন", daysAgo:"{n} দিন আগে", noEstimate:"এখনো পর্যাপ্ত তথ্য নেই",
      noPeriods:"এখনো মাসিকের তথ্য নেই। চক্রের ডায়েরি শুরু করতে প্রথম তারিখটি যোগ করুন।", noSymptoms:"এখনো কোনো লক্ষণ লেখা হয়নি। আপনার নোট এখানে দেখা যাবে।", noWellness:"এখনো দৈনিক সুস্থতার নোট নেই। ছোট একটি নোটও নিজের পরিবর্তন বুঝতে সাহায্য করতে পারে।",
      periodEntry:"মাসিক", spottingEntry:"স্পটিং", readGuide:"গাইড পড়ুন", minutes:"মিনিট পড়তে", articleDisclaimer:"শুধু শিক্ষামূলক তথ্য",
      calendarEmpty:"এই দিনে কোনো তথ্য লেখা হয়নি।", calendarPeriod:"মাসিকের তথ্য আছে", calendarSpotting:"স্পটিংয়ের তথ্য আছে", calendarEstimate:"মাসিকের সম্ভাব্য সময়",
      periodSaved:"আপনার তথ্য এই ব্রাউজারে সংরক্ষিত হয়েছে।", symptomSaved:"আপনার লক্ষণের নোট সংরক্ষিত হয়েছে।", wellnessSaved:"আপনার সুস্থতার নোট সংরক্ষিত হয়েছে।",
      demoCleared:"নমুনা তথ্য মুছে ফেলা হয়েছে। এখন নিজের তথ্য যোগ করতে পারবেন।", dataCleared:"সব স্থানীয় তথ্য মুছে ফেলা হয়েছে।", exportReady:"আপনার তথ্যের কপি তৈরি হয়েছে.",
      exportWarning:"এক্সপোর্ট করা ফাইলে ব্যক্তিগত স্বাস্থ্য-তথ্য থাকতে পারে। নিরাপদ জায়গায় রাখুন।",
      clearConfirmTitle:"সব স্থানীয় তথ্য মুছবেন?", clearConfirmText:"এই ব্রাউজারে ডেমোর সংরক্ষিত মাসিক, লক্ষণ ও সুস্থতার তথ্য মুছে যাবে। সার্ভারে কোনো কপি নেই।",
      confirmClear:"সব তথ্য মুছুন", dataReset:"সব স্থানীয় তথ্য মুছে ফেলা হয়েছে।",
      checklistTitle:"চিকিৎসকের কাছে যাওয়ার প্রস্তুতি", checklistIntro:"কী হচ্ছে তার ছোট একটি তালিকা চিকিৎসককে বুঝতে সাহায্য করতে পারে। যতটুকু শেয়ার করতে স্বাচ্ছন্দ্যবোধ করেন, ততটুকুই নিন।",
      checklistItems:["লক্ষণ বা পরিবর্তন কখন শুরু হয়েছে?","কতবার হয় এবং কতক্ষণ থাকে?","কীসে বাড়ে বা কমে?","এখন কী ওষুধ বা সাপ্লিমেন্ট নিচ্ছেন?","থাকলে প্রাসঙ্গিক রিপোর্ট বা পরীক্ষার ফল সঙ্গে নিন।"],
      copyChecklist:"চেকলিস্ট কপি করুন", checklistCopied:"চেকলিস্ট কপি হয়েছে।", checklistCopyFail:"এখানে কপি করা যাচ্ছে না; তালিকাটি নির্বাচন করে কপি করতে পারেন।",
      aiSafetyTitle:"নারীবন্ধু AI-এর নিরাপত্তা নীতি", aiSafetyText:"Android সংস্করণে AI-এর কাজ শিক্ষামূলক তথ্য দেওয়া। এটি রোগ নির্ণয়, ওষুধ দেওয়া বা ব্যক্তিগত রিপোর্ট ব্যাখ্যা করবে না। দীর্ঘস্থায়ী, বাড়তে থাকা বা অনিশ্চিত সমস্যায় যোগ্য চিকিৎসকের পরামর্শ এবং বিপজ্জনক লক্ষণে জরুরি সেবা নিতে বলবে।",
      urgentReminder:"অতিরিক্ত রক্তপাতের সঙ্গে মাথা ঘোরা/অজ্ঞান হওয়া, তীব্র তলপেটের ব্যথা বা গর্ভাবস্থায় রক্তপাত/ব্যথা হলে এখনই জরুরি চিকিৎসা নিন।",
      cycleInProgress:"আপনার চক্র চলমান", cycleNeedData:"চক্রের ডায়েরি শুরু করুন", cycleNeedDataText:"ব্যক্তিগত সারসংক্ষেপ দেখতে মাসিকের তারিখ যোগ করুন। পর্যাপ্ত ইতিহাস না থাকলে অনুমান দেখানো হবে না।",
      cycleBasedOn:"{n}টি চক্রের ব্যবধানের ভিত্তিতে", noLastPeriod:"মাসিক শুরুর তারিখ নেই", nextIn:"আর প্রায় {n} দিন", nextToday:"আজকের আশেপাশে সম্ভাব্য", nextPast:"সম্ভাব্য তারিখটি পেরিয়ে গেছে",
      noHistory:"এখনো কোনো তথ্য নেই", dayEntry:"{date}: {detail}", sampleTag:"নমুনা", savedTag:"সংরক্ষিত", severitySuffix:"মাত্রা",
      articlePcosTitle:"পিসিওএস ও ইনসুলিন রেজিস্ট্যান্স বোঝা", articlePmsTitle:"পিএমএস ও বারবার হওয়া চক্রের লক্ষণ",
      articleCycleTitle:"মাসিকের চক্র কেন বদলায় এবং স্পটিং কী", articleIntimateTitle:"যোনি ও বাহ্যিক পরিচ্ছন্নতা: স্রাব ও যত্ন",
      articlePcosSummary:"পিসিওএস ডিম্বস্ফোটন, মাসিকের নিয়মিততা ও বিপাকক্রিয়ায় প্রভাব ফেলতে পারে। সাধারণ লক্ষণ এবং চিকিৎসকের সঙ্গে কী আলোচনা করবেন জানুন।",
      articlePmsSummary:"মাসিকের আগে শারীরিক ও মানসিক পরিবর্তন লক্ষ্য করুন এবং কখন চিকিৎসকের সহায়তা প্রয়োজন তা জানুন।",
      articleCycleSummary:"সবার চক্র ২৮ দিনের নয়। স্বাভাবিক পার্থক্য এবং স্পটিং আলাদাভাবে লেখার কারণ জানুন।",
      articleIntimateSummary:"স্বাভাবিক স্রাব, বাইরের অংশের কোমল পরিচ্ছন্নতা এবং কোন পরিবর্তন চিকিৎসককে জানাবেন তা বুঝুন।"
    }
  };

  const ARTICLES = [
    {
      id:"pcos", category:"PCOS", titleKey:"articlePcosTitle", summaryKey:"articlePcosSummary", read:5,
      refs:["International Evidence-Based Guideline for PCOS (2023)","WHO Women's Reproductive Health Guidance","RCOG Green-top Guideline on PCOS"],
      sections:[
        {h:"What is PCOS?", b:"PCOS involves an imbalance of reproductive hormones and often how the body responds to insulin (insulin resistance). Common signs include irregular or infrequent periods, facial or body hair changes, acne, scalp hair thinning, and weight changes.", hb:"পিসিওএস কী?", bb:"পিসিওএস-এ প্রজনন হরমোনের ভারসাম্যে পরিবর্তন হয় এবং অনেক সময় শরীর ইনসুলিন হরমোনের প্রতি কম সংবেদনশীল হয়ে পড়ে। সাধারণ লক্ষণগুলোর মধ্যে রয়েছে অনিয়মিত মাসিক, মুখে বা শরীরে অতিরিক্ত লোম, ব্রণ, চুল পাতলা হওয়া বা ওজনের পরিবর্তন।"},
        {h:"Common medical investigations", b:"Doctors typically review your menstrual history, blood hormone tests (such as LH, FSH, testosterone, TSH, prolactin), blood sugar/insulin tests, and sometimes a pelvic ultrasound. Having ovaries with small follicles on ultrasound alone does not confirm PCOS without clinical assessment.", hb:"সাধারণ ডাক্তারি পরীক্ষা", bb:"চিকিৎসক সাধারণত আপনার মাসিকের ইতিহাস শোনেন এবং প্রয়োজনে হরমোন পরীক্ষা (LH, FSH, টেস্টোস্টেরন, TSH, প্রোল্যাক্টিন), রক্তের শর্করা পরীক্ষা এবং পেলভিক আল্ট্রাসাউন্ড করতে বলতে পারেন। শুধু আল্ট্রাসাউন্ড রিপোর্ট দেখে নিজে থেকে রোগ নির্ণয় করা যায় না।"},
        {h:"Supportive lifestyle habits", b:"Balanced meals with fibre, whole grains, dal, vegetables, and lean protein, along with regular walking or light exercise and steady sleep, support metabolic health.", hb:"দৈনন্দিন সহায়ক অভ্যাস", bb:"আঁশযুক্ত খাবার, শাকসবজি, ডাল, পরিমিত সুষম খাবার, নিয়মিত হাঁটা বা হালকা ব্যায়াম এবং পর্যাপ্ত ঘুম শরীরের ভারসাম্য রক্ষায় সাহায্য করে."}
      ],
      note:"Educational overview only. NariBondhu cannot diagnose PCOS or recommend prescription medicines."
    },
    {
      id:"pms", category:"PMS", titleKey:"articlePmsTitle", summaryKey:"articlePmsSummary", read:4,
      refs:["RCOG Management of Premenstrual Syndrome Guideline","NICE Menstrual Health Guidance"],
      sections:[
        {h:"Common PMS experiences", b:"Many women experience bloating, breast tenderness, mild cramps, fatigue, sleep shifts, irritability, or low mood 5 to 10 days before their period, easing shortly after bleeding begins.", hb:"সাধারণ পিএমএস লক্ষণ", bb:"অনেক নারীর মাসিক শুরুর ৫ থেকে ১০ দিন আগে পেট ফাঁপা, স্তনে ব্যথা বা ভারী ভাব, হালকা তলপেটে ব্যথা, ক্লান্তি, ঘুমের পরিবর্তন বা মন খারাপ দেখা দেয় এবং মাসিক শুরু হলে তা কমে যায়।"},
        {h:"When to seek clinical care", b:"If mood changes feel overwhelming, hopeless, or severely disrupt work, study, or relationships every cycle, or if pelvic pain is severe, a gynaecologist or clinical specialist can help.", hb:"কখন ডাক্তারের পরামর্শ নেবেন", bb:"যদি প্রতি মাসে মানসিক চাপ বা বিষণ্ণতা খুব তীব্র হয় এবং দৈনন্দিন কাজকর্মে বাধা সৃষ্টি করে, অথবা তলপেটের ব্যথা অসহনীয় হয়, তবে অবশ্যই বিশেষজ্ঞ চিকিৎসকের পরামর্শ নিন।"},
        {h:"Self-care basics", b:"Warm compresses for cramps, gentle movement, reducing excess salt/caffeine during symptomatic days, and consistent sleep help many people feel steadier.", hb:"সহজ যত্ন", bb:"তলপেটে হালকা গরম সেঁক, পর্যাপ্ত পানি পান, অতিরিক্ত লবণ ও চা-কফি কমানো এবং নিয়মিত ঘুম স্বস্তি দিতে পারে।"}
      ],
      note:"If symptoms are severe or disrupt daily life, seek professional assessment."
    },
    {
      id:"cycle", category:"Menstrual", titleKey:"articleCycleTitle", summaryKey:"articleCycleSummary", read:4,
      refs:["FIGO Abnormal Uterine Bleeding Systems","WHO Sexual and Reproductive Health"],
      sections:[
        {h:"Normal cycle variation", b:"Adult menstrual cycles commonly range from 21 to 35 days, and individual cycles naturally vary by a few days from month to month. Counting starts on the first day of full menstrual bleeding (Day 1) until the day before the next period starts.", hb:"স্বাভাবিক চক্রের পার্থক্য", bb:"প্রাপ্তবয়স্ক নারীদের মাসিক চক্র সাধারণত ২১ থেকে ৩৫ দিনের মধ্যে হতে পারে এবং প্রতি মাসে কয়েক দিন আগে-পরে হওয়া স্বাভাবিক। পূর্ণ রক্তপাত শুরুর প্রথম দিন থেকে পরবর্তী মাসিকের আগের দিন পর্যন্ত চক্রের দৈর্ঘ্য গণনা করা হয়।"},
        {h:"Spotting vs. menstruation", b:"Spotting is very light, brief bleeding or pinkish-brown marks between periods that does not require full pad changes. Do not count spotting as Day 1 of your period—log it separately so your cycle statistics stay accurate.", hb:"স্পটিং বনাম পূর্ণ মাসিক", bb:"দুই মাসিকের মাঝে খুব হালকা দাগ বা অল্প রক্তপাত হলে তাকে স্পটিং বলে। এটিকে নতুন মাসিকের প্রথম দিন হিসেবে গণনা করবেন না—নিরাপদ হিসাবের জন্য আলাদাভাবে স্পটিং হিসেবে লিখে রাখুন।"},
        {h:"Warning signs", b:"Soaking through pads every hour for consecutive hours, dizziness/fainting, bleeding after menopause, or repeated bleeding between periods require prompt medical evaluation.", hb:"সতর্কতামূলক লক্ষণ", bb:"প্রতি ঘণ্টায় প্যাড ভিজে যাওয়া, মাথা ঘোরা বা অজ্ঞান হওয়া, অথবা বারবার অনিয়মিত রক্তপাত হলে দ্রুত চিকিৎসকের শরণাপন্ন হন।"}
      ],
      note:"Calendar calculations do not establish ovulation or fertility status."
    },
    {
      id:"intimate", category:"Intimate", titleKey:"articleIntimateTitle", summaryKey:"articleIntimateSummary", read:4,
      refs:["WHO Sexual and Reproductive Health Guidance"],
      sections:[
        {h:"Understanding normal discharge", b:"Healthy vaginal discharge keeps tissues clean and moist. It often looks clear, white, or slightly creamy and varies across the menstrual cycle.", hb:"স্বাভাবিক স্রাব বোঝা", bb:"স্বাভাবিক সাদা স্রাব শরীরকে সুরক্ষিত ও আর্দ্র রাখে। মাসিকের চক্রের বিভিন্ন সময়ে এর পরিমাণ সামান্য কম-বেশি হতে পারে।"},
        {h:"Safe intimate hygiene", b:"Wash the external vulvar area gently with clean lukewarm water daily. Avoid harsh perfumed soaps, vaginal douching, or scented sprays inside the vagina, which disrupt protective natural bacteria. Wear breathable cotton underwear and change damp pads frequently during menstruation.", hb:"নিরাপদ পরিচ্ছন্নতা", bb:"বাইরের অংশ প্রতিদিন পরিষ্কার কুসুম গরম বা সাধারণ পানি দিয়ে ধুয়ে নিন। সুগন্ধি সাবান, ডুশ বা ভেতরের অংশে রাসায়নিক ব্যবহার করবেন না। সুতি অন্তর্বাস পরুন এবং মাসিকের সময় নিয়মিত প্যাড পরিবর্তন করুন।"},
        {h:"When to see a doctor", b:"Thick curdy discharge, grey/yellow/green discharge, strong fishy odour, itching, burning, or pelvic discomfort may indicate an infection that needs proper medical diagnosis and treatment—never self-medicate.", hb:"কখন ডাক্তার দেখাবেন", bb:"ঘন দইয়ের মতো বা হলুদ/সবুজ স্রাব, তীব্র দুর্গন্ধ, চুলকানি, জ্বালাপোড়া বা তলপেটে ব্যথা হলে নিজে ওষুধ না খেয়ে স্ত্রীরোগ বিশেষজ্ঞের পরামর্শ নিন।"}
      ],
      note:"This guide is educational and does not diagnose infection or recommend treatment."
    }
  ];

  const today = localDateISO(new Date());
  let calendarMonth = new Date();
  let selectedCalendarDate = today;
  let articleFilter = "all";
  let toastTimer = null;
  let state = loadState();

  function localDateISO(date) {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const d = String(date.getDate()).padStart(2, "0");
    return y + "-" + m + "-" + d;
  }
  function shiftDate(iso, days) {
    const [y,m,d] = iso.split("-").map(Number);
    return new Date(Date.UTC(y, m - 1, d + days)).toISOString().slice(0,10);
  }
  function dateParts(iso) { return iso.split("-").map(Number); }
  function localDate(iso) {
    const [y,m,d] = dateParts(iso);
    return new Date(y, m - 1, d, 12, 0, 0);
  }
  function dayDiff(from, to) {
    const [fy,fm,fd] = dateParts(from);
    const [ty,tm,td] = dateParts(to);
    return Math.round((Date.UTC(ty,tm-1,td) - Date.UTC(fy,fm-1,fd)) / 86400000);
  }
  function formatDate(iso, long = false) {
    if (!iso) return "—";
    const locale = state.language === "bn" ? "bn-BD" : "en-GB";
    return new Intl.DateTimeFormat(locale, long ? {day:"numeric", month:"long", year:"numeric"} : {day:"numeric", month:"short", year:"numeric"}).format(localDate(iso));
  }
  function formatMonth(date) {
    return new Intl.DateTimeFormat(state.language === "bn" ? "bn-BD" : "en-GB", {month:"long", year:"numeric"}).format(date);
  }
  function escapeHTML(value) {
    return String(value ?? "").replace(/[&<>"']/g, char => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[char]));
  }
  function uid() { return "entry-" + Date.now().toString(36) + "-" + Math.random().toString(36).slice(2,8); }
  function sampleState() {
    const previous = shiftDate(today, -46);
    const last = shiftDate(today, -18);
    return {
      language:"en",
      discreetMode:false,
      demoMode:true,
      periods:[
        {id:"sample-period-a", kind:"period", start:previous, end:shiftDate(previous,4), note:""},
        {id:"sample-period-b", kind:"period", start:last, end:shiftDate(last,4), note:""}
      ],
      symptoms:[
        {id:"sample-symptom-a", date:shiftDate(today,-2), name:"Cramps", severity:"Mild", note:""},
        {id:"sample-symptom-b", date:shiftDate(today,-1), name:"Low energy", severity:"Moderate", note:""}
      ],
      wellness:[
        {id:"sample-wellness-a", date:shiftDate(today,-1), mood:"Okay", sleep:"7", energy:"Steady", note:""}
      ]
    };
  }
  function loadState() {
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (!saved) return sampleState();
      const parsed = JSON.parse(saved);
      return {
        ...sampleState(),
        ...parsed,
        periods:Array.isArray(parsed.periods) ? parsed.periods : [],
        symptoms:Array.isArray(parsed.symptoms) ? parsed.symptoms : [],
        wellness:Array.isArray(parsed.wellness) ? parsed.wellness : []
      };
    } catch (error) {
      return sampleState();
    }
  }
  function saveState() {
    try { localStorage.setItem(STORAGE_KEY, JSON.stringify(state)); }
    catch (error) { showToast("Browser storage is unavailable. Your changes may not persist."); }
  }
  function copy() { return COPY[state.language] || COPY.en; }
  function tr(key) { return copy()[key] || COPY.en[key] || key; }
  function interpolate(text, values) {
    return String(text).replace(/\{(\w+)\}/g, (_, key) => values[key] ?? "");
  }
  function setText(id, value) {
    const node = document.getElementById(id);
    if (node) node.textContent = value;
  }
  function cycleMetrics() {
    const starts = state.periods.filter(item => item.kind === "period" && item.start).map(item => item.start).sort();
    const intervals = [];
    for (let i = 1; i < starts.length; i++) {
      const length = dayDiff(starts[i - 1], starts[i]);
      if (length > 0 && length < 100) intervals.push(length);
    }
    const average = intervals.length ? Math.round(intervals.reduce((sum, value) => sum + value, 0) / intervals.length) : null;
    const last = starts.length ? starts[starts.length - 1] : null;
    const next = last && average ? shiftDate(last, average) : null;
    const day = last ? Math.max(1, dayDiff(last, today) + 1) : null;
    return {starts, intervals, average, last, next, day};
  }
  function relativeDate(iso) {
    if (!iso) return tr("noEstimate");
    const diff = dayDiff(today, iso);
    if (diff === 0) return tr("nextToday");
    if (diff === 1) return tr("tomorrow");
    if (diff > 1) return interpolate(tr("inDays"), {n:diff});
    if (diff < 0) return interpolate(tr("daysAgo"), {n:Math.abs(diff)});
    return tr("today");
  }
  function setRoute(route) {
    const allowed = ["home","tracker","learn","bondhuhelp","assistant","profile"];
    const target = allowed.includes(route) ? route : "home";
    if (location.hash !== "#" + target) location.hash = target;
    renderApp(target);
  }
  function currentRoute() {
    const route = location.hash.replace("#","").trim();
    return ["home","tracker","learn","bondhuhelp","assistant","profile"].includes(route) ? route : "home";
  }
  function applyLanguage() {
    document.documentElement.lang = state.language === "bn" ? "bn" : "en";
    $$("[data-i18n]").forEach(node => {
      const key = node.getAttribute("data-i18n");
      if (COPY[state.language][key]) node.innerHTML = COPY[state.language][key];
    });
    const languageLabel = state.language === "en" ? "বাংলা" : "English";
    setText("language-toggle", languageLabel);
    setText("profile-language-toggle", languageLabel);
    const discreet = $("#discreet-toggle");
    const profileDiscreet = $("#profile-discreet-toggle");
    if (discreet) discreet.setAttribute("aria-pressed", String(!!state.discreetMode));
    if (profileDiscreet) profileDiscreet.setAttribute("aria-checked", String(!!state.discreetMode));
    document.body.classList.toggle("discreet-mode", !!state.discreetMode);
    $("#demo-banner").hidden = !state.demoMode;
  }
  function renderApp(route = currentRoute()) {
    applyLanguage();
    $$("[data-page]").forEach(page => page.classList.toggle("is-active", page.dataset.page === route));
    $$("[data-route]").forEach(link => {
      const active = link.dataset.route === route;
      link.classList.toggle("is-active", active);
      if (active) link.setAttribute("aria-current","page");
      else link.removeAttribute("aria-current");
    });
    const titleKeys = {home:"navHome",tracker:"navHealth",learn:"navLearn",bondhuhelp:"navHelp",assistant:"navAi",profile:"navProfile"};
    setText("current-page-title", tr(titleKeys[route] || "navHome"));
    setText("footer-year", String(new Date().getFullYear()));
    renderHome();
    renderCalendar();
    renderHistory();
    renderDiary();
    renderArticles();
  }
  function renderHome() {
    const metrics = cycleMetrics();
    setText("periods-count", String(metrics.starts.length));
    if (!metrics.last) {
      setText("cycle-day","—");
      setText("cycle-status",tr("cycleNeedData"));
      setText("cycle-description",tr("cycleNeedDataText"));
      setText("cycle-confidence",tr("noEstimate"));
      setText("average-cycle","—");
      setText("next-period-date","—");
      setText("next-period-countdown",tr("noEstimate"));
      $("#cycle-ring").style.background = "conic-gradient(#eee8f7 0 100%)";
      return;
    }
    const day = metrics.day;
    const progress = metrics.average ? Math.min(100, Math.max(6, day / metrics.average * 100)) : 20;
    $("#cycle-ring").style.background = "conic-gradient(#b78db2 0 " + progress + "%, #eee8f7 " + progress + "% 100%)";
    setText("cycle-day",String(day));
    setText("cycle-status",tr("cycleInProgress"));
    setText("cycle-description",metrics.average ? tr("cycleBasedOn").replace("{n}",String(metrics.intervals.length)) : tr("cycleNeedDataText"));
    setText("cycle-confidence",metrics.average ? (state.demoMode ? "Illustrative sample · " : "") + metrics.average + "-day average" : tr("noEstimate"));
    setText("average-cycle",metrics.average ? metrics.average + (state.language === "bn" ? " দিন" : " days") : "—");
    if (metrics.next) {
      setText("next-period-date",formatDate(metrics.next,false));
      const diff = dayDiff(today, metrics.next);
      setText("next-period-countdown",diff > 0 ? interpolate(tr("nextIn"),{n:diff}) : diff === 0 ? tr("nextToday") : tr("nextPast"));
    } else {
      setText("next-period-date","—");
      setText("next-period-countdown",tr("noEstimate"));
    }
  }
  function renderCalendar() {
    const label = $("#calendar-month-label");
    const grid = $("#calendar-grid");
    if (!label || !grid) return;
    label.textContent = formatMonth(calendarMonth);
    const year = calendarMonth.getFullYear();
    const month = calendarMonth.getMonth();
    const first = new Date(year,month,1);
    const offset = (first.getDay() + 6) % 7;
    const days = new Date(year,month+1,0).getDate();
    const metrics = cycleMetrics();
    let html = "";
    for (let i = 0; i < offset; i++) html += '<span class="calendar-day is-outside" aria-hidden="true"></span>';
    for (let day = 1; day <= days; day++) {
      const iso = localDateISO(new Date(year,month,day));
      const entry = state.periods.find(item => iso >= item.start && iso <= (item.end || item.start));
      let kind = entry ? (entry.kind === "spotting" ? "is-spotting" : "is-period") : "";
      if (!entry && metrics.next && Math.abs(dayDiff(metrics.next,iso)) <= 1) kind = "is-estimated";
      const todayClass = iso === today ? " is-today" : "";
      const selectedClass = iso === selectedCalendarDate ? " is-selected" : "";
      html += '<button type="button" class="calendar-day ' + kind + todayClass + selectedClass + '" data-calendar-date="' + iso + '" aria-label="' + escapeHTML(formatDate(iso,true)) + '"><span>' + day + '</span>' + (kind ? '<i class="day-mark"></i>' : '') + '</button>';
    }
    const trailing = (7 - ((offset + days) % 7)) % 7;
    for (let i = 0; i < trailing; i++) html += '<span class="calendar-day is-outside" aria-hidden="true"></span>';
    grid.innerHTML = html;
    const selected = selectedCalendarDate;
    const selectedEntry = state.periods.find(item => selected >= item.start && selected <= (item.end || item.start));
    let detail = tr("calendarEmpty");
    if (selectedEntry) detail = selectedEntry.kind === "spotting" ? tr("calendarSpotting") : tr("calendarPeriod");
    else if (metrics.next && Math.abs(dayDiff(metrics.next,selected)) <= 1) detail = tr("calendarEstimate");
    setText("selected-day-detail", interpolate(tr("dayEntry"),{date:formatDate(selected,true),detail}));
    const weekdays = $("#calendar-weekdays");
    if (weekdays) {
      const names = state.language === "bn" ? ["সোম","মঙ্গল","বুধ","বৃহ","শুক্র","শনি","রবি"] : ["Mon","Tue","Wed","Thu","Fri","Sat","Sun"];
      weekdays.innerHTML = names.map(name => "<span>" + name + "</span>").join("");
    }
    setText("last-period-start",metrics.last ? formatDate(metrics.last) : tr("noLastPeriod"));
    setText("tracker-average",metrics.average ? metrics.average + (state.language === "bn" ? " দিন" : " days") : "—");
    setText("tracker-next",metrics.next ? formatDate(metrics.next) : "—");
  }
  function renderHistory() {
    const target = $("#period-history");
    if (!target) return;
    const items = [...state.periods].sort((a,b) => b.start.localeCompare(a.start)).slice(0,5);
    if (!items.length) { target.innerHTML = '<div class="empty-state">' + escapeHTML(tr("noPeriods")) + '</div>'; return; }
    target.innerHTML = items.map(item => {
      const label = item.kind === "spotting" ? tr("spottingEntry") : tr("periodEntry");
      const range = item.end && item.end !== item.start ? formatDate(item.start) + " – " + formatDate(item.end) : formatDate(item.start);
      return '<div class="entry-item"><span class="entry-icon">' + (item.kind === "spotting" ? "·" : "◷") + '</span><span class="entry-copy"><strong class="sensitive-value">' + escapeHTML(label) + '</strong><small class="sensitive-value">' + escapeHTML(range) + (item.note ? " · " + escapeHTML(item.note) : "") + '</small></span><span class="entry-tag">' + escapeHTML(state.demoMode && item.id.startsWith("sample") ? tr("sampleTag") : tr("savedTag")) + '</span></div>';
    }).join("");
  }
  function renderDiary() {
    const symptomTarget = $("#symptom-list");
    const wellnessTarget = $("#wellness-list");
    const symptoms = [...state.symptoms].sort((a,b) => b.date.localeCompare(a.date)).slice(0,5);
    const wellness = [...state.wellness].sort((a,b) => b.date.localeCompare(a.date)).slice(0,5);
    if (symptomTarget) symptomTarget.innerHTML = symptoms.length ? symptoms.map(item =>
      '<div class="entry-item"><span class="entry-icon">♡</span><span class="entry-copy"><strong class="sensitive-value">' + escapeHTML(item.name) + '</strong><small class="sensitive-value">' + escapeHTML(formatDate(item.date)) + (item.note ? " · " + escapeHTML(item.note) : "") + '</small></span><span class="entry-tag">' + escapeHTML(tr(item.severity.toLowerCase())) + '</span></div>'
    ).join("") : '<div class="empty-state">' + escapeHTML(tr("noSymptoms")) + '</div>';
    if (wellnessTarget) wellnessTarget.innerHTML = wellness.length ? wellness.map(item =>
      '<div class="entry-item"><span class="entry-icon">☼</span><span class="entry-copy"><strong class="sensitive-value">' + escapeHTML(item.mood) + '</strong><small class="sensitive-value">' + escapeHTML(formatDate(item.date)) + " · " + escapeHTML(item.sleep) + "h sleep · " + escapeHTML(item.energy) + '</small></span><span class="entry-tag">' + escapeHTML(tr("savedTag")) + '</span></div>'
    ).join("") : '<div class="empty-state">' + escapeHTML(tr("noWellness")) + '</div>';
  }
  function renderArticles() {
    const target = $("#article-grid");
    if (!target) return;
    const filtered = ARTICLES.filter(article => articleFilter === "all" || article.category === articleFilter);
    target.innerHTML = filtered.map(article => {
      const title = tr(article.titleKey);
      const summary = tr(article.summaryKey);
      return '<article class="article-card"><div class="article-topline"><span class="article-category">' + escapeHTML(article.category === "Menstrual" ? tr("filterCycle") : article.category === "Intimate" ? tr("filterIntimate") : article.category) + '</span><span class="article-readtime">' + article.read + " " + escapeHTML(tr("minutes")) + '</span></div><h3>' + escapeHTML(title) + '</h3><p>' + escapeHTML(summary) + '</p><button type="button" data-read-article="' + article.id + '">' + escapeHTML(tr("readGuide")) + ' <span aria-hidden="true">→</span></button></article>';
    }).join("");
  }
  function showToast(message) {
    const toast = $("#toast");
    toast.textContent = message;
    toast.classList.add("is-visible");
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => toast.classList.remove("is-visible"), 2800);
  }
  function openDialog(title, body, eyebrow = "NARIBONDHU") {
    setText("dialog-title", title);
    setText("dialog-eyebrow", eyebrow);
    $("#dialog-content").innerHTML = body;
    const dialog = $("#app-dialog");
    if (!dialog.open) dialog.showModal();
  }
  function closeDialog() {
    const dialog = $("#app-dialog");
    if (dialog.open) dialog.close();
  }
  function confirmOwnData(entryType) {
    openDialog(tr("startWithOwnTitle"),
      '<div class="dialog-copy"><p>' + escapeHTML(tr("startWithOwnText")) + '</p></div><div class="form-actions"><button class="outline-button" type="button" data-action="keep-preview">' + escapeHTML(tr("keepPreview")) + '</button><button class="primary-button" type="button" data-action="confirm-own-data" data-entry-type="' + entryType + '">' + escapeHTML(tr("clearSamples")) + '</button></div>');
  }
  function requestEntry(type) {
    if (state.demoMode) { confirmOwnData(type); return; }
    if (type === "period") openPeriodForm();
    if (type === "symptom") openSymptomForm();
    if (type === "wellness") openWellnessForm();
  }
  function openPeriodForm() {
    openDialog(tr("addPeriodTitle"),
      '<form class="dialog-form" id="period-form"><div class="form-field"><label for="period-kind">' + escapeHTML(tr("kindLabel")) + '</label><select id="period-kind" name="kind"><option value="period">' + escapeHTML(tr("periodOption")) + '</option><option value="spotting">' + escapeHTML(tr("spottingOption")) + '</option></select></div><div class="form-row"><div class="form-field"><label for="period-start">' + escapeHTML(tr("startDateLabel")) + '</label><input id="period-start" name="start" type="date" max="' + today + '" value="' + today + '" required></div><div class="form-field"><label for="period-end">' + escapeHTML(tr("endDateLabel")) + '</label><input id="period-end" name="end" type="date" max="' + today + '" value=""></div></div><small class="form-hint">' + escapeHTML(tr("endDateHint")) + '</small><div class="form-field"><label for="period-note">' + escapeHTML(tr("noteLabel")) + '</label><textarea id="period-note" name="note" maxlength="240" placeholder="' + escapeHTML(tr("notePlaceholder")) + '"></textarea></div><div class="form-actions"><button class="outline-button" type="button" data-action="close-dialog">' + escapeHTML(tr("cancel")) + '</button><button class="primary-button" type="submit">' + escapeHTML(tr("saveEntry")) + '</button></div></form>');
  }
  function openSymptomForm() {
    const options = state.language === "bn" ? ["পেটব্যথা/ক্র্যাম্প","পেট ফাঁপা","মাথাব্যথা","ক্লান্তি","মনের পরিবর্তন","ব্রণ","অন্যান্য"] : ["Cramps","Bloating","Headache","Fatigue","Mood change","Acne","Other"];
    openDialog(tr("addSymptomTitle"),
      '<form class="dialog-form" id="symptom-form"><div class="form-field"><label for="symptom-name">' + escapeHTML(tr("symptomLabel")) + '</label><select id="symptom-name" name="name">' + options.map(value => '<option>' + escapeHTML(value) + '</option>').join("") + '</select></div><div class="form-row"><div class="form-field"><label for="symptom-severity">' + escapeHTML(tr("severityLabel")) + '</label><select id="symptom-severity" name="severity"><option value="Mild">' + escapeHTML(tr("mild")) + '</option><option value="Moderate">' + escapeHTML(tr("moderate")) + '</option><option value="Severe">' + escapeHTML(tr("severe")) + '</option></select></div><div class="form-field"><label for="symptom-date">' + escapeHTML(tr("dateLabel")) + '</label><input id="symptom-date" name="date" type="date" max="' + today + '" value="' + today + '" required></div></div><div class="form-field"><label for="symptom-note">' + escapeHTML(tr("noteLabel")) + '</label><textarea id="symptom-note" name="note" maxlength="240" placeholder="' + escapeHTML(tr("notePlaceholder")) + '"></textarea></div><div class="form-actions"><button class="outline-button" type="button" data-action="close-dialog">' + escapeHTML(tr("cancel")) + '</button><button class="primary-button" type="submit">' + escapeHTML(tr("saveEntry")) + '</button></div></form>');
  }
  function openWellnessForm() {
    openDialog(tr("addWellnessTitle"),
      '<form class="dialog-form" id="wellness-form"><div class="form-row"><div class="form-field"><label for="wellness-mood">' + escapeHTML(tr("moodLabel")) + '</label><select id="wellness-mood" name="mood"><option value="' + escapeHTML(tr("moodGreat")) + '">' + escapeHTML(tr("moodGreat")) + '</option><option value="' + escapeHTML(tr("moodOkay")) + '">' + escapeHTML(tr("moodOkay")) + '</option><option value="' + escapeHTML(tr("moodLow")) + '">' + escapeHTML(tr("moodLow")) + '</option><option value="' + escapeHTML(tr("moodAnxious")) + '">' + escapeHTML(tr("moodAnxious")) + '</option></select></div><div class="form-field"><label for="wellness-energy">' + escapeHTML(tr("energyLabel")) + '</label><select id="wellness-energy" name="energy"><option value="' + escapeHTML(tr("good")) + '">' + escapeHTML(tr("good")) + '</option><option value="' + escapeHTML(tr("steady")) + '">' + escapeHTML(tr("steady")) + '</option><option value="' + escapeHTML(tr("low")) + '">' + escapeHTML(tr("low")) + '</option></select></div></div><div class="form-row"><div class="form-field"><label for="wellness-sleep">' + escapeHTML(tr("sleepLabel")) + '</label><input id="wellness-sleep" name="sleep" type="number" min="0" max="24" step=".5" value="7"></div><div class="form-field"><label for="wellness-date">' + escapeHTML(tr("dateLabel")) + '</label><input id="wellness-date" name="date" type="date" max="' + today + '" value="' + today + '" required></div></div><div class="form-field"><label for="wellness-note">' + escapeHTML(tr("noteLabel")) + '</label><textarea id="wellness-note" name="note" maxlength="240" placeholder="' + escapeHTML(tr("notePlaceholder")) + '"></textarea></div><div class="form-actions"><button class="outline-button" type="button" data-action="close-dialog">' + escapeHTML(tr("cancel")) + '</button><button class="primary-button" type="submit">' + escapeHTML(tr("saveEntry")) + '</button></div></form>');
  }
  function openArticle(id) {
    const article = ARTICLES.find(item => item.id === id);
    if (!article) return;
    const sections = article.sections.map(section => {
      const heading = state.language === "bn" ? section.hb : section.h;
      const body = state.language === "bn" ? section.bb : section.b;
      return '<h3>' + escapeHTML(heading) + '</h3><p>' + escapeHTML(body) + '</p>';
    }).join("");
    const refs = article.refs.map(ref => "<li>" + escapeHTML(ref) + "</li>").join("");
    openDialog(tr(article.titleKey),
      '<div class="dialog-copy">' + sections + '<div class="dialog-alert">' + escapeHTML(state.language === "bn" ? "এটি শিক্ষামূলক তথ্য। ব্যক্তিগত রোগ নির্ণয় বা চিকিৎসার জন্য যোগ্য চিকিৎসকের পরামর্শ নিন।" : article.note) + '</div><h3>' + escapeHTML(state.language === "bn" ? "তথ্যসূত্র (অ্যাপের কনটেন্ট ক্যাটালগে উল্লেখিত)" : "References listed in the app catalogue") + '</h3><ul>' + refs + '</ul></div>');
  }
  function openVisitChecklist() {
    const items = tr("checklistItems");
    const list = items.map(item => "<li>" + escapeHTML(item) + "</li>").join("");
    openDialog(tr("checklistTitle"),
      '<div class="dialog-copy"><p>' + escapeHTML(tr("checklistIntro")) + '</p><ul id="visit-checklist-list">' + list + '</ul><div class="dialog-alert">' + escapeHTML(tr("urgentReminder")) + '</div><div class="form-actions"><button class="outline-button" type="button" data-action="close-dialog">' + escapeHTML(tr("cancel")) + '</button><button class="primary-button" type="button" data-action="copy-checklist">' + escapeHTML(tr("copyChecklist")) + '</button></div></div>');
  }
  function clearSampleData(entryType = null) {
    state.demoMode = false;
    state.periods = [];
    state.symptoms = [];
    state.wellness = [];
    saveState();
    closeDialog();
    renderApp();
    showToast(tr("demoCleared"));
    if (entryType) requestEntry(entryType);
  }
  function clearAllData() {
    state.demoMode = false;
    state.periods = [];
    state.symptoms = [];
    state.wellness = [];
    saveState();
    closeDialog();
    renderApp();
    showToast(tr("dataCleared"));
  }
  function exportData() {
    const payload = {
      app:"NariBondhu web preview",
      exportedAt:new Date().toISOString(),
      notice:"This file may contain personal health information. Store it privately.",
      data:{periods:state.periods,symptoms:state.symptoms,wellness:state.wellness}
    };
    const blob = new Blob([JSON.stringify(payload,null,2)],{type:"application/json"});
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = "naribondhu-local-data.json";
    document.body.appendChild(link);
    link.click();
    link.remove();
    setTimeout(() => URL.revokeObjectURL(url),1000);
    showToast(tr("exportReady") + " " + tr("exportWarning"));
  }
  function submitForm(form) {
    const data = new FormData(form);
    if (form.id === "period-form") {
      const kind = String(data.get("kind"));
      const start = String(data.get("start"));
      const end = String(data.get("end") || start);
      if (end && end < start) { showToast(state.language === "bn" ? "শেষের তারিখ শুরুর তারিখের আগে হতে পারে না।" : "The end date cannot be before the start date."); return; }
      state.periods.push({id:uid(),kind,start,end:kind === "period" ? end : (data.get("end") ? end : start),note:String(data.get("note") || "").trim()});
      state.demoMode = false;
      saveState(); closeDialog(); renderApp(); showToast(tr("periodSaved"));
    } else if (form.id === "symptom-form") {
      state.symptoms.push({id:uid(),name:String(data.get("name")),severity:String(data.get("severity")),date:String(data.get("date")),note:String(data.get("note") || "").trim()});
      state.demoMode = false;
      saveState(); closeDialog(); renderApp(); showToast(tr("symptomSaved"));
    } else if (form.id === "wellness-form") {
      state.wellness.push({id:uid(),mood:String(data.get("mood")),energy:String(data.get("energy")),sleep:String(data.get("sleep")),date:String(data.get("date")),note:String(data.get("note") || "").trim()});
      state.demoMode = false;
      saveState(); closeDialog(); renderApp(); showToast(tr("wellnessSaved"));
    }
  }
  function handleAction(action, target) {
    if (action === "add-period") requestEntry("period");
    else if (action === "add-symptom") requestEntry("symptom");
    else if (action === "add-wellness") requestEntry("wellness");
    else if (action === "start-own-data") clearSampleData();
    else if (action === "confirm-own-data") clearSampleData(target.dataset.entryType);
    else if (action === "keep-preview" || action === "close-dialog") closeDialog();
    else if (action === "calendar-prev") { calendarMonth = new Date(calendarMonth.getFullYear(),calendarMonth.getMonth()-1,1); renderCalendar(); }
    else if (action === "calendar-next") { calendarMonth = new Date(calendarMonth.getFullYear(),calendarMonth.getMonth()+1,1); renderCalendar(); }
    else if (action === "visit-checklist") openVisitChecklist();
    else if (action === "copy-checklist") {
      const text = tr("checklistItems").map((item,index) => (index+1) + ". " + item).join("\n");
      if (navigator.clipboard && navigator.clipboard.writeText) navigator.clipboard.writeText(text).then(() => showToast(tr("checklistCopied"))).catch(() => showToast(tr("checklistCopyFail")));
      else showToast(tr("checklistCopyFail"));
    }
    else if (action === "ai-safety") openDialog(tr("aiSafetyTitle"),'<div class="dialog-copy"><p>' + escapeHTML(tr("aiSafetyText")) + '</p><div class="dialog-alert">' + escapeHTML(tr("urgentReminder")) + '</div></div>');
    else if (action === "export-data") exportData();
    else if (action === "reset-data") openDialog(tr("clearConfirmTitle"),'<div class="dialog-copy"><p>' + escapeHTML(tr("clearConfirmText")) + '</p><div class="form-actions"><button class="outline-button" type="button" data-action="close-dialog">' + escapeHTML(tr("cancel")) + '</button><button class="danger-button" type="button" data-action="confirm-clear">' + escapeHTML(tr("confirmClear")) + '</button></div></div>');
    else if (action === "confirm-clear") clearAllData();
  }

  document.addEventListener("click", event => {
    const routeTarget = event.target.closest("[data-route]");
    if (routeTarget) {
      event.preventDefault();
      setRoute(routeTarget.dataset.route);
      return;
    }
    const actionTarget = event.target.closest("[data-action]");
    if (actionTarget) {
      handleAction(actionTarget.dataset.action, actionTarget);
      return;
    }
    const calendarTarget = event.target.closest("[data-calendar-date]");
    if (calendarTarget) {
      selectedCalendarDate = calendarTarget.dataset.calendarDate;
      renderCalendar();
      return;
    }
    const articleTarget = event.target.closest("[data-read-article]");
    if (articleTarget) { openArticle(articleTarget.dataset.readArticle); return; }
    const filterTarget = event.target.closest("[data-filter]");
    if (filterTarget) {
      articleFilter = filterTarget.dataset.filter;
      $$(".filter-chip").forEach(button => button.classList.toggle("is-selected",button === filterTarget));
      renderArticles();
    }
  });

  document.addEventListener("submit", event => {
    if (event.target.matches("#period-form,#symptom-form,#wellness-form")) {
      event.preventDefault();
      submitForm(event.target);
    }
  });

  $("#language-toggle").addEventListener("click", () => {
    state.language = state.language === "en" ? "bn" : "en";
    saveState(); renderApp();
  });
  $("#profile-language-toggle").addEventListener("click", () => {
    state.language = state.language === "en" ? "bn" : "en";
    saveState(); renderApp();
  });
  $("#discreet-toggle").addEventListener("click", () => {
    state.discreetMode = !state.discreetMode;
    saveState(); renderApp();
  });
  $("#profile-discreet-toggle").addEventListener("click", () => {
    state.discreetMode = !state.discreetMode;
    saveState(); renderApp();
  });
  $("#app-dialog").addEventListener("click", event => {
    if (event.target === $("#app-dialog")) closeDialog();
  });
  window.addEventListener("hashchange", () => renderApp());
  if (!location.hash) history.replaceState(null,"","#home");
  renderApp();
})();