import requests
import os

access_token = os.getenv("ORCID_ACCESS_TOKEN")
print(access_token)
orcid_id = "0000-0002-2403-8551"
#url = f"https://api.orcid.org/v3.0/{orcid_id}/record"
#url = f"https://orcid.org/v3.0/{orcid_id}/record"
url = f"https://pub.orcid.org/v3.0/{orcid_id}"

# headers = {
#     "Accept": "application/vnd.orcid+xml",
#     "Authorization type and Access token": f"Bearer {access_token}"
# }

headers = {
    "Content-type": "application/vnd.orcid+xml",
    "Authorization": f"Bearer {access_token}"
}

try:
    response = requests.get(url, headers=headers)
    if response.status_code == 200:
        print(f"Response: {response.text}")
    else:
        print(f"Failed to fetch data. Response Code: {response.status_code}")
except requests.RequestException as e:
    print(f"An error occurred: {e}")
