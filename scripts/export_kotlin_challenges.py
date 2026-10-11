import json

with open("challenges.json") as f:
    challenges = json.load(f)

def escape(s):
    return s.replace('\\', '\\\\').replace('"', '\\"').replace('$', '\\$')

def chunks(lst, n):
    for i in range(0, len(lst), n):
        yield lst[i:i + n]

part_files = []
chunk_size = 30
chunked = list(chunks(challenges, chunk_size))

for part_idx, chunk in enumerate(chunked):
    file_name = f"app/src/main/java/com/svamaster/data/challenges/SvaChallengesPart{part_idx}.kt"
    part_files.append(f"SvaChallengesPart{part_idx}.list")
    
    code = [
        "package com.svamaster.data.challenges",
        "",
        "import com.svamaster.model.SvaCategory",
        "import com.svamaster.model.SvaPracticeChallenge",
        "",
        f"object SvaChallengesPart{part_idx} {{",
        "    val list: List<SvaPracticeChallenge> = listOf("
    ]

    for item in chunk:
        tokens_code = ", ".join([f'"{escape(t)}"' for t in item["tokens"]])
        distractors_code = ", ".join([f'"{escape(d)}"' for d in item["distractors"]])
        acceptables_code = ", ".join([f'"{escape(a)}"' for a in item["acceptableAnswers"]])

        item_str = f"""        SvaPracticeChallenge(
            id = "{item['id']}",
            prompt = "{escape(item['prompt'])}",
            targetSva = "{escape(item['targetSva'])}",
            acceptableAnswers = listOf({acceptables_code}),
            tokens = listOf({tokens_code}),
            distractors = listOf({distractors_code}),
            explanation = "{escape(item['explanation'])}",
            category = SvaCategory.{item['category']},
            difficulty = "{item['difficulty']}"
        ),"""
        code.append(item_str)

    code.append("    )")
    code.append("}")
    code.append("")

    with open(file_name, "w") as out:
        out.write("\n".join(code))

print(f"Generated {len(chunked)} Kotlin challenge parts.")
