import requests

def call_rest_api(url, params=None, headers=None):
    try:
        print(f"{url=}")
        response = requests.get(url, params=params, headers=headers)

        # Check if the request was successful (HTTP status code 200)
        if response.status_code == 200:
            # Parse and work with the API response, for example, print the response content
            print("API Response:")
            print(response.text)
        else:
            # If the request was not successful, print the error code and message
            print(f"Error: {response.status_code} - {response.text}")

    except Exception as e:
        # Handle exceptions, such as network errors
        print(f"An error occurred: {e}")

# Example usage

def einfo():
    api_url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/einfo.fcgi"
    params = None
    headers = None
    return api_url, params, headers 

def esearch():
    api_url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi"
    # db=pubmed&term=science[journal]+AND+breast+cancer+AND+2008[pdat]

    params = {
        "db": "pubmed",
        "term": "science[journal] AND breast cancer AND 2008[pdat]",
        "usehistory": "y",
    }
    headers = None
    return api_url, params, headers


if __name__ == "__main__":
    
    # Optional: Headers to include with the request
    headers = {
        "Content-Type": "application/xml"
    }

    api_url, params, headers = esearch()
    # Call the REST API
    call_rest_api(api_url, params=params, headers=headers)
