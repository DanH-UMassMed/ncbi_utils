data = [
    (32905776, 195602, 1),
    (32880370, 226126, 1),
    (31420446, 332696, 1),
    (32880370, 332696, 1),
    (31420446, 336093, 1)
]

target_counts = {}

for item in data:
    target = item[1]
    if target in target_counts:
        target_counts[target] += 1
    else:
        target_counts[target] = 1

# Filter targets with more than one match
multiple_matches = [target for target, count in target_counts.items() if count > 1]

print("Targets with more than one match:", multiple_matches)
