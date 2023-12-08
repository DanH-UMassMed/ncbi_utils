import requests
from urllib.parse import urlencode

base_url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/"
search_endpoint = "esearch.fcgi"
fetch_endpoint = "efetch.fcgi"

# Define parameters for the search
params = {
    "db": "pubmed",
    "term": "all[journal]",
    "retmode": "json"
}

# Perform the search
search_response = requests.get(f"{base_url}{search_endpoint}?{urlencode(params)}")

# Check if the search was successful
if search_response.status_code == 200:
    search_results = search_response.json()
    id_list = search_results.get("esearchresult", {}).get("idlist", [])

    # Fetch journal names using the IDs obtained from the search
    if id_list:
        id_string = ",".join(id_list)
        fetch_params = {
            "db": "pubmed",
            "id": id_string,
            "retmode": "json",
            "rettype": "uilist"
        }
        fetch_response = requests.get(f"{base_url}{fetch_endpoint}?{urlencode(fetch_params)}")

        if fetch_response.status_code == 200:
            fetch_results = fetch_response.json()
            journal_list = fetch_results.get("result", {}).get("uids", {}).values()

            # Print the unique journal names
            unique_journals = set(journal_list)
            for journal in unique_journals:
                print(journal)
        else:
            print("Failed to fetch journal names.")
    else:
        print("No IDs found from the search.")
else:
    print("Search request failed.")
