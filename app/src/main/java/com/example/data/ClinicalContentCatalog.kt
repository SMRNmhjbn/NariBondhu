package com.example.data

object ClinicalContentCatalog {
    val articles: List<EducationalArticle> = listOf(
        EducationalArticle(
            articleId = "NB-PCOS-001",
            category = "PCOS",
            topicEn = "Understanding PCOS & Insulin Resistance",
            topicBn = "পিসিওএস (PCOS) এবং ইনসুলিন রেজিস্ট্যান্স বোঝা",
            summaryEn = "Polycystic Ovary Syndrome (PCOS) is a common hormonal condition affecting ovulation, cycle regularity, and metabolism. Learning its signs helps you prepare for a doctor visit.",
            summaryBn = "পিসিওএস (PCOS) একটি সাধারণ হরমোনজনিত অবস্থা, যা মাসিকের নিয়মিততা ও শরীরের বিপাকক্রিয়ায় প্রভাব ফেলতে পারে। এর লক্ষণগুলো জানলে ডাক্তারের সাথে কথা বলা সহজ হয়।",
            bodyEn = """
                What is PCOS?
                PCOS involves an imbalance of reproductive hormones and often how the body responds to insulin (insulin resistance). Common signs include irregular or infrequent periods, facial or body hair changes, acne, scalp hair thinning, and weight changes.

                Common Medical Investigations
                Doctors typically review your menstrual history, blood hormone tests (such as LH, FSH, testosterone, TSH, prolactin), blood sugar/insulin tests, and sometimes a pelvic ultrasound. Having ovaries with small follicles on ultrasound alone does not confirm PCOS without clinical assessment.

                Supportive Lifestyle Habits
                Balanced meals with fibre, whole grains, dal, vegetables, and lean protein, along with regular walking or light exercise and steady sleep, support metabolic health.
            """.trimIndent(),
            bodyBn = """
                পিসিওএস কী?
                পিসিওএস-এ প্রজনন হরমোনের ভারসাম্যে পরিবর্তন হয় এবং অনেক সময় শরীর ইনসুলিন হরমোনের প্রতি কম সংবেদনশীল হয়ে পড়ে। সাধারণ লক্ষণগুলোর মধ্যে রয়েছে অনিয়মিত মাসিক, মুখে বা শরীরে অতিরিক্ত লোম, ব্রণ, চুল পাতলা হওয়া বা ওজনের পরিবর্তন।

                সাধারণ ডাক্তারি পরীক্ষা
                চিকিৎসক সাধারণত আপনার মাসিকের ইতিহাস শোনেন এবং প্রয়োজনে হরমোন পরীক্ষা (LH, FSH, টেস্টোস্টেরন, TSH, প্রোল্যাক্টিন), রক্তের শর্করা পরীক্ষা এবং পেলভিক আল্ট্রাসাউন্ড করতে বলতে পারেন। শুধু আল্ট্রাসাউন্ড রিপোর্ট দেখে নিজে থেকে রোগ নির্ণয় করা যায় না।

                দৈনন্দিন সহায়ক অভ্যাস
                আঁশযুক্ত খাবার, শাকসবজি, ডাল, পরিমিত সুষম খাবার, নিয়মিত হাঁটা বা হালকা ব্যায়াম এবং পর্যাপ্ত ঘুম শরীরের ভারসাম্য রক্ষায় সাহায্য করে।
            """.trimIndent(),
            intendedAudience = "Adult women in Bangladesh (18+)",
            contentOwner = "NariBondhu Clinical Education Team",
            clinicalReviewer = "Consultant Gynaecologist & Endocrinologist (NariBondhu Advisory Group)",
            supportingReferences = listOf(
                "International Evidence-Based Guideline for PCOS (2023)",
                "WHO Women's Reproductive Health Guidance",
                "RCOG Green-top Guideline on PCOS"
            ),
            publicationDate = "2026-02-15",
            reviewDate = "2026-10-01",
            version = "1.2",
            limitationsEn = "Educational overview only. NariBondhu cannot diagnose PCOS or recommend prescription medicines.",
            limitationsBn = "এটি কেবল শিক্ষামূলক তথ্য। নারীবন্ধু পিসিওএস নির্ণয় করতে বা ওষুধের পরামর্শ দিতে পারে না।",
            escalationInstructionsEn = "Consult a verified gynaecologist via BondhuHelp if periods are absent for 3+ months or symptoms disrupt daily life.",
            escalationInstructionsBn = "৩ মাসের বেশি মাসিক বন্ধ থাকলে বা লক্ষণ তীব্র হলে বন্ধুহেল্প-এর মাধ্যমে স্ত্রীরোগ বিশেষজ্ঞের পরামর্শ নিন।"
        ),
        EducationalArticle(
            articleId = "NB-PMS-002",
            category = "PMS",
            topicEn = "Understanding PMS & Recurring Cycle Symptoms",
            topicBn = "পিএমএস (PMS) ও মাসিক-পূর্ব লক্ষণ বোঝা",
            summaryEn = "Premenstrual Syndrome (PMS) causes physical and emotional shifts in the days before bleeding starts. Tracking patterns helps distinguish mild PMS from severe symptoms.",
            summaryBn = "মাসিক শুরু হওয়ার কয়েক দিন আগে শারীরিক ও মানসিক কিছু পরিবর্তন দেখা দিলে তাকে পিএমএস বলে। লক্ষণ লিখে রাখলে নিজের ধারা বোঝা যায়।",
            bodyEn = """
                Common PMS Experiences
                Many women experience bloating, breast tenderness, mild cramps, fatigue, sleep shifts, irritability, or low mood 5 to 10 days before their period, easing shortly after bleeding begins.

                When to Seek Clinical Care
                If mood changes feel overwhelming, hopeless, or severely disrupt work, study, or relationships every cycle (which doctors evaluate for conditions like PMDD), or if pelvic pain is severe, a gynaecologist or clinical specialist can help.

                Self-Care Basics
                Warm compresses for cramps, gentle movement, reducing excess salt/caffeine during symptomatic days, and consistent sleep help many people feel steadier.
            """.trimIndent(),
            bodyBn = """
                সাধারণ পিএমএস লক্ষণ
                অনেক নারীর মাসিক শুরুর ৫ থেকে ১০ দিন আগে পেট ফাঁপা, স্তনে ব্যথা বা ভারী ভাব, হালকা তলপেটে ব্যথা, ক্লান্তি, ঘুমের পরিবর্তন বা মন খারাপ দেখা দেয় এবং মাসিক শুরু হলে তা কমে যায়।

                কখন ডাক্তারের পরামর্শ নেবেন
                যদি প্রতি মাসে মানসিক চাপ বা বিষণ্ণতা খুব তীব্র হয় এবং দৈনন্দিন কাজকর্মে বাধা সৃষ্টি করে, অথবা তলপেটের ব্যথা অসহনীয় হয়, তবে অবশ্যই বিশেষজ্ঞ চিকিৎসকের পরামর্শ নিন।

                সহজ যত্ন
                তলপেটে হালকা গরম সেঁক, পর্যাপ্ত পানি পান, অতিরিক্ত লবণ ও চা-কফি কমানো এবং নিয়মিত ঘুম স্বস্তি দিতে পারে।
            """.trimIndent(),
            intendedAudience = "Adult women experiencing premenstrual shifts",
            contentOwner = "NariBondhu Clinical Education Team",
            clinicalReviewer = "Consultant Gynaecologist & Clinical Psychologist",
            supportingReferences = listOf(
                "RCOG Management of Premenstrual Syndrome Guideline",
                "NICE Menstrual Health Guidance"
            ),
            publicationDate = "2026-02-20",
            reviewDate = "2026-10-01",
            version = "1.1",
            limitationsEn = "Distinguishes general PMS awareness from severe conditions requiring medical evaluation.",
            limitationsBn = "সাধারণ পিএমএস তথ্যের সাথে চিকিৎসকের মূল্যায়ন প্রয়োজন এমন গুরুতর অবস্থাকে আলাদাভাবে বিবেচনা করতে হবে।",
            escalationInstructionsEn = "Seek professional assessment through BondhuHelp for severe pain or mood distress.",
            escalationInstructionsBn = "তীব্র ব্যথা বা অতিরিক্ত মানসিক কষ্ট হলে বন্ধুহেল্প থেকে ডাক্তারের পরামর্শ নিন।"
        ),
        EducationalArticle(
            articleId = "NB-MEN-003",
            category = "Menstrual",
            topicEn = "Why Cycles Vary & Spotting vs. Menstruation",
            topicBn = "মাসিকের চক্র কেন বদলায় এবং স্পটিং বনাম মাসিক",
            summaryEn = "Healthy cycles are not always 28 days. Stress, illness, travel, and hormonal shifts can lengthen or shorten a cycle.",
            summaryBn = "সবার মাসিক ২৮ দিনের হয় না। মানসিক চাপ, অসুস্থতা বা হরমোনের পরিবর্তনে চক্র ছোট বা বড় হতে পারে।",
            bodyEn = """
                Normal Cycle Variation
                Adult menstrual cycles commonly range from 21 to 35 days, and individual cycles naturally vary by a few days from month to month. Counting starts on the first day of full menstrual bleeding (Day 1) until the day before the next period starts.

                Spotting vs. Menstruation
                Spotting is very light, brief bleeding or pinkish-brown marks between periods that does not require full pad changes. Do not count spotting as Day 1 of your period—log it separately so your cycle statistics stay accurate.

                Warning Signs
                Soaking through pads every hour for consecutive hours, dizziness/fainting, bleeding after menopause, or repeated bleeding between periods require prompt medical evaluation.
            """.trimIndent(),
            bodyBn = """
                স্বাভাবিক চক্রের পার্থক্য
                প্রাপ্তবয়স্ক নারীদের মাসিক চক্র সাধারণত ২১ থেকে ৩৫ দিনের মধ্যে হতে পারে এবং প্রতি মাসে কয়েক দিন আগে-পরে হওয়া স্বাভাবিক। পূর্ণ রক্তপাত শুরুর প্রথম দিন থেকে পরবর্তী মাসিকের আগের দিন পর্যন্ত চক্রের দৈর্ঘ্য গণনা করা হয়।

                স্পটিং বনাম পূর্ণ মাসিক
                দুই মাসিকের মাঝে খুব হালকা দাগ বা অল্প রক্তপাত হলে তাকে স্পটিং বলে। এটিকে নতুন মাসিকের প্রথম দিন হিসেবে গণনা করবেন না—নিরাপদ হিসাবের জন্য আলাদাভাবে 'স্পটিং' হিসেবে লিখে রাখুন।

                সতর্কতামূলক লক্ষণ
                প্রতি ঘণ্টায় প্যাড ভিজে যাওয়া, মাথা ঘোরা বা অজ্ঞান হয়ে যাওয়া, বা বারবার অনিয়মিত রক্তপাত হলে দ্রুত চিকিৎসকের শরণাপন্ন হন।
            """.trimIndent(),
            intendedAudience = "General adult female population",
            contentOwner = "NariBondhu Clinical Education Team",
            clinicalReviewer = "Consultant Gynaecologist (BMDC Registered)",
            supportingReferences = listOf(
                "FIGO Abnormal Uterine Bleeding Systems",
                "WHO Sexual and Reproductive Health"
            ),
            publicationDate = "2026-03-01",
            reviewDate = "2026-10-05",
            version = "1.0",
            limitationsEn = "Calendar calculations never establish ovulation or fertility status.",
            limitationsBn = "ক্যালেন্ডার হিসাব কখনোই ডিম্বস্ফোটন বা গর্ভধারণের সময় নিশ্চিত করে না।",
            escalationInstructionsEn = "Seek urgent emergency care (999) for heavy bleeding with dizziness or fainting.",
            escalationInstructionsBn = "অতিরিক্ত রক্তপাতের সাথে মাথা ঘুরলে বা অজ্ঞান হলে দ্রুত জরুরি সেবা ৯৯৯-এ যোগাযোগ করুন।"
        ),
        EducationalArticle(
            articleId = "NB-INT-004",
            category = "Intimate",
            topicEn = "Vaginal & Vulvar Health: Discharge & Hygiene",
            topicBn = "গোপন ও প্রজনন স্বাস্থ্য: সাদা স্রাব ও পরিচ্ছন্নতা",
            summaryEn = "Clear or milky-white discharge without itching or strong odour is often normal. Knowing what changes matter protects intimate comfort.",
            summaryBn = "গন্ধহীন বা চুলকানিহীন হালকা সাদা স্রাব সাধারণত স্বাভাবিক। কখন পরিবর্তনের জন্য ডাক্তার দেখানো দরকার তা জানুন।",
            bodyEn = """
                Understanding Normal Discharge
                Healthy vaginal discharge keeps tissues clean and moist. It often looks clear, white, or slightly creamy and varies across the menstrual cycle.

                Safe Intimate Hygiene
                Wash the external vulvar area gently with clean lukewarm water daily. Avoid harsh perfumed soaps, vaginal douching, or scented sprays inside the vagina, which disrupt protective natural bacteria. Wear breathable cotton underwear and change damp pads frequently during menstruation.

                When to See a Doctor
                Thick curdy discharge, grey/yellow/green discharge, strong fishy odour, itching, burning, or pelvic discomfort may indicate an infection that needs proper medical diagnosis and treatment—never self-medicate.
            """.trimIndent(),
            bodyBn = """
                স্বাভাবিক স্রাব বোঝা
                স্বাভাবিক সাদা স্রাব শরীরকে সুরক্ষিত ও আর্দ্র রাখে। মাসিকের চক্রের বিভিন্ন সময়ে এর পরিমাণ সামান্য কম-বেশি হতে পারে।

                নিরাপদ পরিচ্ছন্নতা
                বাইরের অংশ প্রতিদিন পরিষ্কার কুসুম গরম বা সাধারণ পানি দিয়ে ধুয়ে নিন। সুগন্ধি সাবান, ডুশ বা ভেতরের অংশে রাসায়নিক ব্যবহার করবেন না। সুতি অন্তর্বাস পরুন এবং মাসিকের সময় নিয়মিত প্যাড পরিবর্তন করুন।

                কখন ডাক্তার দেখাবেন
                ঘন দইয়ের মতো বা হলুদ/সবুজ স্রাব, তীব্র দুর্গন্ধ, চুলকানি, জ্বালাপোড়া বা তলপেটে ব্যথা হলে নিজে ওষুধ না খেয়ে স্ত্রীরোগ বিশেষজ্ঞের পরামর্শ নিন।
            """.trimIndent(),
            intendedAudience = "Adult women seeking private intimate health education",
            contentOwner = "NariBondhu Clinical Education Team",
            clinicalReviewer = "Consultant Gynaecologist",
            supportingReferences = listOf(
                "WHO Guidelines for Vaginal Discharge Management",
                "NHS / RCOG Vulval Skin Care Guidance"
            ),
            publicationDate = "2026-03-10",
            reviewDate = "2026-10-10",
            version = "1.1",
            limitationsEn = "Educational hygiene information cannot diagnose infections.",
            limitationsBn = "এই তথ্য কেবল সচেতনতার জন্য, কোনো সংক্রমণ নির্ণয়ের জন্য নয়।",
            escalationInstructionsEn = "Book a private online or clinic consultation on BondhuHelp for persistent discharge or irritation.",
            escalationInstructionsBn = "দীর্ঘস্থায়ী স্রাব বা চুলকানির জন্য বন্ধুহেল্প-এ গোপনীয় অনলাইন বা সরাসরি পরামর্শ নিন।"
        ),
        EducationalArticle(
            articleId = "NB-REP-005",
            category = "Reproductive",
            topicEn = "Endometriosis, Fibroids, Fertility & Menopause",
            topicBn = "এন্ডোমেট্রিওসিস, ফাইব্রয়েড, উর্বরতা ও মেনোপজ",
            summaryEn = "Science-based explanations of common reproductive health topics across every life stage, from fertility awareness to perimenopause.",
            summaryBn = "এন্ডোমেট্রিওসিস, জরায়ুর ফাইব্রয়েড, প্রজনন স্বাস্থ্য এবং মেনোপজ সম্পর্কে বিজ্ঞানভিত্তিক সহজ ধারণা।",
            bodyEn = """
                Endometriosis & Uterine Fibroids
                Severe menstrual cramps that stop daily activities, pain during urination/bowel movements during periods, or very heavy prolonged bleeding should not be dismissed as 'normal'. Doctors evaluate for conditions such as endometriosis or uterine fibroids using clinical exams and imaging.

                Fertility & Contraception
                Cycle tracking calendars alone cannot guarantee fertile days or prevent pregnancy. Speak with a doctor to discuss evidence-based contraception or fertility evaluations tailored to you.

                Perimenopause & Menopause
                Usually beginning in the 40s, cycles may become irregular alongside hot flashes or sleep changes before periods stop for 12 consecutive months.
            """.trimIndent(),
            bodyBn = """
                এন্ডোমেট্রিওসিস ও ফাইব্রয়েড
                মাসিকের সময় অতিরিক্ত তীব্র ব্যথা যা স্বাভাবিক জীবনযাত্রা ব্যাহত করে, বা দীর্ঘদিন ভারী রক্তপাত হলে তা অবহেলা করা উচিত নয়। চিকিৎসক পরীক্ষা ও আল্ট্রাসাউন্ডের মাধ্যমে এন্ডোমেট্রিওসিস বা ফাইব্রয়েডের মতো অবস্থা যাচাই করেন।

                উর্বরতা ও জন্মনিয়ন্ত্রণ
                শুধু ক্যালেন্ডারের হিসাব দিয়ে উর্বর দিন নিশ্চিত করা বা গর্ভধারণ রোধ করা সম্ভব নয়। সঠিক পরামর্শের জন্য চিকিৎসকের সাথে কথা বলুন।

                পেরিমেনোপজ ও মেনোপজ
                সাধারণত চল্লিশের কোঠায় মাসিক অনিয়মিত হতে পারে এবং শরীরে গরম ভাব বা ঘুমের পরিবর্তন দেখা দিতে পারে। টানা ১২ মাস মাসিক না হলে মেনোপজ হয়েছে ধরা হয়।
            """.trimIndent(),
            intendedAudience = "Adult women across reproductive and midlife stages",
            contentOwner = "NariBondhu Clinical Education Team",
            clinicalReviewer = "Consultant Gynaecologist",
            supportingReferences = listOf(
                "ESHRE Endometriosis Guideline",
                "NICE Menopause Diagnosis and Management"
            ),
            publicationDate = "2026-03-18",
            reviewDate = "2026-10-12",
            version = "1.0",
            limitationsEn = "Does not evaluate individual fertility status or recommend contraceptives.",
            limitationsBn = "ব্যক্তিগত গর্ভধারণ-ক্ষমতা বা জন্মনিয়ন্ত্রণ পদ্ধতি নির্ধারণ করে না।",
            escalationInstructionsEn = "Consult a verified BondhuHelp specialist for debilitating cramps or midlife bleeding changes.",
            escalationInstructionsBn = "তীব্র ব্যথা বা মেনোপজের সময় রক্তপাত হলে বন্ধুহেল্প-এ বিশেষজ্ঞ দেখান।"
        ),
        EducationalArticle(
            articleId = "NB-GEN-006",
            category = "General",
            topicEn = "Iron, Anaemia, Thyroid & Breast Health Awareness",
            topicBn = "আয়রন ও রক্তশূন্যতা, থাইরয়েড এবং স্তন স্বাস্থ্য সচেতনতা",
            summaryEn = "Women in Bangladesh commonly experience iron-deficiency anaemia and thyroid shifts. Learn preventive screening basics and breast awareness.",
            summaryBn = "বাংলাদেশের নারীদের মধ্যে রক্তশূন্যতা ও থাইরয়েডের সমস্যা বেশ পরিচিত। প্রতিরোধমূলক পরীক্ষা ও স্তন স্বাস্থ্য সম্পর্কে জানুন।",
            bodyEn = """
                Iron & Anaemia Awareness
                Heavy periods and low dietary iron can cause tiredness, dizziness, pale skin, or shortness of breath on stairs. Iron-rich foods (spinach/lal shak, lentils, eggs, fish, liver, paired with vitamin C like lemon/amloki) support iron intake, while blood tests (CBC, ferritin) confirm anaemia.

                Thyroid & Hormonal Health
                An underactive or overactive thyroid can affect menstrual regularity, energy, mood, and weight. A simple TSH blood test ordered by a doctor checks thyroid balance.

                Breast Health & Preventive Screening
                Know what feels normal for your breasts. Seek a clinical check if you notice a new lump, skin dimpling, nipple discharge, or persistent one-sided pain. Ask your doctor about age-appropriate cervical and breast screening.
            """.trimIndent(),
            bodyBn = """
                রক্তশূন্যতা ও আয়রন
                অতিরিক্ত রক্তপাত ও খাদ্যে আয়রনের ঘাটতি থেকে ক্লান্তি, মাথা ঘোরা বা দুর্বলতা হতে পারে। লালশাক, পালংশাক, ডাল, ডিম, মাছ ও ভিটামিন-সি যুক্ত খাবার (লেবু, আমলকী) আয়রন শোষণে সাহায্য করে। রক্তের CBC পরীক্ষায় রক্তশূন্যতা ধরা পড়ে।

                থাইরয়েড ও হরমোন
                থাইরয়েড হরমোনের তারতম্য মাসিকের অনিয়ম, ক্লান্তি ও ওজনের পরিবর্তনের কারণ হতে পারে। ডাক্তারের পরামর্শে সাধারণ TSH রক্ত পরীক্ষা করা হয়।

                স্তন স্বাস্থ্য ও প্রতিরোধমূলক পরীক্ষা
                স্তনে নতুন কোনো চাকা, চামড়া কুঁচকে যাওয়া বা অস্বাভাবিক পরিবর্তন দেখলে দেরি না করে চিকিৎসকের পরামর্শ নিন।
            """.trimIndent(),
            intendedAudience = "General adult women in Bangladesh",
            contentOwner = "NariBondhu Preventive Health Team",
            clinicalReviewer = "Consultant Gynaecologist & Registered Dietitian",
            supportingReferences = listOf(
                "WHO Global Anaemia Guidance",
                "DGHS Bangladesh National Cervical & Breast Cancer Screening Strategy"
            ),
            publicationDate = "2026-04-02",
            reviewDate = "2026-10-15",
            version = "1.1",
            limitationsEn = "Preventive awareness guide; does not replace laboratory diagnosis or screening exams.",
            limitationsBn = "এটি কেবল প্রতিরোধমূলক সচেতনতা; ল্যাব পরীক্ষা বা ডাক্তারি পরীক্ষার বিকল্প নয়।",
            escalationInstructionsEn = "Schedule a preventive check-up via BondhuHelp if you notice a breast lump or persistent fatigue.",
            escalationInstructionsBn = "স্তনে চাকা বা দীর্ঘস্থায়ী দুর্বলতা থাকলে বন্ধুহেল্প-এর মাধ্যমে ডাক্তার দেখান।"
        ),
    )

    val categories: List<String> = listOf("All", "PCOS", "PMS", "Menstrual", "Intimate", "Reproductive", "General")
}
