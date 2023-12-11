# Create the affiliation column in the lipids_conferences CSV file
# NOTE: The original file is no longer available as it was removed after updating
# The code is left as a potential accelerator 

import csv
import html
import sys

def read_file_lines(file_path):
    try:
        with open(file_path, 'r') as file:
            # Read the file content and split it into lines
            lines = file.readlines()
            # Remove newline characters from each line
            lines = [line.strip() for line in lines]
            return lines
    except FileNotFoundError:
        print(f"File '{file_path}' not found.")
        return []

def get_affiliation(text,match):
    import re
    # Regular expression pattern to match text inside parentheses
    pattern = r'\((.*?)\)'

    # Start the search after the namexs
    name_pos = text.find(match)
    text = text[name_pos + len(match):]
    
    found = None
    matches = re.findall(pattern, text)
    if len(matches)>=1:
        found = matches[0]
    else:
        sys.stderr.write(f"Found none {text}\n")
        found = None


    # Return matches as a list
    return found

def find_name(file_rows, match):
    #print(file_rows)
    matches = [ (match, get_affiliation(row, match)) for row in file_rows if match in row]
    if(len(matches)>1):
        for match in matches:
            if match[1] != None:
                return [match] 
    return matches
    
def read_csv_print_columns(file_path, conference_data):
    try:
        with open(file_path, newline='') as csvfile:
            reader = csv.DictReader(csvfile)
            for row in reader:
                year = row['year']
                attendee = row['attendee']
                role = row['role']
                session = row['session']
                title = row['title']

                year_data = conference_data[year]
                result = find_name(year_data, attendee)
                if len(result):
                    affiliation = result[0][1]
                    affiliation = html.unescape(affiliation)
                    print(f"{year},{attendee},\"{affiliation}\",{role},{session},\"{title}\"")
                else:
                    sys.stderr.write(f"No Affiliation found for '{attendee}' in year {year}.\n")

    except FileNotFoundError:
        print(f"File '{file_path}' not found.")


years = ['2023','2019','2017','2015','2013']
conference_data = {f"{year}":read_file_lines(f"./web_pages/{year}.html") for year in years}
#print(conference_data.keys())

file_path = "lipids_conference.csv"
sys.stderr.write(f"Starting\n")
print("year,attendee,affiliation,role,session,title")
read_csv_print_columns(file_path, conference_data)

#sed -i '1i\year,name,affiliation' names.csv