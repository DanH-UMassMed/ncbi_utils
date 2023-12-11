import csv

state_codes = ["AL","AK","AZ","AR","CA","CO","CT","DE","FL","GA","HI","ID","IL","IN","IA","KS","KY","LA","ME","MD","MA","MI","MN","MS","MO","MT","NE","NV","NH","NJ","NM","NY","NC","ND","OH","OK","OR","PA","RI","SC","SD","TN","TX","UT","VT","VA","WA","WV","WI","WY"]
countries = ["Argentina","Australia","Austria","Canada","Chile","China","Denmark","Finland","France","Germany","Italy","Japan","Singapore","Sweden","Switzerland","Taiwan","The Netherlands","United Kingdom"
]
def parse_affiliation(affiliation):
    country = None
    state = None
    last_word = affiliation.split(', ')[-1]
    first_word = affiliation.split(', ')[0]
    #print(first_word)
    if last_word in state_codes:
        country = "United States"
        state = last_word

    if last_word == 'United States' or last_word == 'USA':
        country = "United States"

    if last_word in countries:
        country = last_word

    if not country:
        print(last_word)

    #     parts = affiliation.split(', ')
    #     if len(parts) >= 2:
    #         state = parts[-2]  # Extracting state for US-based affiliations
    
    return country, first_word


def read_csv_print_columns(file_path):
    try:
        count = 1
        with open(file_path, newline='') as csvfile:
            reader = csv.DictReader(csvfile)
            for row in reader:
                year = row['year']
                attendee = row['attendee']
                affiliation = row['affiliation']
                role = row['role']
                session = row['session']
                title = row['title']
                
                country, school = parse_affiliation(affiliation)
                #if country and state:
                #    print(f"{count}: {affiliation}, || {country}, {state}")
                #    count +=1

                print(f"{year},{attendee},\"{school}\",{country},{role},{session},\"{title}\"")


    except FileNotFoundError:
        print(f"File '{file_path}' not found.")


if __name__ == "__main__":
    print("year,attendee,affiliation,country,role,session,title")
    read_csv_print_columns('lipids_conference_agenda.csv')
