/************************************************************************************************
import java.net.HttpURLConnection
import java.io.DataOutputStream

def URL = new URL('https://orcid.org/oauth/token')
def clientID = 'APP-XXXXXX'
def clientSecret = 'XXXXXXXX'

def connection = URL.openConnection()
connection.setRequestMethod('POST')
connection.setRequestProperty('Accept', 'application/json')
connection.setDoOutput(true)

def params = "client_id=${URLEncoder.encode(clientID, 'UTF-8')}" +
             "&client_secret=${URLEncoder.encode(clientSecret, 'UTF-8')}" +
             "&grant_type=client_credentials" +
             "&scope=/read-public"

def outputStream = new DataOutputStream(connection.getOutputStream())
outputStream.writeBytes(params)
outputStream.flush()
outputStream.close()

def responseCode = connection.getResponseCode()
def response = new StringBuffer()

if (responseCode == HttpURLConnection.HTTP_OK) {
    def reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))
    String line
    while ((line = reader.readLine()) != null) {
        response.append(line)
    }
    reader.close()
} else {
    println "Failed to make a POST request. Response code: $responseCode"
}

println "Response data: ${response.toString()}"
************************************************************************************************/


import java.net.HttpURLConnection
import java.net.URL

def accessToken = System.getenv("ORCID_ACCESS_TOKEN") 
def orcidID = "0000-0002-4660-354X"

def urlString = "https://pub.orcid.org/v3.0/${orcidID}"
URL url = new URL(urlString)

HttpURLConnection connection = (HttpURLConnection) url.openConnection()
connection.setRequestProperty("Accept", "application/vnd.orcid+xml")
connection.setRequestProperty("Authorization type and Access token", "Bearer ${accessToken}")
connection.setRequestMethod("GET")

int responseCode = connection.getResponseCode()

if (responseCode == HttpURLConnection.HTTP_OK) {
    def inputStream = connection.getInputStream()
    def reader = new BufferedReader(new InputStreamReader(inputStream))
    def response = new StringBuffer()
    String line

    while ((line = reader.readLine()) != null) {
        response.append(line)
    }
    reader.close()
    println "Response: ${response.toString()}"
} else {
    println "Failed to fetch data. Response Code: ${responseCode}"
}

connection.disconnect()
