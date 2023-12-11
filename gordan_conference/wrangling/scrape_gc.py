# Scrape the web site https://www.grc.org/molecular-and-cellular-biology-of-lipids-conference
# get the full agenda for the years requested
# do some basic parsing of the data to create a csv file

import requests
from bs4 import BeautifulSoup

years=[2023,2019,2017]

year=2019
attendee=None
role=None
title=None

for year in years:
    # URL of the webpage
    url = f"https://www.grc.org/molecular-and-cellular-biology-of-lipids-conference/{year}/"

    # Send a GET request to the URL
    response = requests.get(url)

    # Parse the HTML content using Beautiful Soup
    soup = BeautifulSoup(response.content, "html.parser")

    # Find all divs with data-oldclass="Full Speaker Title"
    speaker_title_divs = soup.find_all(lambda tag: tag.name == 'div' and 
                                         tag.get('data-oldclass', '').lower() == 'full speaker title')
    role="speaker"
    if speaker_title_divs:
        for speaker_div in speaker_title_divs:
            # Find the div with class="session-names" inside each speaker div
            session_names_div = speaker_div.find("div", class_="session-names")
            
            if session_names_div:
                # Find the speaker's name inside the session names div
                speaker_name = session_names_div.find("strong").get_text(strip=True)
                attendee= speaker_name
            
                
            # Find the talk's title inside the span with class="speaker"
            talk_title = speaker_div.find("span", class_="speaker").get_text(strip=True)
            title=talk_title
            
                
            print(f"{year},{attendee},{role},{title}")  # Separating each speaker's details
            
    else:
        print("No divs with data-oldclass='Full Speaker Title' found on the webpage.")

    # Find all divs with data-oldclass="Full Speaker Title"
    session_title_divs = soup.find_all(lambda tag: tag.name == 'div' and 
                                         tag.get('data-oldclass', '').lower() == 'full session title')
    role="session"
    if session_title_divs:
        for session_div in session_title_divs:
            # Find the div with class="session-names" inside each speaker div
            session_names_div = session_div.find("div", class_="session-names")
            
            if session_names_div:
                # Find the speaker's name inside the session names div
                speaker_name = session_names_div.find("strong").get_text(strip=True)
                attendee = speaker_name
                
            # Find the talk's title inside the span with class="speaker"
            talk_title = session_div.find("span", class_="session").get_text(strip=True)
            title = talk_title
                
            print(f"{year},{attendee},{role},{title}")  # Separating each speaker's details
            
    else:
        print("No divs with data-oldclass='Full Speaker Title' found on the webpage.")
